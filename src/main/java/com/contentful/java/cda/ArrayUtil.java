package com.contentful.java.cda;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Constants.LOCALE;
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
        client.service.sync(client.spaceId, false, queryParam(nextPageUrl, "sync_token"))
            .toBlocking()
            .first();

    return ResourceFactory.fromResponse(response, SynchronizedSpace.class);
  }

  static void resolveLinks(ArrayResource array, CDAClient client) {
    for (CDAEntry entry : array.entries().values()) {
      String contentTypeId = extractNested(entry.attrs(), "contentType", "sys", "id");

      CDAContentType contentType = client.cacheTypeWithId(contentTypeId).toBlocking().first();
      if (contentType == null) {
        throw new RuntimeException(
            String.format("Resource ID: \"%s\" has non-existing content type mapping \"%s\".",
                entry.id(), contentTypeId));
      }

      for (CDAField field : contentType.fields()) {
        String linkType = field.linkType();
        if (linkType == null) {
          continue;
        }

        resolveField(entry, field, array);
      }
    }
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

  static void resolveField(CDAEntry entry, CDAField field, ArrayResource array) {
    CDAType linkType = CDAType.valueOf(field.linkType().toUpperCase(LOCALE));
    for (Map<String, ? super Object> fields : entry.localized.values()) {
      Object link = extractNested(fields, field.id());
      if (link == null) {
        continue;
      }
      String id;
      if (link instanceof CDAResource) {
        // already resolved
        id = ((CDAResource) link).id();
      } else {
        id = extractNested((Map) link, "sys", "id");
      }
      if (id == null) {
        continue;
      }
      CDAResource linkedResource = null;
      if (ASSET.equals(linkType)) {
        linkedResource = array.assets().get(id);
      } else if (ENTRY.equals(linkType)) {
        linkedResource = array.entries().get(id);
      }
      if (linkedResource == null) {
        fields.remove(field.id());
      } else {
        fields.put(field.id(), linkedResource);
      }
    }
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
}
