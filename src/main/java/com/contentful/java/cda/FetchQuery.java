package com.contentful.java.cda;

/**
 * Represents a query to the Delivery API which may be invoked synchronously or asynchronously
 * with a callback.
 */
public class FetchQuery<T extends CDAResource> extends AbsQuery<T, FetchQuery<T>> {
  /**
   * Create a FetchQuery for the given type, using the client.
   *
   * @param type   is the ContentType class to be queried for.
   * @param client a valid client to be used for the actual data retrieval.
   */
  public FetchQuery(Class<T> type, CDAClient client) {
    super(type, client);
  }

  /**
   * Fetch and return a resource matching the given {@code id}.
   *
   * @param id resource id.
   * @return result resource, null if it does not exist.
   */
  public T one(String id) {
    try {
      return baseQuery().one(id).blockingFirst();
    } catch (NullPointerException e) {
      throw new CDAResourceNotFoundException(type, id);
    }
  }

  /**
   * Async fetch resource matching the given {@code id}.
   *
   * @param id       resource id.
   * @param callback callback.
   * @param <C>      callback type.
   * @return the given {@code callback} instance.
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<T>> C one(String id, C callback) {
    return (C) Callbacks.subscribeAsync(baseQuery().one(id), callback, client);
  }

  /**
   * Fetch and return all resources matching the type of this query.
   *
   * @return {@link CDAArray} result.
   */
  public CDAArray all() {
    return baseQuery().all().blockingFirst();
  }

  /**
   * Async fetch all resources matching the type of this query.
   *
   * @param callback callback.
   * @param <C>      callback type.
   * @return the given {@code callback} instance.
   */
  @SuppressWarnings("unchecked")
  public <C extends CDACallback<CDAArray>> C all(C callback) {
    return (C) Callbacks.subscribeAsync(baseQuery().all(), callback, client);
  }

  private ObserveQuery<T> baseQuery() {
    return client.observe(type).where(params);
  }
}
