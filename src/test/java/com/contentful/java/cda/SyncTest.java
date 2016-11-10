package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;

public class SyncTest extends BaseTest {
  @Test @Enqueue({
      "demo/sync_initial_p1.json", "demo/sync_initial_p2.json",
      "demo/space.json", "demo/content_types.json",
      "demo/sync_update_p1.json", "demo/sync_update_p2.json"
  })
  public void sync() throws Exception {
    SynchronizedSpace first = client.sync().observe().toBlocking().first();
    assertInitial(first);

    SynchronizedSpace second = client.sync(first).observe().toBlocking().first();
    assertUpdate(second);
  }

  private void assertUpdate(SynchronizedSpace space) {
    assertThat(space.nextPageUrl()).isNull();
    assertThat(space.nextSyncUrl()).endsWith("/sync?sync_token=bar");
    assertThat(space.items()).hasSize(5);
    assertThat(space.deletedAssets()).containsExactly("jake", "1x0xpXu4pSGS4OukSyWGUK");
    assertThat(space.deletedEntries()).hasSize(9);

    CDAEntry superCat = space.entries().get("supercat");
    assertThat(superCat).isNotNull();
    assertThat(superCat.getField("name")).isEqualTo("Super Cat");
    assertThat(superCat.getField("color")).isEqualTo("black");
    List<String> likes = superCat.getField("likes");
    assertThat(likes).containsExactly("nothing");

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.getField("name")).isEqualTo("foo");
    assertThat(nyanCat.getField("color")).isEqualTo("red");
    likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("a", "b", "c");
    assertThat(nyanCat.getField("bestFriend")).isSameAs(superCat);
  }

  private void assertInitial(SynchronizedSpace space) {
    assertThat(space.nextPageUrl()).isNull();
    assertThat(space.nextSyncUrl()).endsWith("/sync?sync_token=bar");
    assertThat(space.items()).hasSize(15);
    assertThat(space.deletedAssets()).isEmpty();
    assertThat(space.deletedEntries()).isEmpty();

    for (CDAResource resource : space.items()) {
      assertThat(resource).isInstanceOf(LocalizedResource.class);
      LocalizedResource localized = (LocalizedResource) resource;
      assertThat(localized.locale()).isEqualTo("en-US");
    }

    for (CDAEntry entry : space.entries().values()) {
      assertThat(entry.contentType()).isNotNull();
    }

    // Assets
    assertThat(space.assets()).hasSize(4);
    CDAAsset asset = space.assets().get("nyancat");
    assertThat(asset).isNotNull();
    assertThat(asset.title()).isEqualTo("Nyan Cat");

    // Entries
    assertThat(space.entries()).hasSize(11);
    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.getField("bestFriend")).isInstanceOf(CDAEntry.class);

    CDAEntry happyCat = space.entries().get("happycat");
    assertThat(happyCat).isNotNull();
    assertThat(happyCat.getField("name")).isEqualTo("Happy Cat");

    // Localization
    assertThat(nyanCat.locale()).isEqualTo("en-US");
    assertThat(nyanCat.getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.getField("color")).isEqualTo("rainbow");
    nyanCat.setLocale("tlh");
    assertThat(nyanCat.getField("name")).isEqualTo("Nyan vIghro'");
    assertThat(nyanCat.getField("color")).isEqualTo("rainbow"); // fallback
    assertThat(nyanCat.getField("non-existing-does-not-throw")).isNull();
  }

  @SuppressWarnings("unchecked") @Test @Enqueue(defaults = {}, value = {
      "shallow/space.json", "shallow/types.json", "shallow/initial.json",
      "shallow/space.json", "shallow/types.json", "shallow/update.json"
  })
  public void testRawFields() throws Exception {
    SynchronizedSpace space = client.sync().fetch();
    assertThat(space.items()).hasSize(2);
    assertThat(space.assets()).hasSize(1);
    assertThat(space.entries()).hasSize(1);

    CDAEntry foo = space.entries().get("2k5aHpfw7m0waMKYksC2Ww");
    assertThat(foo).isNotNull();
    assertThat(foo.getField("image")).isNotNull();

    // image
    Map<String, Map<?, ?>> rawImage = (Map<String, Map<?, ?>>) foo.rawFields().get("image");
    assertThat(rawImage).isNotNull();
    assertThat(rawImage.get("en-US")).containsKey("sys");

    // array
    Map<String, List<Map<?, ?>>> rawArray = (Map<String, List<Map<?, ?>>>) foo.rawFields().get("array");
    assertThat(rawArray).isNotNull();
    assertThat(rawArray.get("en-US").get(0)).containsKey("sys");

    String syncToken = HttpUrl.parse(space.nextSyncUrl()).queryParameter("sync_token");
    space = client.sync(syncToken).fetch();
    foo = space.entries().get("2k5aHpfw7m0waMKYksC2Ww");
    assertThat(foo).isNotNull();
    assertThat(foo.getField("image")).isNull();

    // image
    rawImage = (Map<String, Map<?, ?>>) foo.rawFields().get("image");
    assertThat(rawImage).isNotNull();
    assertThat(rawImage.get("en-US")).containsKey("sys");

    // array
    rawArray = (Map<String, List<Map<?, ?>>>) foo.rawFields().get("array");
    assertThat(rawArray).isNotNull();
    assertThat(rawArray.get("en-US").get(0)).containsKey("sys");
  }

  @Test
  @Enqueue(
      defaults = {},
      value = {
          "links_invalid/space.json",
          "links_invalid/content_types.json",
          "links_invalid/sync_initial.json"
      }
  )
  public void invalidLinkDoesNotThrow() throws Exception {
    client.sync().fetch();
  }

  @Test @Enqueue({
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/space.json", "demo/content_types.json"
  })
  public void syncingInPreviewWithTokenSyncsInitial() throws Exception {
    client = createPreviewClient();

    final SynchronizedSpace space = client.sync("sometoken").fetch();
    assertInitial(space);
  }

  @Test
  @Enqueue({
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/space.json", "demo/content_types.json",
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/space.json", "demo/content_types.json"
  })
  public void syncingInPreviewWithPreviousSpaceSyncsInitial() throws Exception {
    client = createPreviewClient();

    SynchronizedSpace space = client.sync().fetch();
    assertInitial(space);

    space = client.sync(space).fetch();
    assertInitial(space);
  }

  @Test
  @Enqueue({
      "demo/sync_initial_preview_p1.json",
      "demo/sync_initial_preview_p2.json",
      "demo/space.json"})
  public void syncingWithPreviewWorks() throws Exception {
    client = createPreviewClient();
    final SynchronizedSpace space = client.sync().fetch();

    assertInitial(space);
  }
}
