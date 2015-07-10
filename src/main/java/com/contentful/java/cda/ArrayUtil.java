package com.contentful.java.cda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Util.extractNested;
import static com.contentful.java.cda.Util.queryParam;

final class ArrayUtil {
  private ArrayUtil() {
    throw new AssertionError();
  }

  static SynchronizedSpace iterate(Response response, CDAClient client) {
    SynchronizedSpace result = ResourceFactory.fromResponse(response, SynchronizedSpace.class);
    List<CDAResource> items = result.items;
    while (true) {
      SynchronizedSpace nextSpace = nextSpace(result, client);
      if (nextSpace == null) {
        break;
      }
      items.addAll(nextSpace.items());
      result = nextSpace;
    }
    result.items = items;
    Localization.localizeResources(result.items(), client.cache.space());
    return result;
  }

  static SynchronizedSpace nextSpace(SynchronizedSpace space, CDAClient client) {
    String nextPageUrl = space.nextPageUrl();
    if (nextPageUrl == null) {
      return null;
    }

    Response response =
        client.service.sync(client.spaceId, null, queryParam(nextPageUrl, "sync_token"))
            .toBlocking()
            .first();

    return ResourceFactory.fromResponse(response, SynchronizedSpace.class);
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

  static void mergeIncludes(CDAArray array) {
    if (array.includes != null) {
      if (array.includes.assets != null) {
        array.items().addAll(array.includes.assets);
      }
      if (array.includes.entries != null) {
        array.items().addAll(array.includes.entries);
      }
    }
  }

  private static void resolveArrayOfLinks(CDAEntry entry, CDAField field, ArrayResource array) {
    CDAType linkType =
        CDAType.valueOf(((String) field.items().get("linkType")).toUpperCase(Constants.LOCALE));
    for (Map<String, ? super Object> fields : entry.localized.values()) {
      List links = (List) fields.get(field.id());
      if (links == null) {
        continue;
      }
      List resolved = new ArrayList();
      for (int i = 0; i < links.size(); i++) {
        CDAResource resource = findLinkedResource(array, linkType, links.get(i));
        if (resource != null) {
          resolved.add(resource);
        }
      }
      fields.put(field.id(), resolved);
    }
  }

  static void resolveSingleLink(CDAEntry entry, CDAField field, ArrayResource array) {
    CDAType linkType = CDAType.valueOf(field.linkType().toUpperCase(Locale.US));
    for (Map<String, ? super Object> fields : entry.localized.values()) {
      Object link = fields.get(field.id());
      if (link == null) {
        continue;
      }
      CDAResource resource = findLinkedResource(array, linkType, link);
      if (resource == null) {
        fields.remove(field.id());
      } else {
        fields.put(field.id(), resource);
      }
    }
  }

  private static CDAResource findLinkedResource(ArrayResource array, CDAType linkType,
      Object link) {
    String id;
    if (link instanceof CDAResource) {
      // already resolved
      id = ((CDAResource) link).id();
    } else {
      id = extractNested((Map) link, "sys", "id");
    }
    if (id == null) {
      return null;
    }
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
}
