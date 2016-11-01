package com.contentful.java.cda;

import java.util.Map;

final class Cache {
  private CDASpace space;

  private Map<String, CDAContentType> types;

  private final Object LOCK_SPACE = new Object();

  private final Object LOCK_TYPES = new Object();

  CDASpace space() {
    synchronized (LOCK_SPACE) {
      return space;
    }
  }

  void setSpace(CDASpace space) {
    synchronized (LOCK_SPACE) {
      this.space = space;
    }
  }

  Map<String, CDAContentType> types() {
    synchronized (LOCK_TYPES) {
      return types;
    }
  }

  void setTypes(Map<String, CDAContentType> types) {
    synchronized (LOCK_TYPES) {
      this.types = types;
    }
  }

  void clear() {
    synchronized (LOCK_SPACE) {
      space = null;
    }

    synchronized (LOCK_TYPES) {
      types = null;
    }
  }
}
