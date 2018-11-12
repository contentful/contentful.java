package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.contentful.java.cda.SyncType.onlyDeletedEntries;
import static com.contentful.java.cda.SyncType.onlyEntriesOfType;
import static com.google.common.truth.Truth.assertThat;

public class SyncTest extends BaseTest {
  @Test @Enqueue({
      "demo/sync_initial_p1.json", "demo/sync_initial_p2.json",
      "demo/locales.json", "demo/content_types.json",
      "demo/sync_update_p1.json", "demo/sync_update_p2.json"
  })
  public void sync() {
    SynchronizedSpace first = client.sync().observe().blockingFirst();
    assertInitial(first);

    SynchronizedSpace second = client.sync(first).observe().blockingFirst();
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
    assertThat(superCat.<String>getField("name")).isEqualTo("Super Cat");
    assertThat(superCat.<String>getField("color")).isEqualTo("black");
    List<String> likes = superCat.getField("likes");
    assertThat(likes).containsExactly("nothing");

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.<String>getField("name")).isEqualTo("foo");
    assertThat(nyanCat.<String>getField("color")).isEqualTo("red");
    likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("a", "b", "c");
    assertThat(nyanCat.<CDAEntry>getField("bestFriend")).isSameAs(superCat);
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
      assertThat(localized.defaultLocale).isEqualTo("en-US");
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
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<CDAEntry>getField("bestFriend")).isInstanceOf(CDAEntry.class);

    CDAEntry happyCat = space.entries().get("happycat");
    assertThat(happyCat).isNotNull();
    assertThat(happyCat.<String>getField("name")).isEqualTo("Happy Cat");

    // Localization
    assertThat(nyanCat.defaultLocale).isEqualTo("en-US");
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<String>getField("color")).isEqualTo("rainbow");
    final LocalizedResource.Localizer localizedNyanCat = nyanCat.localize("tlh");
    assertThat(localizedNyanCat.<String>getField("name")).isEqualTo("Nyan vIghro'");
    assertThat(localizedNyanCat.<String>getField("color")).isEqualTo("rainbow"); // fallback
    assertThat(localizedNyanCat.<Object>getField("non-existing-does-not-throw")).isNull();
  }

  @SuppressWarnings("unchecked") @Test @Enqueue(defaults = {}, value = {
      "shallow/locales.json", "shallow/types.json", "shallow/initial.json",
      "shallow/locales.json", "shallow/types.json", "shallow/update.json"
  })
  public void testRawFields() {
    SynchronizedSpace space = client.sync().fetch();
    assertThat(space.items()).hasSize(2);
    assertThat(space.assets()).hasSize(1);
    assertThat(space.entries()).hasSize(1);

    CDAEntry foo = space.entries().get("2k5aHpfw7m0waMKYksC2Ww");
    assertThat(foo).isNotNull();
    assertThat(foo.<Object>getField("image")).isNotNull();

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
    assertThat(foo.<String>getField("image")).isNull();

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
          "links_invalid/locales.json",
          "links_invalid/content_types.json",
          "links_invalid/sync_initial.json"
      }
  )
  public void invalidLinkDoesNotThrow() {
    client.sync().fetch();
  }

  @Test @Enqueue({
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/locales.json", "demo/content_types.json"
  })
  public void syncingInPreviewWithTokenSyncsInitial() {
    client = createPreviewClient();

    final SynchronizedSpace space = client.sync("sometoken").fetch();
    assertInitial(space);
  }

  @Test
  @Enqueue({
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/locales.json", "demo/content_types.json",
      "demo/sync_initial_preview_p1.json", "demo/sync_initial_preview_p2.json",
      "demo/locales.json", "demo/content_types.json"
  })
  public void syncingInPreviewWithPreviousSpaceSyncsInitial() {
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
      "demo/locales.json"})
  public void syncingWithPreviewWorks() {
    client = createPreviewClient();
    final SynchronizedSpace space = client.sync().fetch();

    assertInitial(space);
  }

  @Test
  @Enqueue({
      "demo/sync_initial_staging_p1.json",
      "demo/sync_initial_staging_p2.json",
      "demo/locales.json"})
  public void syncingWithEnvironmentsWorks() {
    client = createBuilder()
        .setEnvironment("staging")
        .build();

    final SynchronizedSpace space = client.sync().fetch();

    assertInitial(space);

    final Collection<CDAEntry> entries = space.entries.values();
    assertThat(entries.size()).isEqualTo(11);
    final Object object = entries.toArray()[0];
    final CDAResource resource = (CDAResource) object;
    final Map<String, Map<String, Object>> environment = resource.getAttribute("environment");
    final Map<String, Object> sys = environment.get("sys");
    assertThat(sys.get("id")).isEqualTo("staging");
  }

  @Test
  public void syncUsesResourceType() {
    final SyncQuery query = client.sync(onlyDeletedEntries());

    assertThat(query.type.getName()).isEqualTo("DeletedEntry");
  }

  @Test
  public void syncUsesContentType() {
    final SyncQuery query = client.sync(onlyEntriesOfType("customType"));

    assertThat(query.type.getContentType()).isEqualTo("customType");
  }
}
