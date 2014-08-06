package com.contentful.java.api;

import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A custom runnable used internally for preparing array result objects.
 *
 * This will attempt to resolve all links within an {@link ArrayResource} instance,
 * while iterating through all of it's normal and included resources.
 */
class ArrayParser<T extends ArrayResource> implements Callable<T> {
    private final T source;
    private final CDASpace space;

    public ArrayParser(T source, CDASpace space) {
        this.source = source;
        this.space = space;
    }

    @Override
    public T call() throws Exception {
        HashMap<String, CDAResource> assets = new HashMap<String, CDAResource>();
        HashMap<String, CDAResource> entries = new HashMap<String, CDAResource>();

        ArrayList<CDAResource> items;

        if (source instanceof CDAArray) {
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
        } else if (source instanceof CDASyncedSpace) {
            items = ((CDASyncedSpace) source).getItems();
        } else {
            throw new IllegalArgumentException("Invalid result item.");
        }

        for (CDAResource item : items) {
            parseResource(item);

            if (item instanceof CDAAsset) {
                assets.put((String) item.getSys().get("id"), item);
            } else if (item instanceof CDAEntry) {
                entries.put((String) item.getSys().get("id"), item);
            }
        }

        for (Map.Entry<String, CDAResource> entry : entries.entrySet()) {
            CDAResource item = entry.getValue();
            resolveResourceLinks(item, assets, entries);
        }

        return source;
    }

    private void parseResource(CDAResource resource) {
        if (source instanceof CDASyncedSpace) {
            if (resource instanceof ResourceWithMap) {
                ResourceWithMap res = (ResourceWithMap) resource;

                HashMap<String, Map> localizedFields = res.getLocalizedFieldsMap();
                Map<String, Object> rawFields = res.getRawFields();

                // create a map for every locale
                for (Locale locale : space.getLocales()) {
                    localizedFields.put(locale.code, new HashMap<String, Object>());
                }

                // iterate through all fields
                for (String key : rawFields.keySet()) {
                    for (Locale locale : space.getLocales()) {
                        Map map = (Map) rawFields.get(key);
                        Object value = map.get(locale.code);

                        if (value != null) {
                            localizedFields.get(locale.code).put(key, value);
                        }
                    }
                }
            }
        }
    }

    private void resolveResourceLinks(CDAResource resource, HashMap<String, CDAResource> assets, HashMap<String, CDAResource> entries) {
        if (resource instanceof ResourceWithMap) {
            ResourceWithMap res = (ResourceWithMap) resource;
            HashMap<String, Map> localizedFields = res.getLocalizedFieldsMap();

            for (Map.Entry<String, Map> entry : localizedFields.entrySet()) {
                Map fields = entry.getValue();

                for (Object k : fields.keySet()) {
                    Object value = fields.get(k);

                    if (value instanceof Map) {
                        CDAResource match = getMatchForField((Map) value, assets, entries);

                        if (match != null) {
                            fields.put(k, match);
                        }
                    }
                }
            }
        }
    }

    private CDAResource getMatchForField(Map map, HashMap<String, CDAResource> assets, HashMap<String, CDAResource> entries) {
        CDAResource result = null;

        Map sys = (Map) map.get("sys");

        if (sys != null) {
            String type = (String) sys.get("type");

            if (Constants.CDAResourceType.Link.equals(Constants.CDAResourceType.valueOf(type))) {
                Constants.CDAResourceType linkType = Constants.CDAResourceType.valueOf((String) sys.get("linkType"));
                String id = (String) sys.get("id");

                if (Constants.CDAResourceType.Asset.equals(linkType)) {
                    result = assets.get(id);
                } else if (Constants.CDAResourceType.Entry.equals(linkType)) {
                    result = entries.get(id);
                } else if (Constants.CDAResourceType.Space.equals(linkType)) {
                    result = space;
                }
            }
        }

        return result;
    }
}
