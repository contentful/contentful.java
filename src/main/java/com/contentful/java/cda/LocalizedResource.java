package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

abstract class LocalizedResource extends CDAResource {
  String locale;

  Map<String, ? super Object> activeFields;

  Map<String, Map<String, ? super Object>> localized;

  @SerializedName("fields")
  Map<String, ? super Object> rawFields;

  /**
   * Extracts a field from the fields set of the active locale, result type is inferred.
   * @param key field key.
   * @param <T> type.
   * @return field value, null if it doesn't exist.
   */
  @SuppressWarnings("unchecked")
  public <T> T getField(String key) {
    return (T) activeFields.get(key);
  }

  /** Returns the active locale code for this resource. */
  public String locale() {
    return locale;
  }

  /** Switches the locale to the one matching the given locale code. */
  public void setLocale(String locale) {
    this.locale = locale;
    this.activeFields = localized.get(locale);
  }
}
