/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda;

import com.contentful.java.cda.model.ArrayResource;
import com.contentful.java.cda.model.CDAArray;
import com.contentful.java.cda.model.CDAAsset;
import com.contentful.java.cda.model.CDAEntry;
import com.contentful.java.cda.model.CDALocale;
import com.contentful.java.cda.model.CDAResource;
import com.contentful.java.cda.model.CDASyncedSpace;
import com.contentful.java.cda.model.ResourceWithMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.contentful.java.cda.Constants.CDAResourceType;

/**
 * Custom {@code Callable} used internally for preparing array result objects. This attempts to
 * resolve all links within an {@code ArrayResource} by iterating through all of it's normal and
 * included resources.
 */
class ArrayParser<T extends ArrayResource> implements Callable<T> {
  private final T source;
  private final ClientContext context;

  public ArrayParser(T source, ClientContext context) {
    this.source = source;
    this.context = context;
  }

  @Override public T call() throws Exception {
    HashMap<String, CDAResource> assets = new HashMap<String, CDAResource>();
    HashMap<String, CDAResource> entries = new HashMap<String, CDAResource>();
    boolean sync = source instanceof CDASyncedSpace;

    ArrayList<CDAResource> items;

    if (sync) {
      items = ((CDASyncedSpace) source).getItems();
    } else if (source instanceof CDAArray) {
      items = ((CDAArray) source).getItems();

      CDAArray.Includes includes = ((CDAArray) source).getIncludes();

      if (includes != null) {
        List<CDAAsset> includedAssets = includes.getAssets();
        List<CDAEntry> includedEntries = includes.getEntries();

        if (includedAssets != null) {
          for (CDAResource item : includedAssets) {
            assets.put((String) item.getSys().get("id"), item);
          }
        }

        if (includedEntries != null) {
          for (CDAResource item : includedEntries) {
            entries.put((String) item.getSys().get("id"), item);
          }
        }
      }
    } else {
      throw new IllegalArgumentException("Invalid input.");
    }

    for (CDAResource item : items) {
      if (sync && item instanceof ResourceWithMap) {
        setLocalizedFields((ResourceWithMap) item);
      }

      // Store the resource in the proper array according to it's unique id.
      if (item instanceof CDAAsset) {
        assets.put((String) item.getSys().get("id"), item);
      } else if (item instanceof CDAEntry) {
        entries.put((String) item.getSys().get("id"), item);
      }
    }

    // Iterate through all entries and attempt to resolve contained links.
    for (Map.Entry<String, CDAResource> entry : entries.entrySet()) {
      CDAResource item = entry.getValue();

      if (item instanceof ResourceWithMap) {
        resolveResourceLinks(item, assets, entries);
      }
    }

    return source;
  }

  private void setLocalizedFields(ResourceWithMap res) {
    Map rawFields = res.getRawFields();
    HashMap<String, Map> localizedFieldsMap = res.getLocalizedFieldsMap();

    // Create a map for every locale
    for (CDALocale locale : context.spaceWrapper.get().getLocales()) {
      HashMap<String, Object> map = new HashMap<String, Object>();

      for (Object key : rawFields.keySet()) {
        String code = locale.getCode();
        Map item = (Map) rawFields.get(key);
        Object value = item.get(code);

        if (value != null) {
          map.put((String) key, value);
        }

        localizedFieldsMap.put(code, map);
      }
    }
  }

  /**
   * Resolves any links contained in a {@code CDAEntry} object.
   *
   * @param resource entry to resolve
   * @param assets map of assets by ids
   * @param entries map of entries by ids
   */
  @SuppressWarnings("unchecked")
  private void resolveResourceLinks(CDAResource resource,
      HashMap<String, CDAResource> assets, HashMap<String, CDAResource> entries) {
    ResourceWithMap res = (ResourceWithMap) resource;
    HashMap<String, Map> localizedFields = res.getLocalizedFieldsMap();

    for (Map fields : localizedFields.values()) {
      for (Object k : fields.keySet()) {
        Object value = fields.get(k);

        if (value instanceof Map) {
          CDAResource match = getMatchForField((Map) value, assets, entries);

          if (match != null) {
            fields.put(k, match);
          }
        } else if (value instanceof List) {
          List list = (List) value;

          for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);

            if (item instanceof Map) {
              CDAResource match = getMatchForField((Map) item, assets, entries);

              if (match != null) {
                list.set(i, match);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Resolves field link.
   *
   * @param map map representing the field's value (the link)
   * @param assets map of assets by ids
   * @param entries map of entries by ids
   * @return {@code CDAResource} or a subclass of it depending on the resource type, null in case
   * the link is not resolvable from this context
   */
  private CDAResource getMatchForField(Map map, HashMap<String, CDAResource> assets,
      HashMap<String, CDAResource> entries) {
    CDAResource result = null;

    Map sys = (Map) map.get("sys");

    if (sys != null) {
      String type = (String) sys.get("type");

      if (CDAResourceType.Link.equals(CDAResourceType.valueOf(type))) {
        CDAResourceType linkType = CDAResourceType.valueOf((String) sys.get("linkType"));
        String id = (String) sys.get("id");

        if (CDAResourceType.Asset.equals(linkType)) {
          result = assets.get(id);
        } else if (CDAResourceType.Entry.equals(linkType)) {
          result = entries.get(id);
        }
      }
    }

    return result;
  }
}
