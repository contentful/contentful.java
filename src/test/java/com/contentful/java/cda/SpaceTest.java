package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.google.common.truth.Truth.assertThat;

public class SpaceTest extends BaseTest {
  @Test
  @Enqueue("demo/space.json")
  public void fetchSpace() throws Exception {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API");
    assertThat(space.id()).isEqualTo("cfexampleapi");
    assertThat(space.type()).isEqualTo(SPACE);
    assertThat(space.locales()).hasSize(2);
    assertThat(space.defaultLocale().code()).isEqualTo("en-US");
  }
}
