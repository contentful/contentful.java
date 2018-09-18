package com.contentful.java.cda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import retrofit2.Response;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Constants.LOCALE;
import static com.contentful.java.cda.Util.extractNested;
import static com.contentful.java.cda.Util.queryParam;

public final class ResourceUtils {
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
    localizeResources(space.items(), client.cache);
    return space;
  }

  static SynchronizedSpace nextSpace(SynchronizedSpace space, CDAClient client) {
    String nextPageUrl = space.nextPageUrl();
    if (nextPageUrl == null) {
      return null;
    }

    Response<SynchronizedSpace> synchronizedSpace =
        client.service.sync(
            client.spaceId,
            client.environmentId,
            null,
            queryParam(nextPageUrl, "sync_token"),
            null,
            null)
            .blockingFirst();

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

  public static void ensureContentType(CDAEntry entry, CDAClient client) {
    CDAContentType contentType = entry.contentType();
    if (contentType != null) {
      return;
    }

    String contentTypeId = extractNested(entry.attrs(), "contentType", "sys", "id");
    try {
      contentType = client.cacheTypeWithId(contentTypeId).blockingFirst();
    } catch (CDAResourceNotFoundException e) {
      throw new CDAContentTypeNotFoundException(entry.id(), CDAEntry.class, contentTypeId, e);
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
      List<CDAResource> resolved = new ArrayList<>();
      for (final Object link : links) {
        String linkId = getLinkId(link);
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
    Set<String> toRemove = new HashSet<>();
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
      assets = new HashSet<>();
    } else {
      assets = new HashSet<>(space.deletedAssets);
    }

    final Set<String> entries;
    if (space.deletedEntries == null) {
      entries = new HashSet<>();
    } else {
      entries = new HashSet<>(space.deletedEntries);
    }

    Flowable.fromIterable(space.items())
        .filter(resource -> {
          CDAType type = resource.type();
          return DELETEDASSET.equals(type) || DELETEDENTRY.equals(type);
        })
        .subscribe(resource -> {
          if (DELETEDASSET.equals(resource.type())) {
            assets.add(resource.id());
          } else {
            entries.add(resource.id());
          }
        });
    space.deletedAssets = assets;
    space.deletedEntries = entries;
  }

  static void localizeResources(Collection<? extends CDAResource> resources, Cache cache) {
    for (CDAResource resource : resources) {
      CDAType type = resource.type();
      if (ASSET.equals(type) || ENTRY.equals(type)) {
        localize((LocalizedResource) resource, cache);
      }
    }
  }

  static void localize(LocalizedResource resource, Cache cache) {
    resource.defaultLocale = cache.defaultLocale().code();
    resource.fallbackLocaleMap = getFallbackLocaleMap(cache);
    String resourceLocale = resource.getAttribute("locale");
    if (resourceLocale == null) {
      // sync
    } else {
      // normal
      resource.defaultLocale = resourceLocale;
      normalizeFields(resource);
    }
  }

  private static Map<String, String> getFallbackLocaleMap(Cache cache) {
    final Map<String, String> fallbackLocales = new HashMap<>(cache.locales().size());

    for (final CDALocale locale : cache.locales()) {
      final String fallback = locale.fallbackLocaleCode();
      if (fallback != null && !"".equals(fallback)) {
        fallbackLocales.put(locale.code, fallback);
      }
    }

    return fallbackLocales;
  }

  static void normalizeFields(LocalizedResource resource) {
    Map<String, Object> fields = new HashMap<>();
    for (String key : resource.fields.keySet()) {
      Object value = resource.fields.get(key);
      if (value == null) {
        continue;
      } else if (resourceContainsLocaleMap(resource, value)) {
        fields.put(key, value);
      } else {
        Map<String, Object> map = new HashMap<>();
        map.put(resource.defaultLocale, value);
        fields.put(key, map);
      }
    }
    resource.fields = fields;
  }

  private static boolean resourceContainsLocaleMap(LocalizedResource resource, Object value) {
    return value instanceof Map
        && ((Map) value).containsKey(resource.defaultLocale);
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
    Map<String, Object> rawFields = new HashMap<>();
    for (String key : resource.fields.keySet()) {
      Map<String, Object> map = new HashMap<>((Map<String, ?>) resource.fields.get(key));
      rawFields.put(key, map);
    }
    resource.rawFields = rawFields;
  }
}
