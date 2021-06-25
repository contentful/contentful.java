package com.contentful.java.cda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.contentful.java.cda.rich.RichTextFactory.resolveRichTextField;

final class ResourceFactory {
  private ResourceFactory() {
    throw new AssertionError();
  }

  static final Gson GSON = createGson();

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
    long start_time = System.nanoTime();
    long estimatedTime = System.nanoTime() - start_time;

    Map<String, CDAAsset> assets = new HashMap<>();
    Map<String, CDAEntry> entries = new HashMap<>();
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    // Map resources from existing space
    if (oldSpace != null) {
      ResourceUtils.mapResources(oldSpace.items(), assets, entries);
    }
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas1 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));

    SynchronizedSpace result = ResourceUtils.iterate(newSpace, client);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas2 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    ResourceUtils.mapResources(result.items(), assets, entries);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas3 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    ResourceUtils.mapDeletedResources(result);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas4 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));

    List<CDAResource> items = new ArrayList<>();
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas5 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    items.addAll(assets.values());
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas6 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    items.addAll(entries.values());
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas7 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    result.items = items;
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas8 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    result.assets = assets;
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas9 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    result.entries = entries;
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas10 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));

    ResourceUtils.setRawFields(result);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas11 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    resolveRichTextField(result, client);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas12 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    ResourceUtils.resolveLinks(result, client);
    estimatedTime = System.nanoTime() - start_time;
    System.out.println("Czas13 " +  TimeUnit.NANOSECONDS.toMillis(estimatedTime));
    return result;
  }

  static <T extends CDAResource> T fromResponse(Response<T> response) {
    return response.body();
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
}
