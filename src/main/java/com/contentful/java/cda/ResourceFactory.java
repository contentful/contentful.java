package com.contentful.java.cda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import retrofit.client.Response;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Constants.CHARSET;
import static com.contentful.java.cda.Constants.LOCALE;
import static com.contentful.java.cda.Util.extractNested;

final class ResourceFactory {
  private static final Gson GSON = createGson();

  static CDASpace space(Response response) {
    CDASpace space = fromResponse(response);
    setDefaultLocale(space);
    return space;
  }

  static CDAArray array(Response response, CDAClient client) {
    CDAArray array = fromResponse(response);
    array.assets = new HashMap<String, CDAAsset>();
    array.entries = new HashMap<String, CDAEntry>();
    arrayMergeIncludes(array);
    arrayMapItems(array);
    arrayLocalizeItems(array);
    arrayResolveLinks(array, client);
    return array;
  }

  private static void arrayResolveLinks(CDAArray array, CDAClient client) {
    for (CDAEntry entry : array.entries().values()) {
      String contentTypeId = extractContentTypeId(entry);

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

  private static void resolveField(CDAEntry entry, CDAField field, CDAArray array) {
    CDAType linkType = CDAType.valueOf(field.linkType().toUpperCase(LOCALE));
    for (Map<String, ? super Object> fields : entry.localized.values()) {
      String id = extractNested(fields, field.id(), "sys", "id");
      if (id == null) {
        return;
      }
      CDAResource linkedResource = null;
      if (ASSET.equals(linkType)) {
        linkedResource = array.assets().get(id);
      } else if (ENTRY.equals(linkType)) {
        linkedResource = array.entries().get(id);
      }
      fields.put(field.id(), linkedResource);
    }
  }

  private static CDAResource resolveField(CDAArray array, CDAField field, String linkType) {
    CDAResource linkedResource = null;
    if (ASSET.toString().equals(linkType)) {
      linkedResource = array.assets().get(field.id());
    } else if (ENTRY.toString().equals(linkType)) {
      linkedResource = array.entries().get(field.id());
    }
    return linkedResource;
  }

  private static String extractContentTypeId(CDAEntry entry) {
    Map contentType = entry.getAttribute("contentType");
    Map sys = (Map) contentType.get("sys");
    return (String) sys.get("id");
  }

  private static void arrayLocalizeItems(CDAArray array) {
    for (CDAResource resource : array.items()) {
      CDAType type = resource.type();
      if (ASSET.equals(type) || ENTRY.equals(type)) {
        localize((LocalizedResource) resource);
      }
    }
  }

  private static void arrayMergeIncludes(CDAArray array) {
    if (array.includes != null) {
      if (array.includes.assets != null) {
        array.items().addAll(array.includes.assets);
      }
      if (array.includes.entries != null) {
        array.items().addAll(array.includes.entries);
      }
    }
  }

  private static void arrayMapItems(CDAArray array) {
    for (CDAResource resource : array.items()) {
      CDAType type = resource.type();
      if (ASSET.equals(type)) {
        array.assets().put(resource.id(), (CDAAsset) resource);
      } else if (ENTRY.equals(type)) {
        array.entries().put(resource.id(), (CDAEntry) resource);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends CDAResource> T fromResponse(Response response) {
    try {
      return (T) GSON.fromJson(new InputStreamReader(response.getBody().in(), CHARSET),
          CDAResource.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void setDefaultLocale(CDASpace space) {
    for (CDALocale locale : space.locales()) {
      if (locale.isDefaultLocale()) {
        space.defaultLocale = locale;
        break;
      }
    }
  }

  private static Gson createGson() {
    return new GsonBuilder()
        .registerTypeAdapter(CDAResource.class, new ResourceDeserializer())
        .create();
  }

  private static void localize(LocalizedResource resource) {
    resource.locale = resource.getAttribute("locale");
    resource.activeFields = resource.rawFields;
    resource.localized = new HashMap<String, Map<String, ? super Object>>();
    resource.localized.put(resource.locale(), resource.activeFields);
  }
}
