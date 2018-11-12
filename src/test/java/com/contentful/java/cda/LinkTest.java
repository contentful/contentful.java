package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class LinkTest extends BaseTest {
  @Test @Enqueue("demo/links_unresolved.json") public void unresolvedLinkIsNull() {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.items()).hasSize(1);
    assertThat(array.entries()).hasSize(1);

    CDAEntry entry = array.entries().get("happycat");
    assertThat(entry).isNotNull();
    assertThat(entry.<String>getField("bestFriend")).isNull();
  }

  @SuppressWarnings("unchecked")
  @Test @Enqueue(defaults = {
      "links/locales.json",
      "links/content_types.json"
  }, value = {
      "links/entries.json"
  }) public void arrays() {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.total()).isEqualTo(4);
    assertThat(array.items()).hasSize(4);
    assertThat(array.assets()).hasSize(2);
    assertThat(array.entries()).hasSize(4);

    CDAEntry container = array.entries().get("3vyEoAvlkk8yE4a8gCCkiu");
    assertThat(container).isNotNull();
    assertThat(container.<Object>getField("asset")).isInstanceOf(CDAAsset.class);
    assertThat(container.<Object>getField("entry")).isInstanceOf(CDAEntry.class);

    List<CDAAsset> assets = container.getField("assets");
    assertThat(assets).isNotNull();
    assertThat(assets).hasSize(2);
    assertThat(assets.get(0).id()).isEqualTo("3xkzMDqRTaoIeKkUYwiIUw");
    assertThat(assets.get(1).id()).isEqualTo("5WHMX35TkQg08sK0agoMw");

    List<CDAEntry> entries = container.getField("entries");
    assertThat(entries).isNotNull();
    assertThat(entries).hasSize(3);
    assertThat(entries.get(0).id()).isEqualTo("4NvEw8RaUUkSa2uEEogAeG");
    assertThat(entries.get(1).id()).isEqualTo("3XNpMBumdOsWuYUs0wsgMS");
    assertThat(entries.get(2).id()).isEqualTo("4kDCK9U7OgQiieIqi6ScWA");

    assertThat((List<String>) container.getField("symbols")).containsExactly("a", "b", "c");
  }

  @Test @Enqueue(defaults = {
      "links/locales.json",
      "links/content_types.json"
  }, value = {
      "links/sync_empty_links.json"
  }) public void testEmptyLinks() {
    client.sync().fetch();
  }
}
