package com.contentful.java.cda.integration;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDALocale;
import com.contentful.java.cda.CDASpace;
import com.contentful.java.cda.LocalizedResource;
import com.contentful.java.cda.SynchronizedSpace;

import java.util.List;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.SyncType.onlyDeletedAssets;
import static com.google.common.truth.Truth.assertThat;

public class IntegrationWithMasterEnvironment extends Integration {
  @Override public void setUp() {
    client = CDAClient.builder()
        .setSpace("5s4tdjmyjfpl")
        .setToken("84017d3a5da6d3ae9c733c6c210c55eebc3da033730b4e5093a6e6aa099b4995")
        .setEnvironment("master")
        .build();
  }

  // space id and so on changed on master
  @Override public void fetchSpace() {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API with En");
    assertThat(space.id()).isEqualTo("5s4tdjmyjfpl");
    assertThat(space.type()).isEqualTo(SPACE);
  }

  // asset url changed between master and staging
  @Override public void fetchSpecificAsset() {
    CDAAsset entry = client.fetch(CDAAsset.class).one("nyancat");

    assertThat(entry.url()).isEqualTo("//images.ctfassets.net/5s4tdjmyjfpl/nyancat/" +
        "28850673d35cacb94192832b5f5c1960/Nyan_cat_250px_frame.png");
  }


  // locales have different ids
  @Override public void fetchOneLocale() {
    final CDALocale found = client.fetch(CDALocale.class).one("4pPeIa89F7KD3G1q47eViY");

    assertThat(found.code()).isEqualTo("en-US");
    assertThat(found.name()).isEqualTo("English");
    assertThat(found.fallbackLocaleCode()).isNull();
    assertThat(found.isDefaultLocale()).isTrue();
  }

  @Override void assertNyanCat(CDAEntry entry) {
    assertThat(entry.id()).isEqualTo("nyancat");
    assertThat(entry.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(entry.<String>getField("color")).isEqualTo("rainbow");
    assertThat(entry.<String>getField("birthday")).isEqualTo("2011-04-04T22:00+00:00");
    assertThat(entry.<Double>getField("lives")).isEqualTo(1337.0);

    List<String> likes = entry.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");

    Object bestFriend = entry.getField("bestFriend");
    assertThat(bestFriend).isInstanceOf(CDAEntry.class);
    assertThat(entry).isSameAs(((CDAEntry) bestFriend).getField("bestFriend"));

    // Localization
    final LocalizedResource.Localizer localized = entry.localize("tlh");
    assertThat(localized.<String>getField("color")).isEqualTo("rainbow");
    assertThat(localized.<Object>getField("non-existing-does-not-throw")).isNull();
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
