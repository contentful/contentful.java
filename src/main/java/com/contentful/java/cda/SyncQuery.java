package com.contentful.java.cda;

import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

import static com.contentful.java.cda.Util.checkNotNull;

public final class SyncQuery {
  final CDAClient client;

  final String syncToken;

  final SynchronizedSpace space;

  final boolean initial;

  private SyncQuery(Builder builder) {
    this.client = checkNotNull(builder.client, "Client must not be null.");
    this.syncToken = builder.syncToken;
    this.space = builder.space;
    this.initial = space == null && syncToken == null;
  }

  public Observable<SynchronizedSpace> observe() {
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
        .flatMap(new Func1<Cache, Observable<Response>>() {
          @Override public Observable<Response> call(Cache cache) {
            return client.service.sync(client.spaceId, token == null, token);
          }
        }).map(new Func1<Response, SynchronizedSpace>() {
          @Override public SynchronizedSpace call(Response response) {
            return ResourceFactory.sync(response, space, client);
          }
        });
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    CDAClient client;

    String syncToken;

    SynchronizedSpace space;

    public Builder setClient(CDAClient client) {
      this.client = client;
      return this;
    }

    public Builder setSyncToken(String syncToken) {
      this.syncToken = syncToken;
      return this;
    }

    public Builder setSpace(SynchronizedSpace space) {
      this.space = space;
      return this;
    }

    public SyncQuery build() {
      return new SyncQuery(this);
    }
  }
}
