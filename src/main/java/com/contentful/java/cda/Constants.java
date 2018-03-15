package com.contentful.java.cda;

import java.util.Locale;

/**
 * This class holds specific constants, used throughout the sdk, not accessible by mere mortals.
 */
class Constants {
  static final Locale LOCALE = Locale.US;

  static final String SCHEME = "https";

  static final String ENDPOINT_PREVIEW = SCHEME + "://preview.contentful.com/";

  static final String ENDPOINT_PROD = SCHEME + "://cdn.contentful.com/";

  static final String PATH_ASSETS = "assets";

  static final String PATH_CONTENT_TYPES = "content_types";

  static final String PATH_ENTRIES = "entries";

  static final String PATH_LOCALES = "locales";

  static final String DEFAULT_ENVIRONMENT = "master";
}
