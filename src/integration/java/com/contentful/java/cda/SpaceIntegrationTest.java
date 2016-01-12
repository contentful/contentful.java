package com.contentful.java.cda;

import org.junit.Test;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.google.common.truth.Truth.assertThat;

public class SpaceIntegrationTest extends BaseIntegrationTest {
  @Test
  public void fetchSpace() throws Exception {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API");
    assertThat(space.id()).isEqualTo("cfexampleapi");
    assertThat(space.type()).isEqualTo(SPACE);
    assertThat(space.locales()).hasSize(2);
    assertThat(space.defaultLocale().code()).isEqualTo("en-US");
  }
}
