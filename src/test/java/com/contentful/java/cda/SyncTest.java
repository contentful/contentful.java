package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import java.util.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SyncTest extends BaseTest {
  @Test @Enqueue({
      "demo/sync_initial_p1.json", "demo/sync_initial_p2.json", "demo/space.json", "demo/content_types.json",
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

    for (CDAResource resource : space.items()) {
      assertThat(resource).isInstanceOf(LocalizedResource.class);
      LocalizedResource localized = (LocalizedResource) resource;
      assertThat(localized.locale()).isEqualTo("en-US");
      assertThat(localized.localized.keySet()).containsExactly("en-US", "tlh");
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
}
