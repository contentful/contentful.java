package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class LocaleTest extends BaseTest {
  @Test
  @Enqueue("locales/array_empty.json")
  public void fetchEmptyLocales() {
    final CDAArray all = client.fetch(CDALocale.class).all();

    assertThat(all.total).isEqualTo(0);
    assertThat(all.limit).isEqualTo(1000);
  }

  @Test(expected = CDAResourceNotFoundException.class)
  @Enqueue("errors/not_found.json")
  public void fetchOneNonExistingThrows() {
    client.fetch(CDALocale.class).one("foo");
  }

  @Test
  @Enqueue("locales/fetch_all_locales.json")
  public void fetchOne() {
    final CDALocale one = client.fetch(CDALocale.class).one("2oQPjMCL9bQkylziydLh57");
    assertThat(one.toString()).isEqualTo("CDALocale { CDAResource { attrs = {" +
        "id=2oQPjMCL9bQkylziydLh57, type=Locale, version=1.0}, id = 2oQPjMCL9bQkylziydLh57, " +
        "type = LOCALE } code = en-US, defaultLocale = true, fallbackLocaleCode = null, " +
        "name = English }");
  }

  @Test
  @Enqueue("locales/fetch_all_locales.json")
  public void fetchAllLocales() {
    final CDAArray found = client.fetch(CDALocale.class).all();

    assertThat(found.items().size()).isEqualTo(2);

    final CDALocale first = (CDALocale) found.items().get(0);
    assertThat(first.code()).isEqualTo("en-US");
    assertThat(first.name()).isEqualTo("English");
    assertThat(first.fallbackLocaleCode()).isNull();
    assertThat(first.isDefaultLocale()).isTrue();

    final CDALocale second = (CDALocale) found.items().get(1);
    assertThat(second.code()).isEqualTo("tlh");
    assertThat(second.name()).isEqualTo("Klingon");
    assertThat(second.fallbackLocaleCode()).isEqualTo("en-US");
    assertThat(second.isDefaultLocale()).isFalse();
  }
}
