package com.contentful.java.cda;

import java.util.Map;

final class Cache {
  private CDASpace space;

  private Map<String, CDAContentType> types;

  private final Object LOCK_SPACE = new Object();

  private final Object LOCK_TYPES = new Object();

  public CDASpace space() {
    synchronized (LOCK_SPACE) {
      return space;
    }
  }

  public void setSpace(CDASpace space) {
    synchronized (LOCK_SPACE) {
      this.space = space;
    }
  }

  public Map<String, CDAContentType> types() {
    synchronized (LOCK_TYPES) {
      return types;
    }
  }

  public void setTypes(Map<String, CDAContentType> types) {
    synchronized (LOCK_TYPES) {
      this.types = types;
    }
  }
}
