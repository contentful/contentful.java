package com.contentful.java.cda;

import java.util.Map;

import okhttp3.HttpUrl;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.CDAType.LOCALE;
import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.Constants.PATH_ASSETS;
import static com.contentful.java.cda.Constants.PATH_CONTENT_TYPES;
import static com.contentful.java.cda.Constants.PATH_ENTRIES;
import static com.contentful.java.cda.Constants.PATH_LOCALES;

final class Util {
  private Util() {
    throw new AssertionError();
  }

  static <T> T checkNotNull(T reference, String format, Object... args) {
    if (reference == null) {
      throw new NullPointerException(String.format(format, args));
    }
    return reference;
  }

  static CharSequence checkNotEmpty(CharSequence string, String format, Object... args) {
    if (string == null) {
      throw new IllegalArgumentException(String.format(format, args));
    }
    if (string.length() == 0) {
      throw new IllegalArgumentException(String.format(format, args));
    }
    return string;
  }

  static String resourcePath(Class<? extends CDAResource> clazz) {
    if (CDAAsset.class.equals(clazz)) {
      return PATH_ASSETS;
    } else if (CDAContentType.class.equals(clazz)) {
      return PATH_CONTENT_TYPES;
    } else if (CDAEntry.class.equals(clazz)) {
      return PATH_ENTRIES;
    } else if (CDALocale.class.equals(clazz)) {
      return PATH_LOCALES;
    }
    throw new IllegalArgumentException("Invalid type specified: " + clazz.getName());
  }

  static Class<? extends CDAResource> classForType(CDAType type) {
    if (ASSET.equals(type)) {
      return CDAAsset.class;
    } else if (CONTENTTYPE.equals(type)) {
      return CDAContentType.class;
    } else if (ENTRY.equals(type)) {
      return CDAEntry.class;
    } else if (SPACE.equals(type)) {
      return CDASpace.class;
    } else if (LOCALE.equals(type)) {
      return CDALocale.class;
    } else if (DELETEDASSET.equals(type) || DELETEDENTRY.equals(type)) {
      return DeletedResource.class;
    }
    throw new IllegalArgumentException("Invalid type provided: " + type);
  }

  static CDAType typeForClass(Class<? extends CDAResource> clazz) {
    if (CDAAsset.class.equals(clazz)) {
      return ASSET;
    } else if (CDAContentType.class.equals(clazz)) {
      return CONTENTTYPE;
    } else if (CDAEntry.class.equals(clazz)) {
      return ENTRY;
    } else if (CDASpace.class.equals(clazz)) {
      return SPACE;
    } else if (CDALocale.class.equals(clazz)) {
      return LOCALE;
    }
    throw new IllegalArgumentException("Invalid class provided: " + clazz.getName());
  }

  @SuppressWarnings("unchecked")
  static <T> T extractNested(Map<?, ?> source, Object... keys) {
    Map<?, ?> curr = source;
    for (int i = 0; i < keys.length; i++) {
      if (i == keys.length - 1) {
        return (T) curr.get(keys[i]);
      }
      curr = (Map<?, ?>) curr.get(keys[i]);
      if (curr == null) {
        break;
      }
    }
    return null;
  }

  static String queryParam(String url, String name) {
    HttpUrl httpUrl = HttpUrl.parse(url);
    if (httpUrl == null) {
      return null;
    }
    return httpUrl.queryParameter(name);
  }
}
