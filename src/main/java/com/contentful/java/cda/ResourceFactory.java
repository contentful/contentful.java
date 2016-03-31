package com.contentful.java.cda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Response;

final class ResourceFactory {
  private ResourceFactory() {
    throw new AssertionError();
  }

  static final Gson GSON = createGson();

  static CDASpace space(Response<CDASpace> response) {
    CDASpace space = response.body();
    setDefaultLocale(space);
    return space;
  }

  static CDAArray array(Response<CDAArray> arrayResponse, CDAClient client) {
    CDAArray array = arrayResponse.body();
    array.assets = new HashMap<String, CDAAsset>();
    array.entries = new HashMap<String, CDAEntry>();

    Set<CDAResource> resources = collectResources(array);
    ResourceUtils.localizeResources(resources, client.cache.space());
    ResourceUtils.mapResources(resources, array.assets, array.entries);
    ResourceUtils.setRawFields(array);
    ResourceUtils.resolveLinks(array, client);
    return array;
  }

  private static Set<CDAResource> collectResources(CDAArray array) {
    Set<CDAResource> resources = new HashSet<CDAResource>(array.items());
    if (array.includes != null) {
      if (array.includes.assets != null) {
        resources.addAll(array.includes.assets);
      }
      if (array.includes.entries != null) {
        resources.addAll(array.includes.entries);
      }
    }
    return resources;
  }

  static SynchronizedSpace sync(Response<SynchronizedSpace> newSpace, SynchronizedSpace oldSpace, CDAClient client) {
    Map<String, CDAAsset> assets = new HashMap<String, CDAAsset>();
    Map<String, CDAEntry> entries = new HashMap<String, CDAEntry>();

    // Map resources from existing space
    if (oldSpace != null) {
      ResourceUtils.mapResources(oldSpace.items(), assets, entries);
    }

    SynchronizedSpace result = ResourceUtils.iterate(newSpace, client);
    ResourceUtils.mapResources(result.items(), assets, entries);
    ResourceUtils.mapDeletedResources(result);

    List<CDAResource> items = new ArrayList<CDAResource>();
    items.addAll(assets.values());
    items.addAll(entries.values());
    result.items = items;
    result.assets = assets;
    result.entries = entries;

    ResourceUtils.setRawFields(result);
    ResourceUtils.resolveLinks(result, client);

    return result;
  }

  static <T extends CDAResource> T fromResponse(Response<T> response) {
    return response.body();
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
