package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.RecordedRequest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ClientTest extends BaseTest {
  @Test
  @Enqueue("demo/space.json")
  public void customOkHttpClient() throws Exception {
    Call.Factory customClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().addHeader("customClient", "yes").build();
        return chain.proceed(request);
      }
    }).build();
    CDAClient client = CDAClient.builder().setSpace("foo").setToken("bar").setClient(customClient).setEndpoint(serverUrl()).build();
    client.fetchSpace();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("customClient")).isEqualTo("yes");
    assertThat(request.getHeader("authorization")).isEqualTo("Bearer bar");
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

    // only check the platform independent user agent string
    assertThat(request.getHeader("User-Agent")).startsWith("contentful.java/" + versionName);
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
