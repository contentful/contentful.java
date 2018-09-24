package com.contentful.java.cda.integration;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAHttpException;
import com.contentful.java.cda.SynchronizedSpace;

import org.junit.Test;

import static com.contentful.java.cda.QueryOperation.Exists;
import static com.contentful.java.cda.QueryOperation.IsEarlierOrAt;
import static com.contentful.java.cda.QueryOperation.IsEarlierThan;
import static com.contentful.java.cda.SyncType.onlyDeletedAssets;
import static com.google.common.truth.Truth.assertThat;

public class IntegrationWithStagingEnvironment extends IntegrationWithMasterEnvironment {
  public void setUp() {
    client = CDAClient.builder()
        .setSpace("5s4tdjmyjfpl")
        .setToken("16bc99120b1e0b33cc16323324baab4dbe9350c0d7a2b222ed8d7d5328e95bf3")
        .setEnvironment("staging")
        .build();
  }


  // cannot fetch space on non master token
  @Test(expected = CDAHttpException.class)
  @Override public void fetchSpace() {
    client.fetchSpace();
  }

  @Override
  public void fetchEntriesInRange() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierThan, "1980-01-01")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.<String>getField("name")).isEqualTo("Staged Garfield");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Override
  public void fetchEntriesEarlierOrAt() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierOrAt, "1979-06-18T23:00:00")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry cat = (CDAEntry) found.items().get(0);
    assertThat(cat.<String>getField("name")).isEqualTo("Staged Garfield");
  }

  @Override
  public void fetchWithExistsQueryOnFields() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.bestFriend", Exists, false) // entries without id
        .all();

    assertThat(found.items()).hasSize(1);
    assertThat(found.entries().get("garfield").<String>getField("name")).isEqualTo("Staged Garfield");
  }

  @Override public void syncTypeOfDeletedAssets() {
    final SynchronizedSpace space = client.sync(onlyDeletedAssets()).fetch();

    assertThat(space.nextSyncUrl()).isNotEmpty();
    assertThat(space.items()).hasSize(0);
    assertThat(space.assets()).hasSize(0);
    assertThat(space.deletedEntries()).hasSize(0);
    assertThat(space.deletedAssets()).hasSize(0);
  }
}
