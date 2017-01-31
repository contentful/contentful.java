package com.contentful.java.cda;

import java.util.Map;

/** Represents a single asset. */
public class CDAAsset extends LocalizedResource {
  /**
   * @return title of this asset.
   */
  public String title() {
    return getField("title");
  }

  /**
   * @return url to the file of this asset.
   */
  public String url() {
    return fileField("url");
  }

  /**
   * @return mime-type of this asset.
   */
  public String mimeType() {
    return fileField("contentType");
  }

  /**
   * Helper method to extract a field from the {@code file} map.
   * @param key the key who's value to be returned.
   * @param <T> the type of this field.
   * @return field of this file.
   */
  @SuppressWarnings("unchecked")
  public <T> T fileField(String key) {
    T result = null;
    Map<String, Object> file = getField("file");
    if (file != null) {
      result = (T) file.get(key);
    }
    return result;
  }

  /**
   * Return a string, showing the id and title.
   * @return a human readable string
   */
  @Override public String toString() {
    return "CDAAsset{" +
        "id='" + id() + '\'' +
        ", title='" + title() + '\'' +
        '}';
  }
}
