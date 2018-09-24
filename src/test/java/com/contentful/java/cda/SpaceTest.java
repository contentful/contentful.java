package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.google.common.truth.Truth.assertThat;

public class SpaceTest extends BaseTest {
  @Test
  @Enqueue(value = "demo/space.json", defaults = {})
  public void fetchSpace() {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API");
    assertThat(space.id()).isEqualTo("cfexampleapi");
    assertThat(space.type()).isEqualTo(SPACE);
  }
}
