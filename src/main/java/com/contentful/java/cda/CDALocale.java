package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents a single locale.
 */
public class CDALocale implements Serializable {
  String code;

  String name;

  @SerializedName("fallbackCode")
  String fallbackLocaleCode;

  @SerializedName("default")
  boolean defaultLocale;

  /**
   * @return code of this locale. ('en-US' or similar).
   */
  public String code() {
    return code;
  }

  /**
   * @return human readable name of this locale.
   */
  public String name() {
    return name;
  }

  /**
   * @return the code of a locale to be used for falling back.
   */
  public String fallbackLocaleCode() {
    return fallbackLocaleCode;
  }

  /**
   * @return true if this is the default locale.
   */
  public boolean isDefaultLocale() {
    return defaultLocale;
  }
}
