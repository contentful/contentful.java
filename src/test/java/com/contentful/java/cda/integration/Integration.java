package com.contentful.java.cda.integration;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDACallback;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAHttpException;
import com.contentful.java.cda.CDALocale;
import com.contentful.java.cda.CDAResource;
import com.contentful.java.cda.CDAResourceNotFoundException;
import com.contentful.java.cda.CDASpace;
import com.contentful.java.cda.LocalizedResource;
import com.contentful.java.cda.QueryOperation.BoundingBox;
import com.contentful.java.cda.QueryOperation.BoundingCircle;
import com.contentful.java.cda.QueryOperation.Location;
import com.contentful.java.cda.SynchronizedSpace;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.QueryOperation.Exists;
import static com.contentful.java.cda.QueryOperation.HasAllOf;
import static com.contentful.java.cda.QueryOperation.HasNoneOf;
import static com.contentful.java.cda.QueryOperation.HasOneOf;
import static com.contentful.java.cda.QueryOperation.IsCloseTo;
import static com.contentful.java.cda.QueryOperation.IsEarlierOrAt;
import static com.contentful.java.cda.QueryOperation.IsEarlierThan;
import static com.contentful.java.cda.QueryOperation.IsEqualTo;
import static com.contentful.java.cda.QueryOperation.IsLaterOrAt;
import static com.contentful.java.cda.QueryOperation.IsLessThanOrEqualTo;
import static com.contentful.java.cda.QueryOperation.IsNotEqualTo;
import static com.contentful.java.cda.QueryOperation.IsWithinBoundingBoxOf;
import static com.contentful.java.cda.QueryOperation.IsWithinCircleOf;
import static com.contentful.java.cda.QueryOperation.Matches;
import static com.contentful.java.cda.SyncType.onlyDeletedAssets;
import static com.contentful.java.cda.SyncType.onlyEntriesOfType;
import static com.google.common.truth.Truth.assertThat;

public class Integration {
  CDAClient client;

  @Before public void setUp() {
    client = CDAClient.builder()
        .setSpace("cfexampleapi")
        .setToken("b4c0n73n7fu1")
        .build();
  }

  @Test
  public void fetchContentType() {
    CDAContentType cat = client.fetch(CDAContentType.class).one("cat");
    assertThat(cat.name()).isEqualTo("Cat");
    assertThat(cat.displayField()).isEqualTo("name");
    assertThat(cat.description()).isEqualTo("Meow.");
    assertThat(cat.fields()).hasSize(8);
  }

  @Test
  public void fetchNyancatEntryAsync() throws InterruptedException {
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
  public void fetchAllEntries() {
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

  @Test(expected = CDAResourceNotFoundException.class)
  public void fetchOneNonExistingEntry() {
    client.fetch(CDAEntry.class).one("fooooo");
  }

  @Test(expected = CDAResourceNotFoundException.class)
  public void fetchOneNonExistingAsset() {
    client.fetch(CDAAsset.class).one("fooooo");
  }

  @Test(expected = CDAResourceNotFoundException.class)
  public void fetchOneNonExistingContentType() {
    client.fetch(CDAContentType.class).one("fooooo");
  }

  @Test
  public void fetchSpace() {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API");
    assertThat(space.id()).isEqualTo("cfexampleapi");
    assertThat(space.type()).isEqualTo(SPACE);
  }

  // "/spaces/{space_id}/sync?initial=true",
  @Test
  public void sync() {
    SynchronizedSpace space = client.sync().observe().blockingFirst();
    assertInitial(space);

    space = client.sync(space).observe().blockingFirst();

    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(14);
    assertThat(space.deletedEntries()).hasSize(0);

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.<String>getField("tlh", "name")).isEqualTo("Nyan vIghro'");
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<String>getField("color")).isEqualTo("rainbow");
    List<String> likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");
  }

  // "/spaces/{space_id}/sync?initial=true&type=Entry&content_type=cat",
  @Test
  public void syncOnlyContentTypeCat() {
    SynchronizedSpace space = client.sync(onlyEntriesOfType("cat")).observe().blockingFirst();
    space = client.sync(space).observe().blockingFirst();

    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(3);
    assertThat(space.deletedEntries()).hasSize(0);

    CDAEntry nyanCat = space.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<String>getField("color")).isEqualTo("rainbow");
    List<String> likes = nyanCat.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");
  }

  // "/spaces/{space_id}/sync?initial=true&type=DELETEDASSETS",
  @Test
  public void syncTypeOfDeletedAssets() {
    final SynchronizedSpace space = client.sync(onlyDeletedAssets()).fetch();

    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(0);
    assertThat(space.assets()).hasSize(0);
    assertThat(space.deletedEntries()).hasSize(0);
    assertThat(space.deletedAssets()).hasSize(6);

    assertThat(space.deletedAssets()).contains("finn");
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

    assertThat(entry.url()).isEqualTo("//images.ctfassets.net/cfexampleapi/" +
        "4gp6taAwW4CmSgumq2ekUm/9da0cd1936871b8d72343e895a00d611/Nyan_cat_250px_frame.png");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}",
  @Test
  public void fetchAllEntriesOfType() {
    CDAArray all = client
        .fetch(CDAEntry.class)
        .withContentType("cat")
        .all();

    assertThat(all.total()).isEqualTo(3);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{field_id}={value}",
  @Test
  public void fetchEntryWithTwoCriteria() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.likes", IsEqualTo, "fish")
        .all();

    assertThat(found.total()).isEqualTo(1);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{linking_field}.sys.id={target_entry_id}",
  @Test
  public void fetchEntryWithLink() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.bestFriend.sys.id", IsEqualTo, "happycat")
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

  //"/spaces/{space_id}/entries?limit={value}",
  @Test
  public void fetchEntriesWithLimit() {
    final CDAArray entries = client.fetch(CDAEntry.class).limit(5).all();

    assertThat(entries.limit()).isEqualTo(5);
    assertThat(entries.items()).hasSize(5);
  }

  //"/spaces/{space_id}/entries?skip={value}",
  @Test
  public void fetchEntriesWithSkip() {
    final CDAArray entries = client.fetch(CDAEntry.class).skip(4).all();

    assertThat(entries.skip()).isEqualTo(4);
    assertThat(entries.items()).hasSize(6);
  }


  //"/spaces/{space_id}/entries?links_to_entry={value}",
  @Test
  public void fetchEntriesWithLinksToEntryQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("links_to_entry", "nyancat")
        .all();

    assertThat(found.total()).isEqualTo(1);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("happycat");
  }


  //"/spaces/{space_id}/entries?links_to_asset={value}",
  @Test
  public void fetchEntriesWithLinksToAssetQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("links_to_asset", "nyancat")
        .all();

    assertThat(found.total()).isEqualTo(1);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("nyancat");
  }

  //"/spaces/{space_id}/entries?links_to_entry={value}",
  @Test
  public void fetchEntriesWithLinksToEntryIdMethod() {
    CDAArray found = client.fetch(CDAEntry.class)
        .linksToEntryId("nyancat")
        .all();

    assertThat(found.total()).isEqualTo(1);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("happycat");
  }


  //"/spaces/{space_id}/entries?links_to_asset={value}",
  @Test
  public void fetchEntriesWithLinksToAssetIdMethod() {
    CDAArray found = client.fetch(CDAEntry.class)
        .linksToAssetId("nyancat")
        .all();

    assertThat(found.total()).isEqualTo(1);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("nyancat");
  }


  //"/spaces/{space_id}/entries?order={attribute}",
  @Test
  public void fetchEntriesWithOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .orderBy("sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    assertThat(found.items().get(0).id()).isEqualTo("4MU1s3potiUEM2G4okYOqw");
  }

  //"/spaces/{space_id}/entries?order=-{attribute}",
  @Test
  public void fetchEntriesInInverseOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .reverseOrderBy("sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("4MU1s3potiUEM2G4okYOqw");
  }

  //"/spaces/{space_id}/entries?order={attribute},{attribute2}",
  @Test
  public void fetchEntriesWithSecondaryOrder() {
    CDAArray found = client.fetch(CDAEntry.class)
        .orderBy("sys.contentType.sys.id", "sys.id")
        .all();

    assertThat(found.total()).isEqualTo(10);
    List<CDAResource> items = found.items();
    assertThat(items.get(items.size() - 1).id()).isEqualTo("finn");
  }

  //"/spaces/{space_id}/entries?limit={value}",
  @Test
  public void fetchWithLimit() {
    CDAArray found = client.fetch(CDAEntry.class)
        .limit(1)
        .orderBy("sys.id")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry entry = (CDAEntry) found.items().get(0);
    assertThat(entry.<String>getField("name")).isEqualTo("Berlin");
  }

  //"/spaces/{space_id}/entries?skip={value}",
  @Test
  public void fetchWithSkip() {
    CDAArray found = client.fetch(CDAEntry.class)
        .skip(1)
        .orderBy("sys.id")
        .all();

    assertThat(found.items().size()).isEqualTo(9);
    CDAEntry entry = (CDAEntry) found.items().get(0);
    assertThat(entry.<String>getField("name")).isEqualTo("London");
  }

  //"/spaces/{space_id}/entries?include={value}",
  @Test
  public void fetchWithoutIncluding() {
    CDAArray found = client.fetch(CDAEntry.class)
        .include(0)
        .all();

    assertThat(found.items().size()).isEqualTo(10);
    assertThat(found.assets().size()).isEqualTo(0);
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bin%5D={value}",
  @Test
  public void fetchWithInQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id", HasOneOf, "finn", "jake")
        .orderBy("sys.id")
        .all();

    assertThat(found.items().size()).isEqualTo(2);
    CDAEntry finn = (CDAEntry) found.items().get(0);
    assertThat(finn.<String>getField("name")).isEqualTo("Finn");

    CDAEntry jake = (CDAEntry) found.items().get(1);
    assertThat(jake.<String>getField("name")).isEqualTo("Jake");
  }

  //"/spaces/{space_id}/entries?{attribute}%5Ball%5D={value}",
  @Test
  public void fetchWithAllQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.likes", HasAllOf, "rainbows", "fish")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry finn = (CDAEntry) found.items().get(0);
    assertThat(finn.<String>getField("name")).isEqualTo("Nyan Cat");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Bnin%5D={value}",
  @Test
  public void fetchWithNotInQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("sys.id", HasNoneOf, "nyancat")
        .all();

    assertThat(found.items().size()).isEqualTo(2);
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bexists%5D={value}",
  @Test
  public void fetchWithExistsQuery() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id", Exists, false) // entries without id
        .all();

    assertThat(found.items().size()).isEqualTo(0);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Bexists%5D={value}",
  @Test
  public void fetchWithExistsQueryOnFields() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.bestFriend", Exists, false) // entries without id
        .all();

    assertThat(found.items()).hasSize(1);
    assertThat(found.entries().get("garfield").<String>getField("name")).isEqualTo("Garfield");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Test
  public void fetchEntriesInRange() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierThan, "1980-01-01")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.<String>getField("name")).isEqualTo("Garfield");
  }

  //"/spaces/{space_id}/entries?{attribute}%5Blte%5D={value}",
  @Test
  public void fetchEntriesVersionLTE100() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.revision", IsLessThanOrEqualTo, 0)
        .all();

    assertThat(found.total()).isEqualTo(0);
    assertThat(found.items()).hasSize(0);
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Test
  public void fetchEntriesEarlierOrAt() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierOrAt, "1979-06-18T23:00:00")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry cat = (CDAEntry) found.items().get(0);
    assertThat(cat.<String>getField("name")).isEqualTo("Garfield");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Test
  public void fetchEntriesLaterOrAt() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsLaterOrAt, "2011-04-04T22:00:00")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry cat = (CDAEntry) found.items().get(0);
    assertThat(cat.<String>getField("name")).isEqualTo("Nyan Cat");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&fields.{field_id}%5Bmatch%5D={value}",
  @Test
  public void fetchEntriesWithFieldMatching() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.name", Matches, "happy")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.<String>getField("name")).isEqualTo("Happy Cat");
  }

  //"/spaces/{space_id}/entries?fields.center%5Bnear%5D={coordinate}&content_type={content_type}",
  @Test
  public void fetchEntriesNearby() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("1t9IbcfdCk6m04uISSsaIK")
        .where("fields.center", IsCloseTo, new Location(38, -122))
        .all();

    assertThat(found.items().size()).isEqualTo(4);
    CDAEntry sf = (CDAEntry) found.items().get(0);
    assertThat(sf.<String>getField("name")).isEqualTo("San Francisco");
    CDAEntry london = (CDAEntry) found.items().get(1);
    assertThat(london.<String>getField("name")).isEqualTo("London");
  }

  //"/spaces/{space_id}/entries?fields.center%5Bnear%5D={coordinate}&content_type={content_type}",
  @Test
  public void fetchEntriesNearbyCircle() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("1t9IbcfdCk6m04uISSsaIK")
        .where("fields.center", IsWithinCircleOf, new BoundingCircle(new Location(38, -122), 100))
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry sf = (CDAEntry) found.items().get(0);
    assertThat(sf.<String>getField("name")).isEqualTo("San Francisco");
  }

  //"/spaces/{space_id}/entries?fields.center%5Bwithin%5D={rectangle}&content_type={content_type}",
  @Test
  public void fetchEntriesWithinBoundingBox() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("1t9IbcfdCk6m04uISSsaIK")
        .where("fields.center", IsWithinBoundingBoxOf, new BoundingBox(40, -124, 36, -120))
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry sf = (CDAEntry) found.items().get(0);
    assertThat(sf.<String>getField("name")).isEqualTo("San Francisco");
  }

  //"/spaces/{space_id}/entries?{attribute}%5Bne%5D={value}",
  @Test
  public void fetchEntriesWithAttributeNotEqual() {
    CDAArray found = client.fetch(CDAEntry.class)
        .where("sys.id", IsNotEqualTo, "nyancat")
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

  // "/spaces/{space_id}/locales",
  @Test
  public void fetchAllLocales() {
    CDAArray found = client.fetch(CDALocale.class)
        .all();

    assertThat(found.limit()).isEqualTo(1000);
    assertThat(found.total()).isEqualTo(2);
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

  // "/spaces/{space_id}/locales/{id}",
  @Test
  public void fetchOneLocale() {
    final CDALocale found = client.fetch(CDALocale.class).one("2oQPjMCL9bQkylziydLh57");

    assertThat(found.code()).isEqualTo("en-US");
    assertThat(found.name()).isEqualTo("English");
    assertThat(found.fallbackLocaleCode()).isNull();
    assertThat(found.isDefaultLocale()).isTrue();
  }

  @SuppressWarnings("unchecked") @Test
  public void testRawFields() {
    SynchronizedSpace space = client.sync().fetch();
    assertThat(space.items()).hasSize(14);
    assertThat(space.assets()).hasSize(4);
    assertThat(space.entries()).hasSize(10);

    CDAEntry happycat = space.entries().get("happycat");
    assertThat(happycat).isNotNull();
    assertThat(happycat.<Object>getField("image")).isNotNull();
    assertThat(happycat.<Object>getField("bestFriend")).isNotNull();

    // image
    Map<String, Map<?, ?>> rawImage = (Map<String, Map<?, ?>>) happycat.rawFields().get("image");
    assertThat(rawImage).isNotNull();
    assertThat(rawImage.get("en-US")).containsKey("sys");

    // array
    Map<String, List<String>> rawArray = (Map<String, List<String>>) happycat.rawFields().get("likes");
    assertThat(rawArray).isNotNull();
    assertThat(rawArray.get("en-US").get(0)).isEqualTo("cheezburger");
  }

  @Test(expected = CDAHttpException.class)
  public void testErrorResponse() {
    try {
      client.fetch(CDAEntry.class).where("sys.asdf", "fas").one("nope");
    } catch (CDAHttpException cdaException) {
      assertThat(cdaException.responseBody()).isNotEmpty();
      throw cdaException;
    }
  }

  private void assertInitial(SynchronizedSpace space) {
    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(14);
    assertThat(space.deletedAssets()).isEmpty();
    assertThat(space.deletedEntries()).isEmpty();

    for (CDAResource resource : space.items()) {
      assertThat(resource).isInstanceOf(LocalizedResource.class);
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
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<Object>getField("bestFriend")).isInstanceOf(CDAEntry.class);

    CDAEntry happyCat = space.entries().get("happycat");
    assertThat(happyCat).isNotNull();
    assertThat(happyCat.<String>getField("name")).isEqualTo("Happy Cat");

    // Localization
    assertThat(nyanCat.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(nyanCat.<String>getField("color")).isEqualTo("rainbow");
    final LocalizedResource.Localizer localizedCat = nyanCat.localize("tlh");
    assertThat(localizedCat.<String>getField("name")).isEqualTo("Nyan vIghro'");
    assertThat(localizedCat.<String>getField("color")).isEqualTo("rainbow"); // fallback
    assertThat(localizedCat.<Object>getField("non-existing-does-not-throw")).isNull();
  }

  void assertNyanCat(CDAEntry entry) {
    assertThat(entry.id()).isEqualTo("nyancat");
    assertThat(entry.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(entry.<String>getField("color")).isEqualTo("rainbow");
    assertThat(entry.<String>getField("birthday")).isEqualTo("2011-04-04T22:00:00+00:00");
    assertThat(entry.<Double>getField("lives")).isEqualTo(1337.0);

    List<String> likes = entry.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");

    Object bestFriend = entry.getField("bestFriend");
    assertThat(bestFriend).isInstanceOf(CDAEntry.class);
    assertThat(entry).isSameAs(((CDAEntry) bestFriend).getField("bestFriend"));

    // Localization
    final LocalizedResource.Localizer localizedCat = entry.localize("tlh");
    assertThat(localizedCat.<String>getField("color")).isEqualTo("rainbow");
    assertThat(localizedCat.<Object>getField("non-existing-does-not-throw")).isNull();
  }
}
