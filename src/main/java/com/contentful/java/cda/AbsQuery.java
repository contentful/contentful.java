package com.contentful.java.cda;

import java.util.HashMap;
import java.util.Map;

import static com.contentful.java.cda.Util.resourcePath;

abstract class AbsQuery<T extends CDAResource, E extends AbsQuery<T, E>> {
  final Class<T> type;

  final CDAClient client;

  final Map<String, String> params = new HashMap<String, String>();

  AbsQuery(Class<T> type, CDAClient client) {
    this.type = type;
    this.client = client;
  }

  protected String path() {
    return resourcePath(type);
  }

  @SuppressWarnings("unchecked")
  public E where(String key, String value) {
    params.put(key, value);
    return (E) this;
  }

  @SuppressWarnings("unchecked")
  protected E where(Map<String, String> params) {
    this.params.clear();
    this.params.putAll(params);
    return (E) this;
  }
}
