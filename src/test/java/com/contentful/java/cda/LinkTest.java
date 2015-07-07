package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class LinkTest extends BaseTest {
  @Test
  @Enqueue("demo/links_unresolved.json")
  public void testUnresolvedLinkIsNull() throws Exception {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.items()).hasSize(1);
    assertThat(array.entries()).hasSize(1);

    CDAEntry entry = array.entries().get("happycat");
    assertThat(entry).isNotNull();
    assertThat(entry.getField("bestFriend")).isNull();
  }
}
