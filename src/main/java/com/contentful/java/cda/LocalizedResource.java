package com.contentful.java.cda;

import java.util.Map;

/** Represents a resource which may contain field values for multiple locales. */
public abstract class LocalizedResource extends CDAResource {
  String locale;

  String defaultLocale;

  Map<String, Object> fields;

  Map<String, Object> rawFields;

  /**
   * Extracts a field from the fields set of the active locale, result type is inferred.
   * @param key field key.
   * @param <T> type.
   * @return field value, null if it doesn't exist.
   */
  @SuppressWarnings("unchecked")
  public <T> T getField(String key) {
    Map<?, ?> value = (Map<? ,?>) fields.get(key);
    if (value == null) {
      return null;
    }
    Object localized = value.get(locale);
    if (localized != null) {
      return (T) localized;
    }
    return (T) value.get(defaultLocale);
  }

  /** Raw unprocessed fields. */
  public Map<String, Object> rawFields() {
    return rawFields;
  }

  /** Returns the active locale code for this resource. */
  public String locale() {
    return locale;
  }

  /** Switches the locale to the one matching the given locale code. */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  void setDefaultLocale(String defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  String defaultLocale() {
    return defaultLocale;
  }
}
