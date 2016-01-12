package com.contentful.java.cda;

import com.contentful.java.cda.lib.TestCallback;

import org.junit.Before;

import static com.google.common.truth.Truth.assertThat;

public class BaseIntegrationTest {
  CDAClient client;

  @Before public void setUp() throws Exception {
    client = createClient();
  }

  protected CDAClient createClient() {
    return createBuilder()
        .setEndpoint(serverUrl())
        .build();
  }

  protected CDAClient getWrongUrlClient() {
    return createBuilder()
        .setEndpoint(getInvalidEndpoint())
        .build();
  }

  private String getInvalidEndpoint() {
    return serverUrl() + "expected_failure/";
  }

  protected CDAClient createPreviewClient() {
    return createBuilder()
        .preview()
        .build();
  }

  private CDAClient.Builder createBuilder() {
    return CDAClient.builder()
        .setSpace("cfexampleapi")
        .setToken("b4c0n73n7fu1");
  }

  protected String serverUrl() {
    return "http://127.0.0.1:5000/";
  }

  protected <T extends CDAResource> T assertCallback(TestCallback<T> callback) {
    assertThat(callback.error()).isNull();
    assertThat(callback.result()).isNotNull();
    return callback.result();
  }
}
