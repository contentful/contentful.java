package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a single locale.
 */
public class CDALocale extends CDAResource {
  private static final long serialVersionUID = -5710267672379169621L;
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

  /**
   * @return a human readable string, representing the object.
   */
  @Override public String toString() {
    return "CDALocale { " + super.toString() + " "
        + "code = " + code() + ", "
        + "defaultLocale = " + isDefaultLocale() + ", "
        + "fallbackLocaleCode = " + fallbackLocaleCode() + ", "
        + "name = " + name() + " "
        + "}";
  }
}
