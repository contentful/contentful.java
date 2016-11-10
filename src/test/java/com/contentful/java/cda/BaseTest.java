package com.contentful.java.cda;

import com.contentful.java.cda.lib.EnqueueResponseRule;
import com.contentful.java.cda.lib.TestCallback;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.contentful.java.cda.Util.checkNotNull;
import static com.google.common.truth.Truth.assertThat;

public class BaseTest {
  public static final String DEFAULT_TOKEN = "test_token";
  public static final String DEFAULT_SPACE = "test_space";

  CDAClient client;

  MockWebServer server;

  List<String> responseQueue;

  @Rule public EnqueueResponseRule enqueueResponse = new EnqueueResponseRule();

  @Before public void setUp() throws Exception {
    server = createServer();
    server.start();

    client = createClient();

    if (responseQueue != null) {
      for (String name : responseQueue) {
        enqueue(name);
      }
    }
  }

  @After public void tearDown() throws Exception {
    server.shutdown();
  }

  protected CDAClient createClient() {
    return createBuilder()
        .build();
  }

  protected CDAClient createPreviewClient() {
    return createBuilder()
        .preview()
        .setEndpoint(serverUrl())
        .build();
  }

  protected CDAClient.Builder createBuilder() {
    return CDAClient.builder()
        .setSpace(DEFAULT_SPACE)
        .setToken(DEFAULT_TOKEN)
        .setEndpoint(serverUrl());
  }

  protected String serverUrl() {
    return "http://" + server.getHostName() + ":" + server.getPort();
  }

  protected MockWebServer createServer() {
    return new MockWebServer();
  }

  protected void enqueue(String fileName) throws IOException {
    URL resource = getClass().getClassLoader().getResource(fileName);
    checkNotNull(resource, "File not found: " + fileName);
    server.enqueue(new MockResponse().setResponseCode(200)
        .setBody(FileUtils.readFileToString(new File(resource.getFile()))));
  }

  public BaseTest setResponseQueue(List<String> responseQueue) {
    this.responseQueue = responseQueue;
    return this;
  }

  protected <T extends CDAResource> T assertCallback(TestCallback<T> callback) {
    assertThat(callback.error()).isNull();
    assertThat(callback.result()).isNotNull();
    return callback.result();
  }
}
