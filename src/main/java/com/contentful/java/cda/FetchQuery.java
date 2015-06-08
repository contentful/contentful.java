package com.contentful.java.cda;

public final class FetchQuery<T extends CDAResource> extends AbsQuery<T, ObserveQuery<T>> {
  public FetchQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  public T one(String id) {
    return baseQuery().one(id).toBlocking().first();
  }

  @SuppressWarnings("unchecked")
  public <C extends CDACallback<T>> C one(String id, C callback) {
    return (C) Callbacks.subscribeAsync(baseQuery().one(id), callback, client);
  }

  public CDAArray all() {
    return baseQuery().all().toBlocking().first();
  }

  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDAArray>> C all(C callback) {
    return (C) Callbacks.subscribeAsync(baseQuery().all(), callback, client);
  }

  private ObserveQuery<T> baseQuery() {
    return client.observe(type).where(params);
  }
}
