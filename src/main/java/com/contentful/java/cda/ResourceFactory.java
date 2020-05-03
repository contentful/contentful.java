package com.contentful.java.cda;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Response;

import static com.contentful.java.cda.rich.RichTextFactory.resolveRichTextField;

final class ResourceFactory {
  private ResourceFactory() {
    throw new AssertionError();
  }

  static final Gson GSON = createGson();
  static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

  static CDAArray array(Response<CDAArray> arrayResponse, CDAClient client) {
    CDAArray array = arrayResponse.body();
    array.assets = new LinkedHashMap<>();
    array.entries = new LinkedHashMap<>();

    Set<CDAResource> resources = collectResources(array);
    ResourceUtils.localizeResources(resources, client.cache);
    ResourceUtils.mapResources(resources, array.assets, array.entries);
    ResourceUtils.setRawFields(array);
    resolveRichTextField(array, client);
    ResourceUtils.resolveLinks(array, client);
    return array;
  }

  private static Set<CDAResource> collectResources(CDAArray array) {
    Set<CDAResource> resources = new LinkedHashSet<>(array.items());
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

  static SynchronizedSpace sync(
      Response<SynchronizedSpace> newSpace,
      SynchronizedSpace oldSpace,
      CDAClient client) {

    Map<String, CDAAsset> assets = new HashMap<>();
    Map<String, CDAEntry> entries = new HashMap<>();
    // Map resources from existing space
    if (oldSpace != null) {
      ResourceUtils.mapResources(oldSpace.items(), assets, entries);
    }
    SynchronizedSpace result = ResourceUtils.iterate(newSpace, client);
    ResourceUtils.mapResources(result.items(), assets, entries);
    ResourceUtils.mapDeletedResources(result);

    List<CDAResource> items = new ArrayList<>();
    items.addAll(assets.values());
    items.addAll(entries.values());
    result.items = items;
    result.assets = assets;
    result.entries = entries;

    ResourceUtils.setRawFields(result);
    resolveRichTextField(result, client);
    ResourceUtils.resolveLinks(result, client);
    return result;
  }

  static <T extends CDAResource> T fromResponse(Response<T> response) {
    return response.body();
  }

  static CDAError errorFromResponse(Response response) {
    try {
      if (response.errorBody() != null) {
        return OBJECT_MAPPER.readValue(response.errorBody().byteStream(), CDAError.class);
      }
    } catch (IOException e) {
    }
    return new CDAError();
  }

  @SuppressWarnings("unchecked")
  static <T extends CDAResource> List<T> fromArrayToItems(CDAArray array) {
    final List<T> result = new ArrayList<>(array.items.size());

    for (CDAResource resource : array.items) {
      result.add((T) resource);
    }

    return result;
  }

  private static Gson createGson() {
    return new GsonBuilder()
        .registerTypeAdapter(CDAResource.class, new ResourceDeserializer())
        .create();
  }

  private static ObjectMapper createObjectMapper() {
    return JsonMapper.builder()
        .addModule(new ParameterNamesModule())
        .addModule(new Jdk8Module())
        .addModule(new JavaTimeModule())
        .addModule(new MrBeanModule())
        .findAndAddModules()
        .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .build();
  }
}
