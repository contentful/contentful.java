package com.contentful.java.cda;

import com.contentful.java.cda.interceptor.AuthorizationHeaderInterceptor;
import com.contentful.java.cda.interceptor.UserAgentHeaderInterceptor;
import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.RecordedRequest;
import rx.Subscriber;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ClientTest extends BaseTest {

  public static final String ERROR_MESSAGE = "This is an expected error!";

  @Test @Enqueue
  public void notUsingCustomCallFactoryDoesCreateCallFactoryWithAuthAndUserAgentInterceptors() throws Exception {

    createClient().fetchSpace();

    final RecordedRequest recordedRequest = server.takeRequest();
    final Headers headers = recordedRequest.getHeaders();

    assertThat(headers.size()).isEqualTo(5);

    assertThat(headers.get(AuthorizationHeaderInterceptor.HEADER_NAME)).endsWith(DEFAULT_TOKEN);
    assertThat(headers.get(UserAgentHeaderInterceptor.HEADER_NAME)).startsWith("contentful.java");
  }

  @Test @Enqueue
  public void usingCustomCallFactoryDoesNotAddDefaultHeaders() throws Exception {
    final Call.Factory callFactory = new OkHttpClient.Builder().build();

    createBuilder()
        .setSpace(DEFAULT_SPACE)
        .setCallFactory(callFactory)
        .build()
        .fetchSpace();

    assertThat(server.getRequestCount()).isEqualTo(1);

    final RecordedRequest recordedRequest = server.takeRequest();
    final Headers headers = recordedRequest.getHeaders();

    assertThat(headers.size()).isEqualTo(4);

    assertThat(headers.get(AuthorizationHeaderInterceptor.HEADER_NAME)).isNull();
    assertThat(headers.get(UserAgentHeaderInterceptor.HEADER_NAME)).startsWith("okhttp");
  }

  @Test @Enqueue
  public void customCallFactoryCanAddInterceptors() throws Exception {
    final Interceptor interceptor = spy(new AuthorizationHeaderInterceptor(DEFAULT_TOKEN));

    Call.Factory callFactory = new OkHttpClient.Builder()
        .addNetworkInterceptor(interceptor)
        .build();

    createBuilder()
        .setSpace(DEFAULT_SPACE)
        .setCallFactory(callFactory)
        .build()
        .fetchSpace();

    verify(interceptor).intercept(any(Interceptor.Chain.class));
  }

  @Test(expected = RuntimeException.class) @Enqueue
  public void throwingAnExceptionInAnInterceptorResultsInRuntimeException() throws Exception {
    final Interceptor interceptor = new Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {
        throw new IOException(ERROR_MESSAGE);
      }
    };

    Call.Factory callFactory = new OkHttpClient.Builder()
        .addInterceptor(new AuthorizationHeaderInterceptor(DEFAULT_TOKEN))
        .addInterceptor(new UserAgentHeaderInterceptor("SOME_USER_AGENT"))
        .addInterceptor(interceptor)
        .build();

    try {
      createBuilder()
          .setSpace(DEFAULT_SPACE)
          .setCallFactory(callFactory)
          .build()
          .fetchSpace();
    } catch (RuntimeException e) {
      assertThat(e.getCause()).isInstanceOf(IOException.class);
      assertThat(e.getCause()).hasMessage(ERROR_MESSAGE);
      throw(e);
    }
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoSpaceAndNoCallFactoryThrows() throws Exception {
    try {
      CDAClient.builder().setToken("token").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Space ID must be provided.");
      throw e;
    }
  }

  @Test
  public void clientWithNoSpaceButCallFactoryBuilds() throws Exception {
    CDAClient.builder()
        .setCallFactory(mock(Call.Factory.class))
        .setSpace(DEFAULT_SPACE)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoTokenThrows() throws Exception {
    try {
      CDAClient.builder().setSpace("space").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("A token must be provided, if no call factory is specified.");
      throw e;
    }
  }

  @Test
  @Enqueue
  public void authHeader() throws Exception {
    client.fetchSpace();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("authorization")).isEqualTo("Bearer " + DEFAULT_TOKEN);
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
