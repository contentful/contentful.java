package com.contentful.java.cda;

import java.util.HashMap;
import java.util.Map;

import static com.contentful.java.cda.Util.resourcePath;

public abstract class AbsQuery<T extends CDAResource, E extends AbsQuery<T, E>> {
  final Class<T> type;

  final CDAClient client;

  Map<String, String> params;

  AbsQuery(Class<T> type, CDAClient client) {
    this.type = type;
    this.client = client;
  }

  protected String path() {
    return resourcePath(type);
  }

  @SuppressWarnings("unchecked")
  public E where(String key, String value) {
    if (params == null) {
      params = new HashMap<String, String>();
    }
    params.put(key, value);
    return (E) this;
  }

  @SuppressWarnings("unchecked")
  protected E where(Map<String, String> params) {
    this.params = params;
    return (E) this;
  }
}
