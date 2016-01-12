package com.contentful.java.cda;

import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ContentTypeIntegrationTest extends BaseIntegrationTest {
  @Test
  public void fetchContentType() throws Exception {
    CDAContentType cat = client.fetch(CDAContentType.class).one("cat");
    assertThat(cat.name()).isEqualTo("Cat");
    assertThat(cat.displayField()).isEqualTo("name");
    assertThat(cat.description()).isEqualTo("Meow.");
    assertThat(cat.fields()).hasSize(8);
  }

  @Test
  public void manuallyFetchedContentTypeIsCached() throws Exception {
    client.fetch(CDAContentType.class).one("cat");
    assertThat(client.cache.types()).hasSize(5);
    assertThat(client.cache.types()).doesNotContainKey("fake");
  }

  @Test
  public void missingContentTypeIsFetchedAndCached() throws Exception {
    assertThat(client.cache.types()).isNull();
    CDAArray array = client.fetch(CDAEntry.class).all();
    CDAEntry garfield = array.entries().get("garfield");
    assertThat(garfield).isNotNull();

    List<String> likes = garfield.getField("likes");
    assertThat(likes).isNotNull();
    assertThat(likes).contains("lasagna");

    assertThat(client.cache.types()).containsKey("cat");
  }
}
