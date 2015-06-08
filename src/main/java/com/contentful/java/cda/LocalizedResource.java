package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public abstract class LocalizedResource extends CDAResource {
  String locale;

  Map<String, ? super Object> activeFields;

  Map<String, Map<String, ? super Object>> localized;

  @SerializedName("fields")
  Map<String, ? super Object> rawFields;

  @SuppressWarnings("unchecked")
  public <T> T getField(String key) {
    return (T) activeFields.get(key);
  }

  public String locale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
    this.activeFields = localized.get(locale);
  }
}
