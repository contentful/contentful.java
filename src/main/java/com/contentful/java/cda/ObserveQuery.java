package com.contentful.java.cda;

import io.reactivex.Flowable;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.CDAType.LOCALE;
import static com.contentful.java.cda.Util.typeForClass;

/**
 * Represents a query to the Delivery API which may be invoked via an {@link Flowable}
 * subscription.
 * <p>
 * Observable requests are subscribed and observed on the same thread that executed
 * the request. Call {@link Flowable#subscribeOn(io.reactivex.Scheduler)} and
 * {@link Flowable#observeOn(io.reactivex.Scheduler)} to control that.
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
  @SuppressWarnings("unchecked")
  public Flowable<T> one(final String id) {
    Flowable<T> flowable = where("sys.id", id).all().map(array -> {
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
      } else if (LOCALE.equals(resourceType)) {
        T found = findById(array, id);
        if (found == null) {
          throw new CDAResourceNotFoundException(type, id);
        }
        return found;
      } else {
        throw new IllegalArgumentException("Cannot invoke query for type: " + type.getName());
      }
    });

    if (CONTENTTYPE.equals(typeForClass(type))) {
      flowable = flowable.map(t -> {
        if (t != null) {
          client.cache.types().put(t.id(), (CDAContentType) t);
        }
        return t;
      });
    }
    return flowable;
  }

  T findById(CDAArray array, String id) {
    for (int i = 0; i < array.items.size(); ++i) {
      final CDAResource item = array.items.get(i);
      if (item.id().equals(id)) {
        return (T) item;
      }
    }

    return null;
  }

  /**
   * Observe an array of all resources matching the type of this query.
   *
   * @return {@link Flowable} instance.
   */
  public Flowable<CDAArray> all() {
    return client.cacheAll(false)
        .flatMap(
            cache -> client.service.array(client.spaceId, client.environmentId, path(), params)
        ).map(response -> ResourceFactory.array(response, client));
  }
}
