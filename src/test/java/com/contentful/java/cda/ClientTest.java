package com.contentful.java.cda;

import com.contentful.java.cda.build.GeneratedBuildParameters;
import com.contentful.java.cda.interceptor.AuthorizationHeaderInterceptor;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor;
import com.contentful.java.cda.interceptor.UserAgentHeaderInterceptor;
import com.contentful.java.cda.lib.Enqueue;
import com.contentful.java.cda.lib.EnqueueResponse;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.RecordedRequest;

import static com.contentful.java.cda.SyncType.onlyEntriesOfType;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ClientTest extends BaseTest {

  public static final String ERROR_MESSAGE = "This is an expected error!";

  @Test @Enqueue
  public void notUsingCustomCallFactoryDoesCreateCallFactoryWithAuthAndUserAgentInterceptors() throws InterruptedException {

    createClient().fetchSpace();

    final RecordedRequest recordedRequest = server.takeRequest();
    final Headers headers = recordedRequest.getHeaders();

    assertThat(headers.size()).isEqualTo(6);

    assertThat(headers.get(AuthorizationHeaderInterceptor.HEADER_NAME)).endsWith(DEFAULT_TOKEN);
    assertThat(headers.get(UserAgentHeaderInterceptor.HEADER_NAME)).startsWith("contentful.java");
  }

  @Test @Enqueue
  public void usingCustomCallFactoryDoesNotAddDefaultHeaders() throws InterruptedException {
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
  public void customCallFactoryCanAddInterceptors() throws IOException {
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
  public void throwingAnExceptionInAnInterceptorResultsInRuntimeException() {
    final Interceptor interceptor = chain -> {
      throw new IOException(ERROR_MESSAGE);
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
      assertThat(e.getCause()).hasMessageThat().isEqualTo(ERROR_MESSAGE);
      throw (e);
    }
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoSpaceAndNoCallFactoryThrows() {
    try {
      CDAClient.builder().setToken("token").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Space ID must be provided.");
      throw e;
    }
  }

  @Test
  public void clientWithNoSpaceButCallFactoryBuilds() {
    CDAClient.builder()
        .setCallFactory(mock(Call.Factory.class))
        .setSpace(DEFAULT_SPACE)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoTokenThrows() {
    try {
      CDAClient.builder().setSpace("space").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("A token must be provided, if no call factory is specified.");
      throw e;
    }
  }

  @Test
  @Enqueue
  public void authHeader() throws InterruptedException {
    client.fetchSpace();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("authorization")).isEqualTo("Bearer " + DEFAULT_TOKEN);
  }

  @Test
  @Enqueue
  public void userAgentHeader() throws InterruptedException {
    String versionName = GeneratedBuildParameters.PROJECT_VERSION;
    assertThat(versionName).matches("^\\d+\\.\\d+\\.\\d+(-\\w+)?$");

    client.fetchSpace();
    RecordedRequest request = server.takeRequest();

    // only check the platform independent user agent string
    assertThat(request.getHeader("User-Agent")).startsWith("contentful.java/" + versionName);
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue
  public void fetchInvalidTypeThrows() {
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

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void enforcingCustomTLS12DoesNotThrow() {
    final CDAClient client = createBuilder()
        .setTls12Implementation(Tls12Implementation.sdkProvided)
        .build();

    assertThat(client).isNotNull();

    client.fetch(CDAEntry.class).all();
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void enforcingSystemTLS12DoesNotThrow() {
    final CDAClient client = createBuilder()
        .setTls12Implementation(Tls12Implementation.systemProvided)
        .build();

    assertThat(client).isNotNull();

    client.fetch(CDAEntry.class).all();
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void usingTLS12recommendationDoesNotThrow() {
    final CDAClient client = createBuilder()
        .setTls12Implementation(Tls12Implementation.useRecommendation)
        .build();

    assertThat(client).isNotNull();

    client.fetch(CDAEntry.class).all();
  }

  @Test
  public void androidBelow20CreatesNewTLSFactoryIfNotEnforcedNotTo() {
    try {
      Platform.platform = new Platform.Android(18, "Mocked Android Platform");
      final CDAClient.Builder builder = CDAClient.builder();

      builder.setTls12Implementation(Tls12Implementation.useRecommendation);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isTrue();

      builder.setTls12Implementation(Tls12Implementation.sdkProvided);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isTrue();

      builder.setTls12Implementation(Tls12Implementation.systemProvided);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isFalse();
    } finally {
      Platform.platform = null;
    }
  }

  @Test
  public void androidAtLeast20UsesSystemTLSFactoryIfNotEnforcedToNotTo() {
    try {
      Platform.platform = new Platform.Android(23, "Mocked _NEW_ Android Platform");
      final CDAClient.Builder builder = CDAClient.builder();

      builder.setTls12Implementation(Tls12Implementation.useRecommendation);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isFalse();

      builder.setTls12Implementation(Tls12Implementation.sdkProvided);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isTrue();

      builder.setTls12Implementation(Tls12Implementation.systemProvided);
      assertThat(builder.isSdkTlsSocketFactoryWanted()).isFalse();
    } finally {
      Platform.platform = null;
    }
  }

  @Test
  public void javaUsesSystemsTLSFactoryIfNotForcedToCustom() {
    final CDAClient.Builder builder = CDAClient.builder();
    Platform.platform = new Platform.Base();

    builder.setTls12Implementation(Tls12Implementation.useRecommendation);
    assertThat(builder.isSdkTlsSocketFactoryWanted()).isFalse();

    builder.setTls12Implementation(Tls12Implementation.sdkProvided);
    assertThat(builder.isSdkTlsSocketFactoryWanted()).isTrue();

    builder.setTls12Implementation(Tls12Implementation.systemProvided);
    assertThat(builder.isSdkTlsSocketFactoryWanted()).isFalse();
  }

  @Test(expected = CDAHttpException.class)
  @Enqueue(
      defaults = {},
      complex = {@EnqueueResponse(fileName = "errors/invalid_query.json", code = 404)}
  )
  public void sendingInvalidQueriesThrowsMeaningfulException() {

    final CDAClient client = createClient();

    try {
      client.fetch(CDAEntry.class).where("not", "existing").all();
    } catch (CDAHttpException cdaException) {
      assertThat(cdaException.responseCode()).isEqualTo(404);
      assertThat(cdaException.responseMessage()).isEqualTo("Client Error");
      throw cdaException;
    }
  }

  @Test(expected = CDAHttpException.class)
  @Enqueue(
      defaults = {},
      complex = {@EnqueueResponse(
          fileName = "errors/ratelimit.json",
          code = 429,
          headers = {
              "X-Contentful-RateLimit-Hour-Limit: 1",
              "X-Contentful-RateLimit-Hour-Remaining: 20",
              "X-Contentful-RateLimit-Second-Limit: 40",
              "X-Contentful-RateLimit-Second-Remaining: 60",
              "X-Contentful-RateLimit-Reset: 80"
          }
      )}
  )
  public void requestingWhileRateLimitedThrows() {

    final CDAClient client = createClient();

    try {
      client.fetch(CDAEntry.class).all();
    } catch (CDAHttpException cdaException) {
      assertThat(cdaException.responseCode()).isEqualTo(429);

      assertThat(cdaException.rateLimitHourLimit()).isEqualTo(1);
      assertThat(cdaException.rateLimitHourRemaining()).isEqualTo(20);
      assertThat(cdaException.rateLimitSecondLimit()).isEqualTo(40);
      assertThat(cdaException.rateLimitSecondRemaining()).isEqualTo(60);
      assertThat(cdaException.rateLimitReset()).isEqualTo(80);
      throw cdaException;
    }
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

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void clearingTheCacheClearsTheCache() {
    client.fetch(CDAContentType.class).all();
    assertThat(client.cache.types()).isNotNull();
    assertThat(client.cache.locales()).isNotNull();
    assertThat(client.cache.locales()).hasSize(2);

    client.clearCache();
    assertThat(client.cache.types()).isNull();
    assertThat(client.cache.locales()).isNull();
  }

  @Test
  @Enqueue("demo/content_types_cat.json")
  public void localesGetCached() {
    client.fetch(CDAContentType.class).all();
    assertThat(client.cache.types()).hasSize(5);
    assertThat(client.cache.locales()).hasSize(2);
    assertThat(client.cache.locales().get(0).code()).isEqualTo("en-US");
    assertThat(client.cache.locales().get(1).code()).isEqualTo("tlh");
    assertThat(client.cache.defaultLocale().code()).isEqualTo("en-US");
  }

  static class InterceptingInterceptor implements Interceptor {
    public boolean hit = false;

    @Override public Response intercept(Chain chain) throws IOException {
      hit = true;
      return chain.proceed(chain.request());
    }
  }

  @Test @Enqueue
  public void customCallFactoryCanUseDefault() {

    final CDAClient.Builder builder = createBuilder();

    final OkHttpClient.Builder callFactoryBuilder = builder.defaultCallFactoryBuilder();
    final InterceptingInterceptor interceptor = new InterceptingInterceptor();
    callFactoryBuilder.addInterceptor(interceptor);

    builder
        .setCallFactory(callFactoryBuilder.build())
        .build()
        .fetchSpace();

    assertThat(interceptor.hit).isTrue();
  }

  @Test
  @Enqueue
  public void contentfulCustomHeaderUsed() throws InterruptedException {
    final CDAClient client = createBuilder().build();

    client.fetchSpace();

    final RecordedRequest request = server.takeRequest();
    final String headerValue = request.getHeader(ContentfulUserAgentHeaderInterceptor.HEADER_NAME);

    assertThat(headerValue).matches("((sdk|platform|os) [.a-zA-Z0-9]+/[.a-zA-Z0-9]+(-.*)?; ?){3}");
  }


  @Test
  @Enqueue
  public void addingApplicationToCustomHeaderWorks() throws InterruptedException {
    final CDAClient client = createBuilder()
        .setApplication("Contentful Java Unit Test", "0.0.1-beta4")
        .build();

    client.fetchSpace();

    final RecordedRequest request = server.takeRequest();
    final String headerValue = request.getHeader(ContentfulUserAgentHeaderInterceptor.HEADER_NAME);

    assertThat(headerValue).contains("app contentful-java-unit-test/0.0.1-beta4;");
  }

  @Test
  @Enqueue
  public void addingIntegrationToCustomHeaderWorks() throws InterruptedException {
    // use this features if you are using creating a library on top of the sdk.
    final CDAClient client = createBuilder()
        .setIntegration("contentful.awesomelib.java", "0.0.1-beta9")
        .build();

    client.fetchSpace();

    final RecordedRequest request = server.takeRequest();
    final String headerValue = request.getHeader(ContentfulUserAgentHeaderInterceptor.HEADER_NAME);

    assertThat(headerValue).contains("integration contentful.awesomelib.java/0.0.1-beta9;");
  }

  @Test
  @Enqueue({"demo/locales.json", "demo/content_types.json", "demo/sync_initial_p1.json"})
  public void syncTypeIsAddedToRequest() throws InterruptedException {
    final CDAClient client = createPreviewClient();

    final CountDownLatch latch = new CountDownLatch(1);
    client.sync(onlyEntriesOfType("CustomType")).fetch(new CDACallback<SynchronizedSpace>() {
      @Override
      protected void onSuccess(SynchronizedSpace result) {
        latch.countDown();
      }

      @Override
      protected void onFailure(Throwable error) {
        latch.countDown();
      }

    });

    latch.await();
    RecordedRequest request = null;
    for (int i = server.getRequestCount(); i > 0; --i) {
      request = server.takeRequest();
    }
    assertThat(request.getPath()).contains("type=Entry");
    assertThat(request.getPath()).contains("content_type=CustomType");
  }
}
