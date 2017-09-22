package com.contentful.java.cda;

import java.util.Map;

final class Cache {
  private CDASpace space;

  private Map<String, CDAContentType> types;

  private final Object spaceLock = new Object();

  private final Object typesLock = new Object();

  CDASpace space() {
    synchronized (spaceLock) {
      return space;
    }
  }

  void setSpace(CDASpace space) {
    synchronized (spaceLock) {
      this.space = space;
    }
  }

  Map<String, CDAContentType> types() {
    synchronized (typesLock) {
      return types;
    }
  }

  void setTypes(Map<String, CDAContentType> types) {
    synchronized (typesLock) {
      this.types = types;
    }
  }

  void clear() {
    synchronized (spaceLock) {
      space = null;
    }

    synchronized (typesLock) {
      types = null;
    }
  }
}
