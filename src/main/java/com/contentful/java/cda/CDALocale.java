package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a single locale.
 */
@JsonTypeName("Locale")
public class CDALocale extends CDAResource {
  private static final long serialVersionUID = -5710267672379169621L;
  @JsonProperty String code;

  @JsonProperty String name;

  @SerializedName("fallbackCode")
  @JsonProperty("fallbackCode")
  String fallbackLocaleCode;

  @SerializedName("default")
  @JsonProperty("default")
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
