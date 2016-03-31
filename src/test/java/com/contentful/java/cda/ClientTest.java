package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.RecordedRequest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ClientTest extends BaseTest {
  @Test(expected = RuntimeException.class)
  public void customClient() throws Exception {
    OkHttpClient mock = mock(OkHttpClient.class);
    CDAClient cli = CDAClient.builder().setSpace("foo").setToken("bar").setClient(mock).build();

    try {
      cli.fetchSpace();
    } catch (RuntimeException e) {
      assertThat(e.getCause()).isInstanceOf(IOException.class);
      assertThat(e.getMessage()).isEqualTo("java.io.IOException: FAILED REQUEST: " +
          "Request{" +
          "method=GET, " +
          "url=https://cdn.contentful.com/spaces/foo, " +
          "tag=Request{method=GET, " +
          "url=https://cdn.contentful.com/spaces/foo, " +
          "tag=null}" +
          "}\n\t… " +
          "Response{" +
          "protocol=http/1.1, " +
          "code=401, " +
          "message=Unauthorized, " +
          "url=https://cdn.contentful.com/spaces/foo" +
          "}");
      throw e;
    }
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoSpaceThrows() throws Exception {
    try {
      CDAClient.builder().setToken("token").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Space ID must be provided.");
      throw e;
    }
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoTokenThrows() throws Exception {
    try {
      CDAClient.builder().setSpace("space").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Access token must be provided.");
      throw e;
    }
  }

  @Test
  @Enqueue
  public void oauthHeader() throws Exception {
    client.fetchSpace();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("authorization")).isEqualTo("Bearer token");
  }

  @Test
  @Enqueue
  public void userAgentHeader() throws Exception {
    String versionName = Util.getProperty("version.name");
    assertThat(versionName).isNotEmpty();
    client.fetchSpace();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("User-Agent")).isEqualTo("contentful.java/" + versionName);
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue
  public void fetchInvalidTypeThrows() throws Exception {
    try {
      client.fetch(CDAResource.class).all();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo(
          "Invalid type specified: com.contentful.java.cda.CDAResource");
      throw e;
    }
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void settingACustomLoggerAndNoneForLogLevelResultsInNoLogging() {
    final Logger logMock = mock(Logger.class);
    final CDAClient client = createBuilder()
        .setLogLevel(Logger.Level.NONE)
        .setLogger(logMock)
        .build();

    client.fetch(CDAContentType.class).all();

    verifyNoMoreInteractions(logMock);
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void settingACustomLoggerAndBasicForLogLevelResultsInLogging() {
    final Logger logMock = mock(Logger.class);
    final CDAClient client = createBuilder()
        .setLogLevel(Logger.Level.BASIC)
        .setLogger(logMock)
        .build();

    client.fetch(CDAContentType.class).all();

    verify(logMock, times(6)).log(anyString());
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void settingACustomLoggerAndFullForLogLevelResultsInLogging() {
    final Logger logMock = mock(Logger.class);
    final CDAClient client = createBuilder()
        .setLogLevel(Logger.Level.FULL)
        .setLogger(logMock)
        .build();

    client.fetch(CDAContentType.class).all();

    verify(logMock, times(6)).log(anyString());
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/content_types_cat.json")
  public void settingNoLoggerAndAnyLogLevelResultsException() {
    try {
      createBuilder()
          .setLogLevel(Logger.Level.BASIC)
          .setLogger(null)
          .build();

    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Cannot log to a null logger. Please set either logLevel to None, or do set a Logger");
      throw e;
    }
  }
}
