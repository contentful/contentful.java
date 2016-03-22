package com.contentful.java.cda.integration;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDACallback;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.contentful.java.cda.CDASpace;
import com.contentful.java.cda.LocalizedResource;
import com.contentful.java.cda.SynchronizedSpace;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.google.common.truth.Truth.assertThat;

public class Integration {
  CDAClient client;

  @Before public void setUp() throws Exception {
    client = CDAClient.builder()
        .setSpace("cfexampleapi")
        .setToken("b4c0n73n7fu1")
        .build();
  }

  @Test
  public void fetchContentType() throws Exception {
    CDAContentType cat = client.fetch(CDAContentType.class).one("cat");
    assertThat(cat.name()).isEqualTo("Cat");
    assertThat(cat.displayField()).isEqualTo("name");
    assertThat(cat.description()).isEqualTo("Meow.");
    assertThat(cat.fields()).hasSize(8);
  }

  @Test
  public void fetchNyancatEntryAsync() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    final CDAEntry[] result = {null};

    client.fetch(CDAEntry.class).one("nyancat", new CDACallback<CDAEntry>() {
      @Override protected void onSuccess(CDAEntry entry) {
        result[0] = entry;
        latch.countDown();
      }

      @Override protected void onFailure(Throwable error) {
        latch.countDown();
      }
    });

    latch.await(1, TimeUnit.SECONDS);
    assertThat(result[0]).isNotNull();
    assertNyanCat(result[0]);
  }

  @Test
  public void fetchAllEntries() throws Exception {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.items()).hasSize(10);
    assertThat(array.assets()).hasSize(4);
    assertThat(array.entries()).hasSize(10);

    assertThat(array.total()).isEqualTo(10);
    assertThat(array.skip()).isEqualTo(0);
    assertThat(array.limit()).isEqualTo(100);

    for (CDAEntry entry : array.entries().values()) {
      assertThat(entry.contentType()).isNotNull();
    }

    CDAEntry nyanCat = array.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertNyanCat(nyanCat);
  }

  @Test
  public void fetchSpace() throws Exception {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API");
    assertThat(space.id()).isEqualTo("cfexampleapi");
    assertThat(space.type()).isEqualTo(SPACE);
    assertThat(space.locales()).hasSize(2);
    assertThat(space.defaultLocale().code()).isEqualTo("en-US");
  }

  @Test
  public void sync() throws Exception {
    SynchronizedSpace space = client.sync().observe().toBlocking().first();
    assertInitial(space);

    space = client.sync(space).observe().toBlocking().first();

    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(14);
    assertThat(space.deletedEntries()).hasSize(0);

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.getField("name")).isEqualTo("Nyan vIghro'");
    assertThat(nyanCat.getField("color")).isEqualTo("rainbow");
    List<String> likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");
  }

  // "/spaces/{space_id}/content_types/{content_type_id}",
  @Test
  public void fetchSpecificContentType() {
    CDAContentType cat = client.fetch(CDAContentType.class).one("cat");
    assertThat(cat.name()).isEqualTo("Cat");
  }

  // "/spaces/{space_id}/entries/{entry_id}?locale={locale}",
  @Test
  public void fetchSpecificEntryWithSpecificLocale() {
    CDAEntry entry = client.fetch(CDAEntry.class)
        .where("locale", "en-US")
        .one("nyancat");

    assertThat(entry.rawFields().get("bestFriend")).isNotNull();
  }

  // "/spaces/{space_id}/entries/{entry_id}",
  @Test
  public void fetchSpecificEntry() {
    CDAEntry entry = client.fetch(CDAEntry.class).one("nyancat");

    assertThat(entry.rawFields().get("bestFriend")).isNotNull();
  }

  //"/spaces/{space_id}/assets",
  @Test
  public void fetchAllAssets() {
    CDAArray all = client.fetch(CDAAsset.class).all();

    assertThat(all.total()).isEqualTo(4);
  }

  //"/spaces/{space_id}/assets/{asset_id}",
  @Test
  public void fetchSpecificAsset() {
    CDAAsset entry = client.fetch(CDAAsset.class).one("nyancat");

    assertThat(entry.url()).isEqualTo("//images.contentful.com/cfexampleapi/4gp6taAwW4CmSgumq2ekUm/9da0cd1936871b8d72343e895a00d611/Nyan_cat_250px_frame.png");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}",
  @Test
  public void fetchAllEntriesOfType() {
    CDAArray all = client.fetch(CDAEntry.class).where("content_type", "cat").all();

    assertThat(all.total()).isEqualTo(3);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{field_id}={value}",
  @Test
  public void fetchEntryWithTwoCriteria() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "cat")
        .where("fields.likes", "fish")
        .all();

    assertThat(found.total()).isEqualTo(1);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{linking_field}.sys.id={target_entry_id}",
  @Test
  public void fetchEntryWithLink() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "cat")
        .where("fields.bestFriend.sys.id", "happycat")
        .all();

    assertThat(found.total()).isEqualTo(1);
  }

  //"/spaces/{space_id}/entries/{entry_id}?locale={locale}",
  @Test
  public void fetchEntriesWithLocale() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("locale", "en-US")
        .all();

    assertThat(found.total()).isEqualTo(10);
  }

  //"/spaces/{space_id}/entries?query={value}",
  @Test
  public void fetchEntriesWithQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("query", "nyan")
        .all();

    assertThat(found.total()).isEqualTo(1);
  }

  //"/spaces/{space_id}/entries?order={attribute}",
  @Test
  public void fetchEntriesWithOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("order", "sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    assertThat(found.items().get(0).id()).isEqualTo("4MU1s3potiUEM2G4okYOqw");
  }

  //"/spaces/{space_id}/entries?order=-{attribute}",
  @Test
  public void fetchEntriesInInverseOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("order", "-sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("4MU1s3potiUEM2G4okYOqw");
  }

  //"/spaces/{space_id}/entries?order={attribute},{attribute2}",
  @Test
  public void fetchEntriesWithSecondaryOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("order", "sys.contentType.sys.id,sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("finn");
  }

  //"/spaces/{space_id}/entries?limit={value}",
  @Test
  public void fetchWithLimit() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("limit", "1")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry entry = (CDAEntry) found.items().get(0);
    assertThat(entry.getField("name")).isEqualTo("Berlin");
  }

  //"/spaces/{space_id}/entries?skip={value}",
  @Test
  public void fetchWithSkip() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("skip", "1")
        .all();

    assertThat(found.items().size()).isEqualTo(9);
    CDAEntry entry = (CDAEntry) found.items().get(0);
    assertThat(entry.getField("name")).isEqualTo("Nyan Cat");
  }

  //"/spaces/{space_id}/entries?include={value}",
  @Test
  public void fetchWithoutIncluding() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("include", "0")
        .all();

    assertThat(found.items().size()).isEqualTo(10);
    assertThat(found.assets().size()).isEqualTo(0);
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bin%5D={value}",
  @Test
  public void fetchWithInQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id[in]", "finn,jake")
        .all();

    assertThat(found.items().size()).isEqualTo(2);
    CDAEntry finn = (CDAEntry) found.items().get(0);
    assertThat(finn.getField("name")).isEqualTo("Finn");

    CDAEntry jake = (CDAEntry) found.items().get(1);
    assertThat(jake.getField("name")).isEqualTo("Jake");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Bnin%5D={value}",
  @Test
  public void fetchWithNotInQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "cat")
        .where("sys.id[nin]", "nyancat")
        .all();

    assertThat(found.items().size()).isEqualTo(2);
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bexists%5D={value}",
  @Test
  public void fetchWithExistsQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id[exists]", "false") // entries without id
        .all();

    assertThat(found.items().size()).isEqualTo(0);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Test
  public void fetchEntriesInRange() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "cat")
        .where("fields.birthday[lte]", "1980-01-01")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.getField("name")).isEqualTo("Garfield");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{field_id}%5Bmatch%5D={value}",
  @Test
  public void fetchEntriesWithFieldMatching() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "cat")
        .where("fields.name[match]", "happy")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.getField("name")).isEqualTo("Happy Cat");
  }

  //"/spaces/{space_id}/entries?fields.center%5Bnear%5D={coordinate}&content_type={content_type}",
  @Test
  public void fetchEntriesNearby() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "1t9IbcfdCk6m04uISSsaIK")
        .where("fields.center[near]", "38,-122")
        .all();

    assertThat(found.items().size()).isEqualTo(4);
    CDAEntry sf = (CDAEntry) found.items().get(0);
    assertThat(sf.getField("name")).isEqualTo("San Francisco");
    CDAEntry london = (CDAEntry) found.items().get(1);
    assertThat(london.getField("name")).isEqualTo("London");
  }

  //"/spaces/{space_id}/entries?fields.center%5Bwithin%5D={rectangle}&content_type={content_type}",
  @Test
  public void fetchEntriesWithinBoundingBox() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("content_type", "1t9IbcfdCk6m04uISSsaIK")
        .where("fields.center[within]", "40,-124,36,-120")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry sf = (CDAEntry) found.items().get(0);
    assertThat(sf.getField("name")).isEqualTo("San Francisco");
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bne%5D={value}",
  @Test
  public void fetchEntriesWithAttributeNotEqual() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id[ne]", "nyancat")
        .all();

    assertThat(found.items().size()).isEqualTo(9);
  }

  // "/spaces/{space_id}/assets?mimetype_group={mimetype_group}",
  @Test
  public void fetchAssetsForMimeType() {
    CDAArray found = client.fetch(CDAAsset.class)
        .where("mimetype_group", "image")
        .all();

    assertThat(found.items().size()).isEqualTo(4);
  }

  @SuppressWarnings("unchecked") @Test
  public void testRawFields() throws Exception {
    SynchronizedSpace space = client.sync().fetch();
    assertThat(space.items()).hasSize(14);
    assertThat(space.assets()).hasSize(4);
    assertThat(space.entries()).hasSize(10);

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

  private void assertInitial(SynchronizedSpace space) {
    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(14);
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
    assertThat(space.entries()).hasSize(10);
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

  private void assertNyanCat(CDAEntry entry) {
    assertThat(entry.id()).isEqualTo("nyancat");
    assertThat(entry.getField("name")).isEqualTo("Nyan Cat");
    assertThat(entry.getField("color")).isEqualTo("rainbow");
    assertThat(entry.getField("birthday")).isEqualTo("2011-04-04T22:00:00+00:00");
    assertThat(entry.getField("lives")).isEqualTo(1337.0);

    List<String> likes = entry.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");

    Object bestFriend = entry.getField("bestFriend");
    assertThat(bestFriend).isInstanceOf(CDAEntry.class);
    assertThat(entry).isSameAs(((CDAEntry) bestFriend).getField("bestFriend"));

    // Localization
    assertThat(entry.locale()).isEqualTo("en-US");
    entry.setLocale("tlh");
    assertThat(entry.getField("color")).isEqualTo("rainbow");
    assertThat(entry.getField("non-existing-does-not-throw")).isNull();
  }
}
