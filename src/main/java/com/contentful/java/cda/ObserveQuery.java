package com.contentful.java.cda;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import retrofit2.Response;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Util.typeForClass;

/**
 * Represents a query to the Delivery API which may be invoked via an {@link Flowable}
 * subscription.
 * <p>
 * Observable requests are subscribed and observed on the same thread that executed
 * the request. Call {@link Flowable#subscribeOn(Scheduler)} and {@link Flowable#observeOn(Scheduler)}
 * to control that.
 */
public class ObserveQuery<T extends CDAResource> extends AbsQuery<T, ObserveQuery<T>> {
  ObserveQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  /**
   * Observe a resource matching the given {@code id}.
   *
   * @param id resource id.
   * @return {@link Flowable} instance.
   * @throws CDAResourceNotFoundException if resource was not found.
   */
  public Flowable<T> one(final String id) {
    Flowable<T> flowable = where("sys.id", id).all().map(new Function<CDAArray, T>() {
      @Override @SuppressWarnings("unchecked") public T apply(CDAArray array) {
        if (array.items().size() == 0) {
          throw new CDAResourceNotFoundException(type, id);
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
      flowable = flowable.map(new Function<T, T>() {
        @Override public T apply(T t) {
          if (t != null) {
            client.cache.types().put(t.id(), (CDAContentType) t);
          }
          return t;
        }
      });
    }
    return flowable;
  }

  /**
   * Observe an array of all resources matching the type of this query.
   *
   * @return {@link Flowable} instance.
   */
  public Flowable<CDAArray> all() {
    return client.cacheAll(false)
        .flatMap(new Function<Cache, Flowable<Response<CDAArray>>>() {
          @Override public Flowable<Response<CDAArray>> apply(Cache cache) {
            return client.service.array(client.spaceId, path(), params);
          }
        }).map(new Function<Response<CDAArray>, CDAArray>() {
          @Override public CDAArray apply(Response<CDAArray> response) {
            return ResourceFactory.array(response, client);
          }
        });
  }
}
