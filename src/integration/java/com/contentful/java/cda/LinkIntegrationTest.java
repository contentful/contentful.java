package com.contentful.java.cda;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class LinkIntegrationTest extends BaseIntegrationTest {
  @Test public void unresolvedLinkIsNull() throws Exception {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.items()).hasSize(11);
    assertThat(array.entries()).hasSize(11);

    CDAEntry entry = array.entries().get("happycat");
    assertThat(entry).isNotNull();
    assertThat(entry.getField("notExistentField")).isNull();
  }

  @SuppressWarnings("unchecked")
  @Test public void arrays() throws Exception {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.total()).isEqualTo(11);
    assertThat(array.items()).hasSize(11);
    assertThat(array.assets()).hasSize(4);
    assertThat(array.entries()).hasSize(11);

    CDAEntry jake = array.entries().get("jake");
    assertThat(jake).isNotNull();
    assertThat(jake.getField("image")).isInstanceOf(CDAAsset.class);
    assertThat(jake.getField("name")).isInstanceOf(String.class);
  }

  @Test public void testEmptyLinks() throws Exception {
    client.sync().fetch();
  }
}
