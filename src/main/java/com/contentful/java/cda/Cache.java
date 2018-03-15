package com.contentful.java.cda;

import java.util.List;
import java.util.Map;

class Cache {
  private List<CDALocale> locales;

  private CDALocale defaultLocale;

  private Map<String, CDAContentType> types;

  private final Object localesLock = new Object();

  private final Object typesLock = new Object();

  List<CDALocale> locales() {
    return locales;
  }

  CDALocale defaultLocale() {
    return defaultLocale;
  }

  void setLocales(List<CDALocale> locales) {
    synchronized (localesLock) {
      this.locales = locales;

      updateDefaultLocale();
    }
  }

  void updateDefaultLocale() {
    if (this.locales != null) {
      for (final CDALocale locale : this.locales) {
        if (locale.isDefaultLocale()) {
          this.defaultLocale = locale;
        }
      }
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
    synchronized (localesLock) {
      locales = null;
      defaultLocale = null;
    }

    synchronized (typesLock) {
      types = null;
    }
  }
}
