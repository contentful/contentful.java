package com.contentful.java.cda;

import java.util.List;

public final class CDASpace extends CDAResource {
  String name;

  List<CDALocale> locales;

  CDALocale defaultLocale;

  public String name() {
    return name;
  }

  public List<CDALocale> locales() {
    return locales;
  }

  public CDALocale defaultLocale() {
    return defaultLocale;
  }
}
