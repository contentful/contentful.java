package com.contentful.java.cda;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import org.reactivestreams.Publisher;
import retrofit2.Response;

import static com.contentful.java.cda.Util.checkNotNull;

/**
 * Represents a query to the Sync API.
 */
public class SyncQuery {
  final CDAClient client;

  final String syncToken;

  final SynchronizedSpace space;

  final boolean initial;

  final SyncType type;

  final Integer limit;

  SyncQuery(Builder builder) {
    this.client = checkNotNull(builder.client, "Client must not be null.");
    this.syncToken = builder.syncToken;
    this.space = builder.space;
    this.initial = builder.isInitial();
    this.type = builder.type;
    this.limit = builder.limit;
  }

  /**
   * Returns an {@link Flowable} to which one can subscribe in order to fulfill this sync query.
   *
   * @return {@link Flowable} instance.
   */
  public Flowable<SynchronizedSpace> observe() {
    final String token;
    if (space != null) {
      String nextSyncUrl = space.nextSyncUrl();
      if (nextSyncUrl == null) {
        throw new IllegalArgumentException("Provided space for synchronization is corrupt.");
      } else {
        token = Util.queryParam(space.nextSyncUrl(), "sync_token");
      }
    } else {
      token = syncToken;
    }
    return client.cacheAll(true)
            .flatMap(new Function<Cache, Publisher<Response<SynchronizedSpace>>>() {
                       @Override
                       public Publisher<Response<SynchronizedSpace>> apply(Cache cache) {
                         return client.service.sync(
                                 client.spaceId,
                                 client.environmentId,
                                 initial ? initial : null,
                                 token,
                                 initial && type != null ? type.getName() : null,
                                 initial && type != null ? type.getContentType() : null,
                                 limit);
                       }
                     }
            ).map(
                    new Function<Response<SynchronizedSpace>, SynchronizedSpace>() {
                      @Override
                      public SynchronizedSpace apply(
                              Response<SynchronizedSpace> synchronizedSpace) {
                        return ResourceFactory.sync(synchronizedSpace, space, client);
                      }
                    }
            );
  }

  /**
   * Invokes the request to sync (blocking).
   *
   * @return {@link SynchronizedSpace} instance.
   */
  public SynchronizedSpace fetch() {
    return observe().blockingFirst();
  }

  /**
   * Invokes the request to sync (asynchronously) with the provided {@code callback}.
   *
   * @param callback callback.
   * @param <C>      callback type.
   * @return the given callback instance.
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<SynchronizedSpace>> C fetch(C callback) {
    return (C) Callbacks.subscribeAsync(observe(), callback, client);
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    CDAClient client;

    String syncToken;

    SynchronizedSpace space;

    SyncType type;

    Integer limit;

    Builder setClient(CDAClient client) {
      this.client = client;
      return this;
    }

    Builder setSyncToken(String syncToken) {
      this.syncToken = syncToken;
      return this;
    }

    Builder setSpace(SynchronizedSpace space) {
      this.space = space;
      return this;
    }

    Builder setType(SyncType type) {
      if (isInitial()) {
        this.type = type;
      }
      return this;
    }

    /**
     * Sets the limit for the number of entries to be returned per page.
     * This parameter is only used for initial sync.
     *
     * @param limit the maximum number of entries per page
     * @return this builder instance
     */
    Builder setLimit(Integer limit) {
      if (isInitial()) {
        this.limit = limit;
      }
      return this;
    }

    boolean isInitial() {
      return space == null && syncToken == null;
    }

    SyncQuery build() {
      return new SyncQuery(this);
    }
  }
}
