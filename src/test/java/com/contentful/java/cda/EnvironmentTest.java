package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import okhttp3.mockwebserver.RecordedRequest;

import static com.google.common.truth.Truth.assertThat;

public class EnvironmentTest extends BaseTest {

  @Test @Enqueue("demo/entries.json")
  public void creatingDefaultClientTalksToMaster() throws InterruptedException {

    createBuilder()
        .setSpace(DEFAULT_SPACE)
        .build()
        .fetch(CDAEntry.class)
        .all();

    assertThat(server.getRequestCount()).isEqualTo(3);

    final RecordedRequest recordedRequest = server.takeRequest();
    assertThat(recordedRequest.getRequestUrl().toString()).contains("/environments/master/");
  }

  @Test @Enqueue("demo/entries.json")
  public void settingEnvironmentUpdatesRequestURI() throws InterruptedException {
    createBuilder()
        .setSpace(DEFAULT_SPACE)
        .setEnvironment(STAGING_ENVIRONMENT)
        .build()
        .fetch(CDAEntry.class)
        .all();

    assertThat(server.getRequestCount()).isEqualTo(3);

    final RecordedRequest recordedRequest = server.takeRequest();
    assertThat(recordedRequest.getRequestUrl().toString()).contains("/environments/" + STAGING_ENVIRONMENT + "/");
  }
}
