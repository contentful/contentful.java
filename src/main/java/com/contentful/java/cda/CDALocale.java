package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

public final class CDALocale {
  String code;

  String name;

  @SerializedName("default")
  boolean defaultLocale;

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public boolean isDefaultLocale() {
    return defaultLocale;
  }
}
