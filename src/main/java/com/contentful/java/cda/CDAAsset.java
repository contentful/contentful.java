package com.contentful.java.cda;

import java.util.Map;

/** Represents a single asset. */
public final class CDAAsset extends LocalizedResource {
  /** Title */
  public String title() {
    return getField("title");
  }

  /** Url */
  public String url() {
    return fileField("url");
  }

  /** Mime-type */
  public String mimeType() {
    return fileField("contentType");
  }

  /** Helper method to extract a field from the {@code file} map. */
  @SuppressWarnings("unchecked")
  public <T> T fileField(String key) {
    T result = null;
    Map<?, ?> file = getField("file");
    if (file != null) {
      result = (T) file.get(key);
    }
    return result;
  }

  @Override public String toString() {
    return "CDAAsset{" +
        "id='" + id() + '\'' +
        ", title='" + title() + '\'' +
        '}';
  }
}
