package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

/** Represents a single locale. */
public final class CDALocale {
  String code;

  String name;

  @SerializedName("default")
  boolean defaultLocale;

  /** Code */
  public String code() {
    return code;
  }

  /** Name */
  public String name() {
    return name;
  }

  /** Default */
  public boolean isDefaultLocale() {
    return defaultLocale;
  }
}
