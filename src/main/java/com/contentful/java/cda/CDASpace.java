package com.contentful.java.cda;

import java.util.List;

/**
 * Represents a single space.
 */
public class CDASpace extends CDAResource {
  String name;

  List<CDALocale> locales;

  CDALocale defaultLocale;

  /**
   * @return name of this space.
   */
  public String name() {
    return name;
  }

  /**
   * @return a list of locales of this space.
   */
  public List<CDALocale> locales() {
    return locales;
  }

  /**
   * @return default locale.
   */
  public CDALocale defaultLocale() {
    return defaultLocale;
  }

  /**
   * Create a String from this object.
   * @return a String containing the id and name of this space
   */
  @Override public String toString() {
    return "CDASpace{" +
        "id='" + id() + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
