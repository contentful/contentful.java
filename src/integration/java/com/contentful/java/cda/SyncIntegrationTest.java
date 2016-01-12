package com.contentful.java.cda;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class SyncIntegrationTest extends BaseIntegrationTest {
  @Test
  public void sync() throws Exception {
    SynchronizedSpace first = client.sync().observe().toBlocking().first();
    assertInitial(first);

    SynchronizedSpace second = client.sync(first).observe().toBlocking().first();
    assertUpdate(second);
  }

  private void assertUpdate(SynchronizedSpace space) {
    assertThat(space.nextPageUrl()).isNull();
    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(15);
    assertThat(space.deletedEntries()).hasSize(0);

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.getField("name")).isEqualTo("Nyan vIghro'");
    assertThat(nyanCat.getField("color")).isEqualTo("rainbow");
    List<String> likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");
  }

  private void assertInitial(SynchronizedSpace space) {
    assertThat(space.nextPageUrl()).isNull();
    assertThat(space.nextSyncUrl()).isNotEmpty();
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

  @SuppressWarnings("unchecked") @Test
  public void testRawFields() throws Exception {
    SynchronizedSpace space = client.sync().fetch();
    assertThat(space.items()).hasSize(15);
    assertThat(space.assets()).hasSize(4);
    assertThat(space.entries()).hasSize(11);

    CDAEntry happycat = space.entries().get("happycat");
    assertThat(happycat).isNotNull();
    assertThat(happycat.getField("image")).isNotNull();
    assertThat(happycat.getField("bestFriend")).isNotNull();

    // image
    Map<String, Map<?, ?>> rawImage = (Map<String, Map<?, ?>>) happycat.rawFields().get("image");
    assertThat(rawImage).isNotNull();
    assertThat(rawImage.get("en-US")).containsKey("sys");

    // array
    Map<String, List<String>> rawArray = (Map<String, List<String>>) happycat.rawFields().get("likes");
    assertThat(rawArray).isNotNull();
    assertThat(rawArray.get("en-US").get(0)).isEqualTo("cheezburger");
  }

  @Test
  public void invalidLinkDoesNotThrow() throws Exception {
    client.sync().fetch();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void syncingWithPreviewTokenThrows() throws Exception {
    client = createPreviewClient();
    client.sync().fetch();
  }
}
