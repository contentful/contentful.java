package com.contentful.java.cda;

import java.util.Map;

/**
 * Represents a resource which may contain field values for multiple locales.
 */
public abstract class LocalizedResource extends CDAResource {
  String locale;

  String defaultLocale;

  Map<String, String> fallbackLocaleMap;

  Map<String, Object> fields;

  Map<String, Object> rawFields;

  /**
   * Extracts a field from the fields set of the active locale, result type is inferred.
   *
   * @param key field key.
   * @param <T> type.
   * @return field value, null if it doesn't exist.
   */
  @SuppressWarnings("unchecked")
  public <T> T getField(String key) {
    final Map<String, T> value = (Map<String, T>) fields.get(key);
    if (value == null) {
      return null;
    }

    return getFieldForFallbackLocale(value, locale);
  }

  private <T> T getFieldForFallbackLocale(Map<String, T> value, String locale) {
    if (locale == null) {
      return null;
    }

    final T localized = value.get(locale);
    if (localized != null) {
      return localized;
    } else {
      return getFieldForFallbackLocale(value, fallbackLocaleMap.get(locale));
    }
  }

  /**
   * @return raw unprocessed fields.
   */
  public Map<String, Object> rawFields() {
    return rawFields;
  }

  /**
   * @return returns the active locale code for this resource.
   */
  public String locale() {
    return locale;
  }

  /**
   * Switches the locale to the one matching the given locale code.
   *
   * @param locale the locale to be set.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  void setFallbackLocaleMap(Map<String, String> fallbackLocaleMap) {
    this.fallbackLocaleMap = fallbackLocaleMap;
  }

  Map<String, String> fallbackLocaleMap() {
    return fallbackLocaleMap;
  }

  void setDefaultLocale(String defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  String defaultLocale() {
    return defaultLocale;
  }
}
