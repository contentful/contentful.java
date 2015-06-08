package com.contentful.java.cda;

import java.util.Map;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.Util.typeForClass;

public final class ObserveQuery<T extends CDAResource> extends AbsQuery<T, ObserveQuery<T>> {
  ObserveQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  public Observable<T> one(final String id) {
    Observable<T> observable = where("sys.id", id).all().map(new Func1<CDAArray, T>() {
      @Override @SuppressWarnings("unchecked") public T call(CDAArray array) {
        CDAType resourceType = typeForClass(type);
        Map<String, ? extends CDAResource> map;
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
          client.cache.types().put(t.id(), (CDAContentType) t);
          return t;
        }
      });
    }
    return observable;
  }

  public Observable<CDAArray> all() {
    return client.cacheSpace(false)
        .flatMap(new Func1<CDASpace, Observable<Map<String, CDAContentType>>>() {
          @Override public Observable<Map<String, CDAContentType>> call(CDASpace space) {
            return client.cacheTypes(false);
          }
        })
        .flatMap(new Func1<Map<String, CDAContentType>, Observable<Response>>() {
          @Override public Observable<Response> call(Map<String, CDAContentType> contentTypes) {
            return client.service.array(client.spaceId, path(), params);
          }
        })
        .map(new Func1<Response, CDAArray>() {
          @Override public CDAArray call(Response response) {
            return ResourceFactory.array(response, client);
          }
        });
  }
}
