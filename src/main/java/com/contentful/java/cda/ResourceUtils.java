package com.contentful.java.cda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Constants.LOCALE;
import static com.contentful.java.cda.Util.extractNested;
import static com.contentful.java.cda.Util.queryParam;

final class ResourceUtils {
  private ResourceUtils() {
    throw new AssertionError();
  }

  static SynchronizedSpace iterate(Response<SynchronizedSpace> spaceResponse, CDAClient client) {
    SynchronizedSpace space = ResourceFactory.fromResponse(spaceResponse);
    List<CDAResource> items = space.items;
    while (true) {
      SynchronizedSpace nextSpace = nextSpace(space, client);
      if (nextSpace == null) {
        break;
      }
      items.addAll(nextSpace.items());
      space = nextSpace;
    }
    space.items = items;
    localizeResources(space.items(), client.cache.space());
    return space;
  }

  static SynchronizedSpace nextSpace(SynchronizedSpace space, CDAClient client) {
    String nextPageUrl = space.nextPageUrl();
    if (nextPageUrl == null) {
      return null;
    }

    Response<SynchronizedSpace> synchronizedSpace =
        client.service.sync(client.spaceId, null, queryParam(nextPageUrl, "sync_token"))
            .toBlocking()
            .first();

    return synchronizedSpace.body();
  }

  static void resolveLinks(ArrayResource array, CDAClient client) {
    for (CDAEntry entry : array.entries().values()) {
      ensureContentType(entry, client);
      for (CDAField field : entry.contentType().fields()) {
        if (field.linkType() != null) {
          resolveSingleLink(entry, field, array);
        } else if ("Array".equals(field.type) && "Link".equals(field.items().get("type"))) {
          resolveArrayOfLinks(entry, field, array);
        }
      }
    }
  }

  static void ensureContentType(CDAEntry entry, CDAClient client) {
    CDAContentType contentType = entry.contentType();
    if (contentType != null) {
      return;
    }

    String id = extractNested(entry.attrs(), "contentType", "sys", "id");
    contentType = client.cacheTypeWithId(id).toBlocking().first();
    if (contentType == null) {
      throw new RuntimeException(
          String.format("Entry '%s' has non-existing content type mapping '%s'.",
              entry.id(), id));
    }
    entry.setContentType(contentType);
  }

  @SuppressWarnings("unchecked")
  static void resolveArrayOfLinks(CDAEntry entry, CDAField field, ArrayResource array) {
    CDAType linkType =
        CDAType.valueOf(((String) field.items().get("linkType")).toUpperCase(LOCALE));
    Map<String, Object> value = (Map<String, Object>) entry.fields.get(field.id());
    if (value == null) {
      return;
    }
    for (String locale : value.keySet()) {
      List<?> links = (List<?>) value.get(locale);
      if (links == null) {
        continue;
      }
      List<CDAResource> resolved = new ArrayList<CDAResource>();
      for (int i = 0; i < links.size(); i++) {
        String linkId = getLinkId(links.get(i));
        if (linkId == null) {
          continue;
        }
        CDAResource resource = findLinkedResource(array, linkType, linkId);
        if (resource != null) {
          resolved.add(resource);
        }
      }
      value.put(locale, resolved);
    }
  }

  @SuppressWarnings("unchecked")
  static void resolveSingleLink(CDAEntry entry, CDAField field, ArrayResource array) {
    CDAType linkType = CDAType.valueOf(field.linkType().toUpperCase(LOCALE));
    Map<String, Object> value = (Map<String, Object>) entry.fields.get(field.id());
    if (value == null) {
      return;
    }
    Set<String> toRemove = new HashSet<String>();
    for (String locale : value.keySet()) {
      String linkId = getLinkId(value.get(locale));
      if (linkId == null) {
        continue;
      }
      CDAResource resource = findLinkedResource(array, linkType, linkId);
      if (resource == null) {
        toRemove.add(locale);
      } else {
        value.put(locale, resource);
      }
    }
    for (String locale : toRemove) {
      value.remove(locale);
    }
  }

  static String getLinkId(Object link) {
    if (link == null) {
      return null;
    }
    if (link instanceof CDAResource) {
      // already resolved
      return ((CDAResource) link).id();
    }
    return extractNested((Map<?, ?>) link, "sys", "id");
  }

  static CDAResource findLinkedResource(ArrayResource array, CDAType linkType,
                                        String id) {
    if (ASSET.equals(linkType)) {
      return array.assets().get(id);
    } else if (ENTRY.equals(linkType)) {
      return array.entries().get(id);
    }
    return null;
  }

  static void mapResources(Collection<? extends CDAResource> resources,
                           Map<String, CDAAsset> assets, Map<String, CDAEntry> entries) {
    for (CDAResource resource : resources) {
      CDAType type = resource.type();
      String id = resource.id();
      if (ASSET.equals(type)) {
        assets.put(id, (CDAAsset) resource);
      } else if (DELETEDASSET.equals(type)) {
        assets.remove(id);
      } else if (DELETEDENTRY.equals(type)) {
        entries.remove(id);
      } else if (ENTRY.equals(type)) {
        entries.put(id, (CDAEntry) resource);
      }
    }
  }

  static void mapDeletedResources(SynchronizedSpace space) {
    final Set<String> assets;
    if (space.deletedAssets == null) {
      assets = new HashSet<String>();
    } else {
      assets = new HashSet<String>(space.deletedAssets);
    }

    final Set<String> entries;
    if (space.deletedEntries == null) {
      entries = new HashSet<String>();
    } else {
      entries = new HashSet<String>(space.deletedEntries);
    }

    Observable.from(space.items())
        .filter(new Func1<CDAResource, Boolean>() {
          @Override public Boolean call(CDAResource resource) {
            CDAType type = resource.type();
            return DELETEDASSET.equals(type) || DELETEDENTRY.equals(type);
          }
        })
        .subscribe(new Action1<CDAResource>() {
          @Override public void call(CDAResource resource) {
            if (DELETEDASSET.equals(resource.type())) {
              assets.add(resource.id());
            } else {
              entries.add(resource.id());
            }
          }
        });
    space.deletedAssets = assets;
    space.deletedEntries = entries;
  }

  static void localizeResources(Collection<? extends CDAResource> resources, CDASpace space) {
    for (CDAResource resource : resources) {
      CDAType type = resource.type();
      if (ASSET.equals(type) || ENTRY.equals(type)) {
        localize((LocalizedResource) resource, space);
      }
    }
  }

  static void localize(LocalizedResource resource, CDASpace space) {
    resource.setDefaultLocale(space.defaultLocale().code());
    resource.setFallbackLocaleMap(getFallbackLocaleMap(space));
    String resourceLocale = resource.getAttribute("locale");
    if (resourceLocale == null) {
      // sync
      resource.setLocale(resource.defaultLocale());
    } else {
      // normal
      resource.setLocale(resourceLocale);
      normalizeFields(resource);
    }
  }

  private static Map<String, String> getFallbackLocaleMap(CDASpace space) {
    final Map<String, String> fallbackLocales = new HashMap<String, String>(space.locales().size());

    for (final CDALocale locale : space.locales()) {
      final String fallback = locale.fallbackLocaleCode();
      if (fallback != null && !"".equals(fallback)) {
        fallbackLocales.put(locale.code, fallback);
      }
    }

    return fallbackLocales;
  }

  static void normalizeFields(LocalizedResource resource) {
    Map<String, Object> fields = new HashMap<String, Object>();
    for (String key : resource.fields.keySet()) {
      Object value = resource.fields.get(key);
      if (value == null) {
        continue;
      } else if (resourceContainsLocaleMap(resource, value)) {
        fields.put(key, value);
      } else {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(resource.locale(), value);
        fields.put(key, map);
      }
    }
    resource.fields = fields;
  }

  private static boolean resourceContainsLocaleMap(LocalizedResource resource, Object value) {
    return value instanceof Map
        && ((Map) value).containsKey(resource.locale);
  }

  static void setRawFields(ArrayResource array) {
    for (CDAAsset asset : array.assets().values()) {
      setRawFields(asset);
    }
    for (CDAEntry entry : array.entries().values()) {
      setRawFields(entry);
    }
  }

  @SuppressWarnings("unchecked")
  private static void setRawFields(LocalizedResource resource) {
    Map<String, Object> rawFields = new HashMap<String, Object>();
    for (String key : resource.fields.keySet()) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.putAll((Map<String, ?>) resource.fields.get(key));
      rawFields.put(key, map);
    }
    resource.rawFields = rawFields;
  }
}
