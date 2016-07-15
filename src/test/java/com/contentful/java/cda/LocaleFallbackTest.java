package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class LocaleFallbackTest extends BaseTest {
  @Test
  @Enqueue(value = "locales/space.json", defaults = {})
  public void fetchLocales() throws Exception {
    final CDASpace space = client.fetchSpace();

    final List<CDALocale> locales = space.locales;
    Map<String, CDALocale> localesByCodeMap = listToMapByNamer(locales, localeNamer);

    assertThat(localesByCodeMap).hasSize(4);
    assertThat(localesByCodeMap.get("default").fallbackLocaleCode()).isNull();
    assertThat(localesByCodeMap.get("default").isDefaultLocale()).isTrue();

    assertThat(localesByCodeMap.get("first").fallbackLocaleCode()).isEqualTo("inbetween");

    assertThat(localesByCodeMap.get("inbetween").fallbackLocaleCode()).isEqualTo("default");

    assertThat(localesByCodeMap.get("null").fallbackLocaleCode()).isNull();
  }

  @Test
  @Enqueue(
      defaults = {"locales/space.json", "locales/content_types.json"},
      value = "locales/entries.json"
  )
  public void testFallbackLocaleQueueToDefaultOneHop() throws Exception {
    final CDAArray all = client.fetch(CDAEntry.class).all();
    final Map<String, CDAEntry> entries = all.entries();

    final CDAEntry nofirst = entries.get("no-first");
    assertThat(nofirst.getField("title")).isEqualTo("no-first");

    nofirst.setLocale("first");
    assertThat(nofirst.getField("title")).isEqualTo("inbetween");

    nofirst.setLocale("inbetween");
    assertThat(nofirst.getField("title")).isEqualTo("inbetween");

    nofirst.setLocale("default");
    assertThat(nofirst.getField("title")).isEqualTo("no-first");
  }

  @Test
  @Enqueue(
      defaults = {"locales/space.json", "locales/content_types.json"},
      value = "locales/entries.json"
  )
  public void testFallbackLocaleQueueToDefaultTwoHops() throws Exception {
    final CDAArray all = client.fetch(CDAEntry.class).all();
    final Map<String, CDAEntry> entries = all.entries();

    final CDAEntry noFirstAndNoInBetween = entries.get("no-first-and-no-inbetween");
    assertThat(noFirstAndNoInBetween.getField("title")).isEqualTo("no-first-and-no-inbetween");

    noFirstAndNoInBetween.setLocale("first");
    assertThat(noFirstAndNoInBetween.getField("title")).isEqualTo("no-first-and-no-inbetween");

    noFirstAndNoInBetween.setLocale("inbetween");
    assertThat(noFirstAndNoInBetween.getField("title")).isEqualTo("no-first-and-no-inbetween");

    noFirstAndNoInBetween.setLocale("default");
    assertThat(noFirstAndNoInBetween.getField("title")).isEqualTo("no-first-and-no-inbetween");
  }

  @Test
  @Enqueue(
      defaults = {"locales/space.json", "locales/content_types.json"},
      value = "locales/entries.json"
  )
  public void testFallbackLocaleQueueToNull() throws Exception {
    final CDAArray all = client.fetch(CDAEntry.class).all();
    final Map<String, CDAEntry> entries = all.entries();

    final CDAEntry toNull = entries.get("no-null");
    assertThat(toNull.getField("title")).isEqualTo("no-null");

    toNull.setLocale("null");
    assertThat(toNull.getField("title")).isNull();
  }


  private static Namer<CDALocale> localeNamer = new Namer<CDALocale>() {
    @Override public String name(CDALocale cdaLocale) {
      return cdaLocale.code();
    }
  };

  private interface Namer<T> {
    String name(T t);
  }

  private <T> Map<String, T> listToMapByNamer(List<T> list, Namer<T> namer) {
    final HashMap<String, T> map = new HashMap<String, T>();

    for (T item : list) {
      final String key = namer.name(item);
      if (map.containsKey(key)) {
        throw new IllegalStateException("Locale Code should not be present twice!");
      }

      map.put(key, item);
    }

    return map;
  }

}
