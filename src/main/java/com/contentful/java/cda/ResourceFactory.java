package com.contentful.java.cda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;

import static com.contentful.java.cda.Constants.CHARSET;

final class ResourceFactory {
  private ResourceFactory() {
    throw new AssertionError();
  }

  private static final Gson GSON = createGson();

  static CDASpace space(Response response) {
    CDASpace space = fromResponse(response);
    setDefaultLocale(space);
    return space;
  }

  static CDAArray array(Response response, CDAClient client) {
    CDAArray array = fromResponse(response, CDAArray.class);
    array.assets = new HashMap<String, CDAAsset>();
    array.entries = new HashMap<String, CDAEntry>();
    ArrayUtil.mergeIncludes(array);
    Localization.localizeResources(array.items(), client.cache.space());
    ArrayUtil.mapResources(array.items(), array.assets, array.entries);
    ArrayUtil.resolveLinks(array, client);
    return array;
  }

  static SynchronizedSpace sync(Response response, SynchronizedSpace old, CDAClient client) {
    Map<String, CDAAsset> assets = new HashMap<String, CDAAsset>();
    Map<String, CDAEntry> entries = new HashMap<String, CDAEntry>();

    // Map resources from existing space
    if (old != null) {
      ArrayUtil.mapResources(old.items(), assets, entries);
    }

    SynchronizedSpace result = ArrayUtil.iterate(response, client);
    ArrayUtil.mapResources(result.items(), assets, entries);

    List<CDAResource> items = new ArrayList<CDAResource>();
    items.addAll(assets.values());
    items.addAll(entries.values());
    result.items = items;
    result.assets = assets;
    result.entries = entries;

    ArrayUtil.resolveLinks(result, client);

    return result;
  }

  @SuppressWarnings("unchecked")
  static <T extends CDAResource> T fromResponse(Response response) {
    return (T) fromResponse(response, CDAResource.class);
  }

  static <T extends CDAResource> T fromResponse(Response response, Class<T> clazz) {
    try {
      return GSON.fromJson(new InputStreamReader(response.getBody().in(), CHARSET), clazz);
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
}
