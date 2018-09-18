package com.contentful.java.cda;

import java.util.Map;

/**
 * Represents a resource which may contain field values for multiple locales.
 */
public abstract class LocalizedResource extends CDAResource {
  private static final long serialVersionUID = 5713028146014748949L;

  public class Localizer {
    private final String locale;

    Localizer(String locale) {
      this.locale = locale;
    }

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

    <T> T getFieldForFallbackLocale(Map<String, T> value, String locale) {
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
  }

  String defaultLocale;

  Map<String, String> fallbackLocaleMap;

  Map<String, Object> fields;

  Map<String, Object> rawFields;

  /**
   * Creates an object to be used for returning different field in one locale.
   *
   * @param locale pointing to a locale in the environment.
   * @return localizer to localize the fields.
   */
  public Localizer localize(String locale) {
    return new Localizer(locale);
  }

  /**
   * Get a field using the environments default locale.
   *
   * @param key field key.
   * @param <T> type.
   * @return field value, null if it doesn't exist.
   * @see #localize(String)
   */
  public <T> T getField(String key) {
    return localize(defaultLocale).getField(key);
  }

  /**
   * Extracts a field from the fields set of the active locale, result type is inferred.
   *
   * @param key field key.
   * @param <T> type.
   * @return field value, null if it doesn't exist.
   */
  public <T> T getField(String locale, String key) {
    return localize(locale).getField(key);
  }

  /**
   * Internal method for updating contents of a field.
   * <p>
   * This method is used by the SDK to generate objects based on raw fields.
   *
   * <b>Do not use this field to update data on Contentful. Take a look at the CMA-SDK for that.</b>
   *
   * @param locale locale to be updated.
   * @param key    the key of the field to be updated.
   * @param value  the value of the field to be used.
   */
  @SuppressWarnings("unchecked")
  public void setField(String locale, String key, Object value) {
    ((Map<String, Object>) fields.get(key)).put(locale, value);
  }

  /**
   * @return raw unprocessed fields.
   */
  public Map<String, Object> rawFields() {
    return rawFields;
  }
}
