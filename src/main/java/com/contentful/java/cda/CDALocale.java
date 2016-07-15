package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Represents a single locale. */
public class CDALocale implements Serializable {
  String code;

  String name;

  @SerializedName("fallbackCode")
  String fallbackLocaleCode;

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

  /** Fallback code */
  public String fallbackLocaleCode() {
    return fallbackLocaleCode;
  }

  /** Default */
  public boolean isDefaultLocale() {
    return defaultLocale;
  }
}
