package com.contentful.java.cda;

import java.util.List;

/** Represents a single space. */
public final class CDASpace extends CDAResource {
  String name;

  List<CDALocale> locales;

  CDALocale defaultLocale;

  /** Name */
  public String name() {
    return name;
  }

  /** Locales List */
  public List<CDALocale> locales() {
    return locales;
  }

  /** Default Locale */
  public CDALocale defaultLocale() {
    return defaultLocale;
  }

  @Override public String toString() {
    return "CDASpace{" +
        "id='" + id() + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
