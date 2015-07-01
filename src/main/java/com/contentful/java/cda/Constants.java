package com.contentful.java.cda;

import java.util.Locale;

public class Constants {
  private Constants() {
    throw new AssertionError();
  }

  static final Locale LOCALE = Locale.US;

  static final String CHARSET = "UTF-8";

  static final String SCHEME = "https";

  static final String ENDPOINT_PREVIEW = SCHEME + "://preview.contentful.com";

  static final String ENDPOINT_PROD = SCHEME + "://cdn.contentful.com";

  static final String PATH_ASSETS = "assets";

  static final String PATH_CONTENT_TYPES = "content_types";

  static final String PATH_ENTRIES = "entries";

  static final String PROPERTIES = "contentful_cda.properties";
}
