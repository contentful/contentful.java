package com.contentful.java.cda;

//BEGIN TO LONG CODE LINES

import com.contentful.java.cda.interceptor.AuthorizationHeaderInterceptor;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version;
import com.contentful.java.cda.interceptor.ErrorInterceptor;
import com.contentful.java.cda.interceptor.HeaderInterceptor;
import com.contentful.java.cda.interceptor.LogInterceptor;
import com.contentful.java.cda.interceptor.UserAgentHeaderInterceptor;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.reactivestreams.Publisher;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.Base64;

import static com.contentful.java.cda.Constants.ENDPOINT_PROD;
import static com.contentful.java.cda.Constants.PATH_CONTENT_TYPES;
import static com.contentful.java.cda.Constants.PATH_LOCALES;
import static com.contentful.java.cda.ResourceFactory.fromArrayToItems;
import static com.contentful.java.cda.ResourceFactory.fromResponse;
import static com.contentful.java.cda.Tls12Implementation.useRecommendation;
import static com.contentful.java.cda.Util.checkNotNull;
import static com.contentful.java.cda.build.GeneratedBuildParameters.PROJECT_VERSION;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.os;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.platform;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.sdk;
import static javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm;
//END TO LONG CODE LINES

/**
 * Client to be used when requesting information from the Delivery API. Every client is associated
 * with exactly one Space, but there is no limit to the concurrent number of clients existing at
 * any one time. Avoid creating multiple clients for the same Space. Use {@link #builder()}
 * to create a new client instance.
 */
public class CDAClient {
  private static final int CONTENT_TYPE_LIMIT_MAX = 1000;
  private static final int CROSS_SPACE_TOKENS_MAX = 20;

  final String spaceId;

  final String environmentId;

  final String token;

  final CDAService service;

  final Cache cache;

  final Executor callbackExecutor;

  final boolean preview;

  final boolean logSensitiveData;

  final boolean hasCrossSpaceTokens;

  CDAClient(Builder builder) {
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
    this.environmentId = builder.environment;
    this.token = builder.token;
    this.preview = builder.preview;
    this.logSensitiveData = builder.logSensitiveData;
    this.hasCrossSpaceTokens = builder.crossSpaceTokens != null
        && !builder.crossSpaceTokens.isEmpty();
  }

  private void validate(Builder builder) {
    checkNotNull(builder.space, "Space ID must be provided.");
    checkNotNull(builder.environment, "Environment ID must not be null.");

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
            .addConverterFactory(clientBuilder.createOrGetConverterFactory(clientBuilder))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .callFactory(clientBuilder.createOrGetCallFactory(clientBuilder))
            .baseUrl(endpoint);

    return retrofitBuilder.build().create(CDAService.class);
  }

  /**
   * Returns a {@link FetchQuery} for a given {@code type}, which can be used to fulfil the
   * request synchronously or asynchronously when a callback is provided.
   *
   * @param type resource type. This can be either a {@link CDALocale}, a {@link CDAEntry},
   *             a {@link CDAAsset}, or a {@link CDAContentType}
   * @param <T>  type for avoiding casting on calling side.
   * @return A query to call {@link FetchQuery#all()} or {@link FetchQuery#one(String)} on it.
   * @see #fetchSpace()
   */
  public <T extends CDAResource> FetchQuery<T> fetch(Class<T> type) {
    return new FetchQuery<>(type, this);
  }

  /**
   * Returns a {@link TransformQuery} to transform the default contentful response into a specific
   * custom model type.
   * <p>
   *
   * @param <T> An annotated custom {@link TransformQuery.ContentfulEntryModel} model.
   * @return a query for async calls to Contentful transforming the response to custom types
   * @see TransformQuery.ContentfulEntryModel
   * @see TransformQuery.ContentfulField
   * @see TransformQuery.ContentfulSystemField
   */
  public <T> TransformQuery<T> observeAndTransform(Class<T> type) {
    return new TransformQuery<>(type, this);
  }

  /**
   * Returns an {@link ObserveQuery} for a given {@code type}, which can be used to return
   * an {@link Flowable} that fetches the desired resources.
   *
   * @param type resource type. This can be either a {@link CDALocale}, a {@link CDAEntry},
   *             a {@link CDAAsset}, or a {@link CDAContentType}
   * @param <T>  type for avoiding casting on calling side.
   * @return A query to call {@link ObserveQuery#all()} or {@link ObserveQuery#one(String)} on it.
   * @see #observeSpace()
   */
  public <T extends CDAResource> ObserveQuery<T> observe(Class<T> type) {
    return new ObserveQuery<>(type, this);
  }

  /**
   * Populate the content type cache with _all_ available content types.
   * <p>
   * This method will run through all the content types, saving them in the process and also takes
   * care of paging.
   * <p>
   * This method is synchronous.
   *
   * @return the number of content types cached.
   */
  public int populateContentTypeCache() {
    return observeContentTypeCachePopulation().blockingFirst();
  }

  /**
   * Populate the content type cache with _all_ available content types.
   * <p>
   * This method is synchronous.
   *
   * @param limit the number of content types per page.
   * @return the number of content types cached.
   * @throws IllegalArgumentException if limit is less or equal to 0.
   * @throws IllegalArgumentException if limit is more then 1_000.
   * @see #populateContentTypeCache()
   */
  public int populateContentTypeCache(int limit) {
    if (limit > CONTENT_TYPE_LIMIT_MAX) {
      throw new IllegalArgumentException("Content types per page limit cannot be more then 1000.");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException("Content types per page limit cannot be "
              + "less or equal to 0.");
    }

    return observeContentTypeCachePopulation(limit).blockingFirst();
  }

  /**
   * Populate the content type cache with _all_ available content types.
   * <p>
   * This method will run through all the content types, saving them in the process and also takes
   * care of paging.
   * <p>
   * This method is asynchronous and needs to be subscribed to.
   *
   * @return the flowable representing the asynchronous call.
   */
  public Flowable<Integer> observeContentTypeCachePopulation() {
    return observeContentTypeCachePopulation(CONTENT_TYPE_LIMIT_MAX);
  }

  /**
   * Populate the content type cache with _all_ available content types.
   * <p>
   * This method will run through all the content types, saving them in the process and also takes
   * care of paging.
   * <p>
   * This method is asynchronous and needs to be subscribed to.
   *
   * @param limit the number of content types per page.
   * @return the flowable representing the asynchronous call.
   * @throws IllegalArgumentException if limit is less or equal to 0.
   * @throws IllegalArgumentException if limit is more then 1_000.
   */
  public Flowable<Integer> observeContentTypeCachePopulation(final int limit) {
    if (limit > CONTENT_TYPE_LIMIT_MAX) {
      throw new IllegalArgumentException("Content types per page limit cannot be more then 1000.");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException("Content types per page limit cannot be "
              + "less or equal to 0.");
    }

    return
            observe(CDAContentType.class)
                    .orderBy("sys.id")
                    .limit(limit)
                    .all()
                    .map(
                            new Function<CDAArray, CDAArray>() {
                              @Override
                              public CDAArray apply(CDAArray array) {
                                if (array.skip() + array.limit() < array.total()) {
                                  return nextPage(array);
                                } else {
                                  return array;
                                }
                              }

                              private CDAArray nextPage(CDAArray array) {
                                final CDAArray nextArray = observe(CDAContentType.class)
                                        .orderBy("sys.id")
                                        .limit(limit)
                                        .skip(array.skip + limit)
                                        .all()
                                        .map(this)
                                        .blockingFirst();

                                array.skip = nextArray.skip;
                                array.items.addAll(nextArray.items);
                                array.assets.putAll(nextArray.assets);
                                array.entries.putAll(nextArray.entries);

                                return array;
                              }
                            }
                    )
                    .map(new Function<CDAArray, Integer>() {
                           @Override
                           public Integer apply(CDAArray array) {
                             for (CDAResource resource : array.items) {
                               if (resource instanceof CDAContentType) {
                                 cache.types().put(resource.id(), (CDAContentType) resource);
                               } else {
                                 throw new IllegalStateException(
                                         "Requesting a list of content types should not return "
                                                 + "any other type.");
                               }
                             }
                             return array.total;
                           }
                         }
                    );
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
   * Returns a {@link SyncQuery} for initial synchronization via
   * the Sync API with a specified limit.
   *
   * @param limit the maximum number of entries per page
   * @return query instance.
   */
  public SyncQuery sync(int limit) {
    return sync(null, null, null, limit);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with the provided {@code syncToken} via
   * the Sync API.
   * <p>
   * If called from a {@link #preview} client, this will always do an initial sync.
   *
   * @param type the type to be sync'ed.
   * @return query instance.
   */
  public SyncQuery sync(SyncType type) {
    return sync(null, null, type);
  }

  /**
   * Returns a {@link SyncQuery} for synchronization with the provided {@code syncToken} via
   * the Sync API with a specified limit.
   * <p>
   * If called from a {@link #preview} client, this will always do an initial sync.
   *
   * @param type the type to be sync'ed.
   * @param limit the maximum number of entries per page
   * @return query instance.
   */
  public SyncQuery sync(SyncType type, int limit) {
    return sync(null, null, type, limit);
  }

  public boolean shouldLogSensitiveData() {
    return logSensitiveData;
  }
  /**
   * Returns a {@link SyncQuery} for synchronization with the provided {@code syncToken} via
   * the Sync API.
   * <p>
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
   * <p>
   * If called from a {@link #preview} client, this will always do an initial sync.
   *
   * @param synchronizedSpace space to sync.
   * @return query instance.
   */
  public SyncQuery sync(SynchronizedSpace synchronizedSpace) {
    return sync(null, synchronizedSpace);
  }

  private SyncQuery sync(String syncToken, SynchronizedSpace synchronizedSpace) {
    return sync(syncToken, synchronizedSpace, null);
  }

  private SyncQuery sync(String syncToken, SynchronizedSpace synchronizedSpace, SyncType type) {
    return sync(syncToken, synchronizedSpace, type, null);
  }

  private SyncQuery sync(String syncToken, SynchronizedSpace synchronizedSpace,
                         SyncType type, Integer limit) {
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
    if (type != null) {
      builder.setType(type);
    }
    if (limit != null) {
      builder.setLimit(limit);
    }
    return builder.build();
  }

  /**
   * @return the space for this client (synchronously).
   */
  public CDASpace fetchSpace() {
    return observeSpace().blockingFirst();
  }

  /**
   * Asynchronously fetch the space.
   *
   * @param <C>      the type of the callback to be used.
   * @param callback the value of the callback to be called back.
   * @return the space for this client (asynchronously).
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDASpace>> C fetchSpace(C callback) {
    return (C) Callbacks.subscribeAsync(observeSpace(), callback, this);
  }

  /**
   * @return an {@link Flowable} that fetches the space for this client.
   */
  public Flowable<CDASpace> observeSpace() {
    return service.space(spaceId).map(new Function<Response<CDASpace>, CDASpace>() {
      @Override
      public CDASpace apply(Response<CDASpace> response) throws Exception {
        return ResourceFactory.fromResponse(response);
      }
    });
  }

  /**
   * Caching
   */
  Flowable<Cache> cacheAll(final boolean invalidate) {
    return cacheLocales(invalidate)
            .flatMap(new Function<List<CDALocale>,
                    Publisher<? extends Map<String, CDAContentType>>>() {
              @Override
              public Publisher<? extends Map<String, CDAContentType>>
                apply(List<CDALocale> locales) {
                  return CDAClient.this.cacheTypes(invalidate);
              }
            })
            .map(new Function<Map<String, CDAContentType>, Cache>() {
              @Override
              public Cache apply(Map<String, CDAContentType> stringCDAContentTypeMap) {
                return cache;
              }
            });
  }

  Flowable<List<CDALocale>> cacheLocales(boolean invalidate) {
    List<CDALocale> locales = invalidate ? null : cache.locales();
    if (locales == null) {
      return service.array(spaceId, environmentId, PATH_LOCALES, new HashMap<>())
              .map(new Function<Response<CDAArray>, List<CDALocale>>() {
                     @Override
                     public List<CDALocale> apply(Response<CDAArray> localesResponse) {
                       final List<CDALocale> locales1 =
                               fromArrayToItems(fromResponse(localesResponse));
                       cache.setLocales(locales1);
                       return locales1;
                     }
                   }
              );
    }
    return Flowable.just(locales);
  }

  Flowable<Map<String, CDAContentType>> cacheTypes(boolean invalidate) {
    Map<String, CDAContentType> types = invalidate ? null : cache.types();
    if (types == null) {
      return service.array(
              spaceId,
              environmentId,
              PATH_CONTENT_TYPES,
              new HashMap<>()
      ).map(new Function<Response<CDAArray>, Map<String, CDAContentType>>() {
              @Override
              public Map<String, CDAContentType> apply(Response<CDAArray> arrayResponse) {
                CDAArray array = ResourceFactory.array(arrayResponse, CDAClient.this);
                Map<String, CDAContentType> tmp = new ConcurrentHashMap<>();
                for (CDAResource resource : array.items()) {
                  tmp.put(resource.id(), (CDAContentType) resource);
                }
                cache.setTypes(tmp);
                return tmp;
              }
            }
      );
    }
    return Flowable.just(types);
  }

  Flowable<CDAContentType> cacheTypeWithId(String id) {
    CDAContentType contentType = cache.types().get(id);
    if (contentType == null) {
      return observe(CDAContentType.class)
              .one(id)
              .map(new Function<CDAContentType, CDAContentType>() {
                     @Override
                     public CDAContentType apply(CDAContentType resource) {
                       if (resource != null) {
                         cache.types().put(resource.id(), resource);
                       }
                       return resource;
                     }
                   }
              );
    }
    return Flowable.just(contentType);
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
            PROJECT_VERSION,
            properties.getProperty("java.runtime.name"),
            properties.getProperty("java.runtime.version"),
            properties.getProperty("os.name"),
            properties.getProperty("os.version")
    );
  }

  static Section[] createCustomHeaderSections(Section application, Section integration) {
    final Properties properties = System.getProperties();

    final Platform platform = Platform.get();
    return new Section[]{
            sdk(
                    "contentful.java",
                    Version.parse(PROJECT_VERSION)),
            platform(
                    "java",
                    Version.parse(properties.getProperty("java.runtime.version"))
            ),
            os(
                    OperatingSystem.parse(platform.name()),
                    Version.parse(platform.version())
            ),
            application,
            integration
    };
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
    String space;
    String environment = Constants.DEFAULT_ENVIRONMENT;
    String token;
    String endpoint;

    Logger logger;
    Logger.Level logLevel = Logger.Level.NONE;

    Call.Factory callFactory;
    Converter.Factory converterFactory;

    boolean preview;

    private boolean logSensitiveData = true;
    Tls12Implementation tls12Implementation = useRecommendation;

    Section application;
    Section integration;

    Map<String, String> crossSpaceTokens;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    Builder() {
    }

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
     * Sets the environment ID.
     *
     * @param environment the space id to be set.
     * @return this builder for chaining.
     */
    public Builder setEnvironment(String environment) {
      this.environment = environment;
      return this;
    }

    /**
     * Sets the logSensitiveData value for logging
     * the authorization headers in the CDAHttpException.
     *
     * @param logSensitiveData boolean value to be set.
     * @return this builder for chaining.
     */
    public Builder setLogSensitiveData(boolean logSensitiveData) {
      this.logSensitiveData = logSensitiveData;
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
     * <p>
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
     *
     * @param callFactory the factory to be used to create a call.
     * @return this builder for chaining.
     */
    public Builder setCallFactory(Call.Factory callFactory) {
      this.callFactory = callFactory;
      return this;
    }

    Call.Factory createOrGetCallFactory(Builder clientBuilder) {
      final Call.Factory callFactory;

      if (clientBuilder.callFactory == null) {
        callFactory = defaultCallFactoryBuilder().build();
      } else {
        callFactory = clientBuilder.callFactory;
      }

      return callFactory;
    }

    /**
     * Sets a custom converter factory.
     *
     * @param converterFactory the factory to be used to convert.
     * @return this builder for chaining.
     */
    public Builder setConverterFactory(Converter.Factory converterFactory) {
      this.converterFactory = converterFactory;
      return this;
    }

    Converter.Factory createOrGetConverterFactory(Builder clientBuilder) {
      final Converter.Factory converterFactory;

      if (clientBuilder.converterFactory == null) {
        converterFactory = GsonConverterFactory.create(ResourceFactory.GSON);
      } else {
        converterFactory = clientBuilder.converterFactory;
      }

      return converterFactory;
    }

    /**
     * Encodes cross-space tokens to a base64-encoded JSON string for the
     * x-contentful-resource-resolution header.
     *
     * @param tokens map of space IDs to access tokens
     * @return base64-encoded JSON string
     */
    private String encodeCrossSpaceTokens(Map<String, String> tokens) {
      Map<String, Map<String, String>> payload = new HashMap<>();
      payload.put("spaces", tokens);
      String json = ResourceFactory.GSON.toJson(payload);
      return Base64.getEncoder().encodeToString(
              json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private OkHttpClient.Builder setLogger(OkHttpClient.Builder okBuilder) {
      if (logger != null) {
        switch (logLevel) {
          case BASIC:
            return okBuilder.addInterceptor(new LogInterceptor(logger));
          case FULL:
            return okBuilder.addNetworkInterceptor(new LogInterceptor(logger));
          case NONE:
            break;
          default:
            break;
        }
      } else {
        if (logLevel != Logger.Level.NONE) {
          throw new IllegalArgumentException(
                  "Cannot log to a null logger. "
                          + "Please set either logLevel to None, or do set a Logger"
          );
        }
      }
      return okBuilder;
    }

    private OkHttpClient.Builder useTls12IfWanted(OkHttpClient.Builder okBuilder) {
      if (isSdkTlsSocketFactoryWanted()) {
        try {
          okBuilder.sslSocketFactory(new TlsSocketFactory(), getX509TrustManager());
        } catch (GeneralSecurityException exception) {
          throw new IllegalArgumentException(
                  "Cannot create TlsSocketFactory for TLS 1.2. "
                          + "Please consider using 'setTls12Implementation(systemProvided)', "
                          + "or update to a system providing TLS 1.2 support.",
                  exception);
        }
      }

      return okBuilder;
    }

    X509TrustManager getX509TrustManager() throws NoSuchAlgorithmException, KeyStoreException {
      final TrustManagerFactory trustManagerFactory =
              TrustManagerFactory.getInstance(getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);

      return extractX509TrustManager(trustManagerFactory.getTrustManagers());
    }

    X509TrustManager extractX509TrustManager(TrustManager[] trustManagers)
            throws NoSuchAlgorithmException {
      if (trustManagers != null) {
        for (final TrustManager manager : trustManagers) {
          if (manager instanceof X509TrustManager) {
            return (X509TrustManager) manager;
          }
        }
      }

      throw new NoSuchAlgorithmException(
              "Cannot find a 'X509TrustManager' in system provided managers: '"
                      + Arrays.toString(trustManagers) + "'.");
    }

    boolean isSdkTlsSocketFactoryWanted() {
      switch (tls12Implementation) {
        case sdkProvided:
          return true;
        case systemProvided:
          return false;
        default:
        case useRecommendation:
          return Platform.get().needsCustomTLSSocketFactory();
      }
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
      final Section[] sections = createCustomHeaderSections(application, integration);

      OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
              .connectionPool(OK_HTTP_CLIENT.connectionPool())
              .addInterceptor(new AuthorizationHeaderInterceptor(token))
              .addInterceptor(new UserAgentHeaderInterceptor(createUserAgent()))
              .addInterceptor(new ContentfulUserAgentHeaderInterceptor(sections));

      // Add cross-space resolution header if tokens are configured
      if (crossSpaceTokens != null && !crossSpaceTokens.isEmpty()) {
        String encodedHeader = encodeCrossSpaceTokens(crossSpaceTokens);
        okBuilder.addInterceptor(new HeaderInterceptor(
                "x-contentful-resource-resolution", encodedHeader));
      }

      okBuilder.addInterceptor(new ErrorInterceptor(logSensitiveData));

      setLogger(okBuilder);
      useTls12IfWanted(okBuilder);

      return okBuilder;
    }

    /**
     * Overwrite the recommendation from the SDK for using a custom TLS12 socket factory.
     * <p>
     * This SDK recommends a TLS12 socket factory to be used: Either the system one, or an SDK owned
     * implementation. If this recommendation does not fit your needs, feel free to overwrite the
     * recommendation here.
     * <p>
     * Some operation systems and frameworks, esp. Android and Java 1.6, might opt for implementing
     * TLS12 (enforced by Contentful) but do not enable it. The SDK tries to find those situations
     * and recommends to either use the system TLSSocketFactory or a SDK provided one.
     *
     * @param implementation which implementation to be used.
     * @return this builder for ease of chaining.
     */
    public Builder setTls12Implementation(Tls12Implementation implementation) {
      this.tls12Implementation = implementation;
      return this;
    }

    /**
     * Tell the client which application this is.
     * <p>
     * It might be used for internal tracking of Contentfuls tools.
     *
     * @param name    the name of the app.
     * @param version the version in semver of the app.
     * @return this builder for chaining.
     */
    public Builder setApplication(String name, String version) {
      this.application = Section.app(name, Version.parse(version));
      return this;
    }

    /**
     * Set the name of the integration.
     * <p>
     * This custom user agent header will be used for libraries build on top of this library.
     *
     * @param name    of the integration.
     * @param version version of the integration.
     * @return this builder for chaining.
     */
    public Builder setIntegration(String name, String version) {
      this.integration = Section.integration(name, Version.parse(version));
      return this;
    }

    /**
     * Sets cross-space tokens for resolving cross-space references.
     * <p>
     * This enables automatic resolution of entries and assets from other spaces by providing
     * access tokens for those spaces. Cross-space resources will be included in the response's
     * includes section.
     * <p>
     * The maximum number of extra spaces supported is 20 (21 total including the main space).
     *
     * @param spaceIdToToken a map of space IDs to their corresponding access tokens (CDA tokens).
     * @return this builder for chaining.
     * @throws IllegalArgumentException if the map is null or contains more than 20 spaces.
     */
    public Builder setCrossSpaceTokens(Map<String, String> spaceIdToToken) {
      checkNotNull(spaceIdToToken, "Cross-space tokens map must not be null.");
      if (spaceIdToToken.size() > CROSS_SPACE_TOKENS_MAX) {
        throw new IllegalArgumentException(
            String.format("Maximum %d extra spaces supported "
                            + "for cross-space resolution, but %d provided.",
                CROSS_SPACE_TOKENS_MAX, spaceIdToToken.size()));
      }
      this.crossSpaceTokens = spaceIdToToken;
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