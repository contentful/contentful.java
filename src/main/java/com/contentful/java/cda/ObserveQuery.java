package com.contentful.java.cda;

import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Util.typeForClass;

/**
 * Represents a query to the Delivery API which may be invoked via an {@link Observable}
 * subscription.
 *
 * Observable requests are subscribed and observed on the same thread that executed
 * the request. Call {@link Observable#subscribeOn(Scheduler)} and {@link Observable#observeOn(Scheduler)}
 * to control that.
 */
public class ObserveQuery<T extends CDAResource> extends AbsQuery<T, ObserveQuery<T>> {
  ObserveQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  /**
   * Observe a resource matching the given {@code id}.
   * @param id resource id.
   * @return {@link Observable} instance.
   */
  public Observable<T> one(final String id) {
    Observable<T> observable = where("sys.id", id).all().map(new Func1<CDAArray, T>() {
      @Override @SuppressWarnings("unchecked") public T call(CDAArray array) {
        if (array.items().size() == 0) {
          return null;
        }
        CDAType resourceType = typeForClass(type);
        if (ASSET.equals(resourceType)) {
          return (T) array.assets().get(id);
        } else if (ENTRY.equals(resourceType)) {
          return (T) array.entries().get(id);
        } else if (CONTENTTYPE.equals(resourceType)) {
          return (T) array.items().get(0);
        } else {
          throw new IllegalArgumentException("Cannot invoke query for type: " + type.getName());
        }
      }
    });

    if (CONTENTTYPE.equals(typeForClass(type))) {
      observable = observable.map(new Func1<T, T>() {
        @Override public T call(T t) {
          if (t != null) {
            client.cache.types().put(t.id(), (CDAContentType) t);
          }
          return t;
        }
      });
    }
    return observable;
  }

  /**
   * Observe an array of all resources matching the type of this query.
   * @return {@link Observable} instance.
   */
  public Observable<CDAArray> all() {
    return client.cacheAll(false)
        .flatMap(new Func1<Cache, Observable<Response<CDAArray>>>() {
          @Override public Observable<Response<CDAArray>> call(Cache cache) {
            return client.service.array(client.spaceId, path(), params);
          }
        }).map(new Func1<Response<CDAArray>, CDAArray>() {
          @Override public CDAArray call(Response<CDAArray> response) {
            return ResourceFactory.array(response, client);
          }
        });
  }
}
