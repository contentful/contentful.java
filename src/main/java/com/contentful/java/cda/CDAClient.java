package com.contentful.java.cda;

import com.contentful.java.cda.interceptor.AuthorizationHeaderInterceptor;
import com.contentful.java.cda.interceptor.ErrorInterceptor;
import com.contentful.java.cda.interceptor.LogInterceptor;
import com.contentful.java.cda.interceptor.UserAgentHeaderInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

import static com.contentful.java.cda.Constants.ENDPOINT_PROD;
import static com.contentful.java.cda.Constants.PATH_CONTENT_TYPES;
import static com.contentful.java.cda.Util.checkNotNull;

/**
 * Client to be used when requesting information from the Delivery API. Every client is associated
 * with exactly one Space, but there is no limit to the concurrent number of clients existing at
 * any one time. Avoid creating multiple clients for the same Space. Use {@link #builder()}
 * to create a new client instance.
 */
public class CDAClient {
  final String spaceId;

  final String token;

  final CDAService service;

  final Cache cache;

  final Executor callbackExecutor;

  final boolean preview;

  private CDAClient(Builder builder) {
    validate(builder);
    this.spaceId = builder.space;
    this.token = builder.token;
    this.preview = builder.preview;
    this.service = createService(builder);
    this.cache = new Cache();
    this.callbackExecutor = Platform.get().callbackExecutor();
  }

  private void validate(Builder builder) {
    checkNotNull(builder.space, "Space ID must be provided.");
    if (builder.callFactory == null) {
      checkNotNull(builder.token, "A token must be provided, if no call factory is specified.");
    }
  }

  private CDAService createService(Builder clientBuilder) {
    String endpoint = clientBuilder.endpoint;
    if (endpoint == null) {
      endpoint = ENDPOINT_PROD;
    }

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(ResourceFactory.GSON))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .callFactory(createOrGetCallFactory(clientBuilder))
        .baseUrl(endpoint);

    return retrofitBuilder.build().create(CDAService.class);
  }

  private Call.Factory createOrGetCallFactory(Builder clientBuilder) {
    final Call.Factory callFactory;

    if (clientBuilder.callFactory == null) {
      OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
          .addInterceptor(new AuthorizationHeaderInterceptor(clientBuilder.token))
          .addInterceptor(new UserAgentHeaderInterceptor(createUserAgent()))
          .addInterceptor(new ErrorInterceptor());

      okBuilder = setLogger(okBuilder, clientBuilder);

      callFactory = okBuilder.build();
    } else {
      callFactory = clientBuilder.callFactory;
    }

    return callFactory;
  }

  private OkHttpClient.Builder setLogger(OkHttpClient.Builder okBuilder, Builder clientBuilder) {
    if (clientBuilder.logger != null) {
      switch (clientBuilder.logLevel) {
        case NONE:
          break;
        case BASIC:
          return okBuilder.addInterceptor(new LogInterceptor(clientBuilder.logger));
        case FULL:
          return okBuilder.addNetworkInterceptor(new LogInterceptor(clientBuilder.logger));
      }
    } else {
      if (clientBuilder.logLevel != Logger.Level.NONE) {
        throw new IllegalArgumentException("Cannot log to a null logger. Please set either logLevel to None, or do set a Logger");
      }
    }
    return okBuilder;
  }

  /**
   * Returns a {@link FetchQuery} for a given {@code type}, which can be used to fulfill the
   * request synchronously or asynchronously when a callback is provided.
   *
   * @param type resource type.
   * @param <T>  resource type.
   * @return query instance.
   */
  public <T extends CDAResource> FetchQuery<T> fetch(Class<T> type) {
    return new FetchQuery<T>(type, this);
  }

  /**
   * Returns an {@link ObserveQuery} for a given {@code type}, which can be used to return
   * an {@link Observable} that fetches the desired resources.
   *
   * @param type resource type.
   * @param <T>  resource type.
   * @return query instance.
   */
  public <T extends CDAResource> ObserveQuery<T> observe(Class<T> type) {
    return new ObserveQuery<T>(type, this);
  }

  /**
   * Returns a {@link SyncQuery} for initial synchronization via the Sync API.
   *
   * @return query instance.
   * @throws UnsupportedOperationException if tried to sync with a preview token
   */
  public SyncQuery sync() {
    return sync(null, null);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with the provided {@code syncToken} via
   * the Sync API.
   *
   * @param syncToken sync token.
   * @return query instance.
   * @throws UnsupportedOperationException if tried to sync with a preview token
   */
  public SyncQuery sync(String syncToken) {
    return sync(syncToken, null);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with an existing space.
   *
   * @param synchronizedSpace space to sync.
   * @return query instance.
   * @throws UnsupportedOperationException if tried to sync with a preview token
   */
  public SyncQuery sync(SynchronizedSpace synchronizedSpace) {
    return sync(null, synchronizedSpace);
  }

  private SyncQuery sync(String syncToken, SynchronizedSpace synchronizedSpace) {
    if (preview) {
      throw new UnsupportedOperationException("Syncing using a preview token is not supported. Please use a production token.");
    }

    SyncQuery.Builder builder = SyncQuery.builder().setClient(this);
    if (synchronizedSpace != null) {
      builder.setSpace(synchronizedSpace);
    }
    if (syncToken != null) {
      builder.setSyncToken(syncToken);
    }
    return builder.build();
  }

  /**
   * Fetches the space for this client (synchronously).
   */
  public CDASpace fetchSpace() {
    return observeSpace().toBlocking().first();
  }

  /**
   * Fetches the space for this client (asynchronously).
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDASpace>> C fetchSpace(C callback) {
    return (C) Callbacks.subscribeAsync(observeSpace(), callback, this);
  }

  /**
   * Returns an {@link Observable} that fetches the space for this client.
   */
  public Observable<CDASpace> observeSpace() {
    return cacheSpace(true);
  }

  /**
   * Caching
   */
  Observable<Cache> cacheAll(final boolean invalidate) {
    return cacheSpace(invalidate)
        .flatMap(new Func1<CDASpace, Observable<Map<String, CDAContentType>>>() {
          @Override public Observable<Map<String, CDAContentType>> call(CDASpace cdaSpace) {
            return cacheTypes(invalidate);
          }
        })
        .map(new Func1<Map<String, CDAContentType>, Cache>() {
          @Override public Cache call(Map<String, CDAContentType> stringCDAContentTypeMap) {
            return cache;
          }
        });
  }

  Observable<CDASpace> cacheSpace(boolean invalidate) {
    CDASpace space = invalidate ? null : cache.space();
    if (space == null) {
      return service.space(spaceId).map(new Func1<Response<CDASpace>, CDASpace>() {
        @Override public CDASpace call(Response<CDASpace> response) {
          CDASpace space = ResourceFactory.space(response);
          cache.setSpace(space);
          return space;
        }
      });
    }
    return Observable.just(space);
  }

  Observable<Map<String, CDAContentType>> cacheTypes(boolean invalidate) {
    Map<String, CDAContentType> types = invalidate ? null : cache.types();
    if (types == null) {
      return service.array(spaceId, PATH_CONTENT_TYPES, new HashMap<String, String>()).map(
          new Func1<Response<CDAArray>, Map<String, CDAContentType>>() {
            @Override public Map<String, CDAContentType> call(Response<CDAArray> arrayResponse) {
              CDAArray array = ResourceFactory.array(arrayResponse, CDAClient.this);
              Map<String, CDAContentType> tmp = new ConcurrentHashMap<String, CDAContentType>();
              for (CDAResource resource : array.items()) {
                tmp.put(resource.id(), (CDAContentType) resource);
              }
              cache.setTypes(tmp);
              return tmp;
            }
          });
    }
    return Observable.just(types);
  }

  Observable<CDAContentType> cacheTypeWithId(String id) {
    CDAContentType contentType = cache.types().get(id);
    if (contentType == null) {
      return observe(CDAContentType.class).one(id).map(new Func1<CDAContentType, CDAContentType>() {
        @Override public CDAContentType call(CDAContentType resource) {
          if (resource != null) {
            cache.types().put(resource.id(), resource);
          }
          return resource;
        }
      });
    }
    return Observable.just(contentType);
  }

  String createUserAgent() {
    final Properties properties = System.getProperties();
    return String.format("contentful.java/%s(%s %s) %s/%s",
        Util.getProperty("version.name"),
        properties.getProperty("java.runtime.name"),
        properties.getProperty("java.runtime.version"),
        properties.getProperty("os.name"),
        properties.getProperty("os.version")
    );
  }

  /**
   * Returns a {@link CDAClient} builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * This builder will be used to configure and then create a {@link CDAClient}.
   */
  public static class Builder {
    private Builder() {
    }

    String space;
    String token;
    String endpoint;

    Logger logger;
    Logger.Level logLevel = Logger.Level.NONE;

    Call.Factory callFactory;
    boolean preview;

    /**
     * Sets the space ID.
     */
    public Builder setSpace(String space) {
      this.space = space;
      return this;
    }

    /**
     * Sets the space access token.
     */
    public Builder setToken(String token) {
      this.token = token;
      return this;
    }

    /**
     * Sets a custom endpoint.
     */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    /**
     * Sets a custom logger level.
     * <p>
     * If set to {@link Logger.Level}.NONE any custom logger will get ignored.
     */
    public Builder setLogLevel(Logger.Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    /**
     * Sets a custom logger.
     */
    public Builder setLogger(Logger logger) {
      this.logger = logger;
      return this;
    }

    /**
     * Sets the endpoint to point the Preview API.
     */
    public Builder preview() {
      preview = true;
      return this.setEndpoint(Constants.ENDPOINT_PREVIEW);
    }

    /**
     * Sets a custom HTTP call factory.
     */
    public Builder setCallFactory(Call.Factory callFactory) {
      this.callFactory = callFactory;
      return this;
    }

    /**
     * Create CDAClient, using the specified configuration options.
     *
     * @return a build CDAClient.
     */
    public CDAClient build() {
      return new CDAClient(this);
    }
  }
}
