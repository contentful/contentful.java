package com.contentful.java.cda;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.contentful.java.cda.CDAType.ARRAY;
import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.CONTENTTYPE;
import static com.contentful.java.cda.CDAType.ENTRY;
import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.Constants.PATH_ASSETS;
import static com.contentful.java.cda.Constants.PATH_CONTENT_TYPES;
import static com.contentful.java.cda.Constants.PATH_ENTRIES;

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

  static void checkState(boolean condition, String format, Object... args) {
    if (!condition) {
      throw new IllegalStateException(String.format(format, args));
    }
  }

  static void checkArgument(boolean condition, String format, Object... args) {
    if (!condition) throw new IllegalArgumentException(String.format(format, args));
  }

  static String resourcePath(Class<? extends CDAResource> clazz) {
    if (CDAAsset.class.equals(clazz)) {
      return PATH_ASSETS;
    } else if (CDAContentType.class.equals(clazz)) {
      return PATH_CONTENT_TYPES;
    } else if (CDAEntry.class.equals(clazz)) {
      return PATH_ENTRIES;
    }
    throw new IllegalArgumentException("No path available for class: " + clazz.getName());
  }

  static Class<? extends CDAResource> classForType(CDAType type) {
    if (ARRAY.equals(type)) {
      return CDAArray.class;
    } else if (ASSET.equals(type)) {
      return CDAAsset.class;
    } else if (CONTENTTYPE.equals(type)) {
      return CDAContentType.class;
    } else if (ENTRY.equals(type)) {
      return CDAEntry.class;
    } else if (SPACE.equals(type)) {
      return CDASpace.class;
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
    }
    throw new IllegalArgumentException("Invalid class provided: " + clazz.getName());
  }

  @SuppressWarnings("unchecked")
  static <T> T extractNested(Map source, String... keys) {
    Map curr = source;
    for (int i = 0; i < keys.length; i++) {
      if (i == keys.length - 1) {
        return (T) curr.get(keys[i]);
      }
      curr = (Map) curr.get(keys[i]);
      if (curr == null) {
        break;
      }
    }
    return null;
  }

  static String getProperty(String name) {
    Properties properties = new Properties();
    try {
      properties.load(Util.class.getClassLoader().getResourceAsStream(Constants.PROPERTIES));
      String value = properties.getProperty(name);
      checkNotNull(properties, "Properties file contains no version number.");
      return value;
    } catch (IOException e) {
      throw new RuntimeException("Unable to read from properties file.", e);
    }
  }
}
