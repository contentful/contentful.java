package com.contentful.java.cda;

import java.util.Map;

public final class CDAAsset extends LocalizedResource {
  public String title() {
    return getField("title");
  }

  public String url() {
    return fileField("url");
  }

  public String mimeType() {
    return fileField("contentType");
  }

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
