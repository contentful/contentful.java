package com.contentful.java.cda;

public final class FetchQuery<T extends CDAResource> extends AbsQuery<T, ObserveQuery<T>> {
  public FetchQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  public T one(String id) {
    return client.observe(type).one(id).toBlocking().first();
  }

  public CDAArray all() {
    return client.observe(type).where(params).all().toBlocking().first();
  }
}
