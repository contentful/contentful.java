package com.contentful.java.cda;

import okhttp3.HttpUrl;

import java.util.Map;

import static com.contentful.java.cda.CDAType.LOCALE;
import static com.contentful.java.cda.CDAType.TAG;
import static com.contentful.java.cda.CDAType.DELETEDASSET;
import static com.contentful.java.cda.CDAType.DELETEDENTRY;
import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;

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

  static String resourcePath(Class<?> clazz) {
    if (CDAAsset.class.equals(clazz)) {
      return Constants.PATH_ASSETS;
    } else if (CDAContentType.class.equals(clazz)) {
      return Constants.PATH_CONTENT_TYPES;
    } else if (CDAEntry.class.equals(clazz)) {
      return Constants.PATH_ENTRIES;
    } else if (CDALocale.class.equals(clazz)) {
      return Constants.PATH_LOCALES;
    } else if (CDATag.class.equals(clazz)) {
      return Constants.PATH_TAGS;
    }
    throw new IllegalArgumentException("Invalid type specified: " + clazz.getName());
  }

  static Class<? extends CDAResource> classForType(CDAType type) {
    if (CDAType.ASSET.equals(type)) {
      return CDAAsset.class;
    } else if (CDAType.CONTENTTYPE.equals(type)) {
      return CDAContentType.class;
    } else if (CDAType.ENTRY.equals(type)) {
      return CDAEntry.class;
    } else if (CDAType.SPACE.equals(type)) {
      return CDASpace.class;
    } else if (LOCALE.equals(type)) {
      return CDALocale.class;
    } else if (DELETEDASSET.equals(type) || DELETEDENTRY.equals(type)) {
      return DeletedResource.class;
    } else if (TAG.equals(type)) {
      return CDATag.class;
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
    } else if (CDATag.class.equals(clazz)) {
      return TAG;
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
