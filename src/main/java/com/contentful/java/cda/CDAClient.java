package com.contentful.java.cda;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

import static com.contentful.java.cda.Constants.ENDPOINT_PROD;
import static com.contentful.java.cda.Constants.PATH_CONTENT_TYPES;
import static com.contentful.java.cda.Util.checkNotNull;

public final class CDAClient {
  final String spaceId;

  final String token;

  final CDAService service;

  final Cache cache;

  final Executor callbackExecutor;

  private CDAClient(Builder builder) {
    validate(builder);
    this.spaceId = builder.space;
    this.token = builder.token;
    this.service = createService(builder);
    this.cache = new Cache();
    this.callbackExecutor = Platform.get().callbackExecutor();
  }

  private void validate(Builder builder) {
    checkNotNull(builder.space, "Space ID must be provided.");
    checkNotNull(builder.token, "Access token must be provided.");
  }

  private CDAService createService(Builder clientBuilder) {
    String endpoint = clientBuilder.endpoint;
    if (endpoint == null) {
      endpoint = ENDPOINT_PROD;
    }

    RestAdapter.Builder restBuilder = new RestAdapter.Builder()
        .setEndpoint(endpoint)
        .setRequestInterceptor(new Interceptor(token));

    setLogLevel(restBuilder, clientBuilder);
    return restBuilder.build().create(CDAService.class);
  }

  private void setLogLevel(RestAdapter.Builder restBuilder, Builder clientBuilder) {
    if (clientBuilder.logLevel != null) {
      restBuilder.setLogLevel(clientBuilder.logLevel);
    }
  }

  public <T extends CDAResource> ObserveQuery<T> observe(Class<T> type) {
    return new ObserveQuery<T>(type, this);
  }

  public <T extends CDAResource> FetchQuery<T> fetch(Class<T> type) {
    return new FetchQuery<T>(type, this);
  }

  Observable<CDASpace> cacheSpace(boolean invalidate) {
    CDASpace space = invalidate ? null : cache.space();
    if (space == null) {
      return service.space(spaceId).map(new Func1<Response, CDASpace>() {
        @Override public CDASpace call(Response response) {
          CDASpace tmp = ResourceFactory.space(response);
          cache.setSpace(tmp);
          return tmp;
        }
      });
    }
    return Observable.just(space);
  }

  Observable<Map<String, CDAContentType>> cacheTypes(boolean invalidate) {
    Map<String, CDAContentType> types = invalidate ? null : cache.types();
    if (types == null) {
      return service.array(spaceId, PATH_CONTENT_TYPES, null).map(
          new Func1<Response, Map<String, CDAContentType>>() {
            @Override public Map<String, CDAContentType> call(Response response) {
              CDAArray array = ResourceFactory.array(response, CDAClient.this);
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
          cache.types().put(resource.id(), resource);
          return resource;
        }
      });
    }
    return Observable.just(contentType);
  }

  public Observable<CDASpace> observeSpace() {
    return cacheSpace(true);
  }

  public CDASpace fetchSpace() {
    return observeSpace().toBlocking().first();
  }

  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDASpace>> C fetchSpace(C callback) {
    return (C) Callbacks.subscribeAsync(observeSpace(), callback, this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
    }

    String space;

    String token;

    String endpoint;

    LogLevel logLevel;

    public Builder setSpace(String space) {
      this.space = space;
      return this;
    }

    public Builder setToken(String token) {
      this.token = token;
      return this;
    }

    public Builder setEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public Builder setLogLevel(LogLevel logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public Builder preview() {
      return this.setEndpoint(Constants.ENDPOINT_PREVIEW);
    }

    public CDAClient build() {
      return new CDAClient(this);
    }
  }
}
