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
    this(new Cache(),
        Platform.get().callbackExecutor(),
        createService(builder),
        builder);
    validate(builder);
  }

  CDAClient(Cache cache, Executor executor, CDAService service, Builder builder) {
    this.cache = cache;
    this.callbackExecutor = executor;
    this.service = service;
    this.spaceId = builder.space;
    this.token = builder.token;
    this.preview = builder.preview;
  }

  private void validate(Builder builder) {
    checkNotNull(builder.space, "Space ID must be provided.");
    if (builder.callFactory == null) {
      checkNotNull(builder.token, "A token must be provided, if no call factory is specified.");
    }
  }

  private static CDAService createService(Builder clientBuilder) {
    String endpoint = clientBuilder.endpoint;
    if (endpoint == null) {
      endpoint = ENDPOINT_PROD;
    }

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(ResourceFactory.GSON))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .callFactory(clientBuilder.createOrGetCallFactory(clientBuilder))
        .baseUrl(endpoint);

    return retrofitBuilder.build().create(CDAService.class);
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
   */
  public SyncQuery sync() {
    return sync(null, null);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with the provided {@code syncToken} via
   * the Sync API.
   *
   * If called from a {@link #preview} client, this will always do an initial sync.
   *
   * @param syncToken sync token.
   * @return query instance.
   */
  public SyncQuery sync(String syncToken) {
    return sync(syncToken, null);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with an existing space.
   *
   * If called from a {@link #preview} client, this will always do an initial sync.
   *
   * @param synchronizedSpace space to sync.
   * @return query instance.
   */
  public SyncQuery sync(SynchronizedSpace synchronizedSpace) {
    return sync(null, synchronizedSpace);
  }

  private SyncQuery sync(String syncToken, SynchronizedSpace synchronizedSpace) {
    if (preview) {
      syncToken = null;
      synchronizedSpace = null;
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
   * @return the space for this client (synchronously).
   */
  public CDASpace fetchSpace() {
    return observeSpace().toBlocking().first();
  }

  /**
   * Asynchronously fetch the space.
   *
   * @param <C> the type of the callback to be used.
   * @param callback the value of the callback to be called back.
   * @return the space for this client (asynchronously).
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDASpace>> C fetchSpace(C callback) {
    return (C) Callbacks.subscribeAsync(observeSpace(), callback, this);
  }

  /**
   * @return an {@link Observable} that fetches the space for this client.
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

  /**
   * Clear the java internal cache.
   *
   * @return this client for chaining.
   */
  public CDAClient clearCache() {
    cache.clear();
    return this;
  }

  static String createUserAgent() {
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
   * @return a {@link CDAClient} builder.
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
    boolean useTLS12;

    /**
     * Sets the space ID.
     *
     * @param space the space id to be set.
     * @return this builder for chaining.
     */
    public Builder setSpace(String space) {
      this.space = space;
      return this;
    }

    /**
     * Sets the space access token.
     *
     * @param token the access token, sometimes called authorization token.
     * @return this builder for chaining.
     */
    public Builder setToken(String token) {
      this.token = token;
      return this;
    }

    /**
     * Sets a custom endpoint.
     *
     * @param endpoint the url to be calling to (i.e. https://cdn.contentful.com).
     * @return this builder for chaining.
     */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    /**
     * Sets a custom logger level.
     *
     * If set to {@link Logger.Level}.NONE any custom logger will get ignored.
     *
     * @param logLevel the amount/level of logging to be used.
     * @return this builder for chaining.
     */
    public Builder setLogLevel(Logger.Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    /**
     * Sets a custom logger.
     *
     * @param logger the logger to be set.
     * @return this builder for chaining.
     */
    public Builder setLogger(Logger logger) {
      this.logger = logger;
      return this;
    }

    /**
     * Sets the endpoint to point the Preview API.
     *
     * @return this builder for chaining.
     */
    public Builder preview() {
      preview = true;
      return this.setEndpoint(Constants.ENDPOINT_PREVIEW);
    }

    /**
     * Sets a custom HTTP call factory.
     * @param callFactory the factory to be used to create a call.
     * @return this builder for chaining.
     */
    public Builder setCallFactory(Call.Factory callFactory) {
      this.callFactory = callFactory;
      return this;
    }

    private Call.Factory createOrGetCallFactory(Builder clientBuilder) {
      final Call.Factory callFactory;

      if (clientBuilder.callFactory == null) {
        callFactory = defaultCallFactoryBuilder().build();
      } else {
        callFactory = clientBuilder.callFactory;
      }

      return callFactory;
    }

    private OkHttpClient.Builder setLogger(OkHttpClient.Builder okBuilder) {
      if (logger != null) {
        switch (logLevel) {
          case NONE:
            break;
          case BASIC:
            return okBuilder.addInterceptor(new LogInterceptor(logger));
          case FULL:
            return okBuilder.addNetworkInterceptor(new LogInterceptor(logger));
        }
      } else {
        if (logLevel != Logger.Level.NONE) {
          throw new IllegalArgumentException("Cannot log to a null logger. Please set either logLevel to None, or do set a Logger");
        }
      }
      return okBuilder;
    }

    private OkHttpClient.Builder useTLS12IfWanted(OkHttpClient.Builder okBuilder) {
      if (useTLS12) {
        try {
          okBuilder.sslSocketFactory(new TLSSocketFactory());
        } catch (Exception e) {
          throw new IllegalArgumentException("Cannot create TLSSocketFactory for TLS 1.2", e);
        }
      }

      return okBuilder;
    }

    /**
     * Returns the default Call.Factory.Builder used throughout this SDK.
     * <p>
     * Please use this method last in the building step, since changing settings as in the
     * {@link #token} or others afterwards will not be reflected by this factory.
     * <p>
     * This might be useful if you want to augment the default client, without needing to rely on
     * replicating the current sdk behaviour.
     *
     * @return A {@link Call.Factory} used through out SDK, as if no custom call factory was used.
     */
    public OkHttpClient.Builder defaultCallFactoryBuilder() {
      OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
          .addInterceptor(new AuthorizationHeaderInterceptor(token))
          .addInterceptor(new UserAgentHeaderInterceptor(createUserAgent()))
          .addInterceptor(new ErrorInterceptor());

      setLogger(okBuilder);
      useTLS12IfWanted(okBuilder);

      return okBuilder;
    }

    /**
     * Sets the flag of enforcing TLS 1.2.
     * 
     * If this is not used, TLS 1.2 may not be used per default on all
     * configurations. 
     *
     * @return this builder for chaining.
     *
     * @see <a href="https://developer.android.com/reference/javax/net/ssl/SSLSocket.html">reference</a>
     */
    public Builder useTLS12() {
      this.useTLS12 = true;
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
