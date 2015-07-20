package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

import static com.contentful.java.cda.Constants.LOCALE;

public abstract class CDAResource {
  @SerializedName("sys")
  Map<String, Object> attrs;

  public String id() {
    return getAttribute("id");
  }

  public CDAType type() {
    String type = getAttribute("type");
    return CDAType.valueOf(type.toUpperCase(LOCALE));
  }

  public Map<String, Object> attrs() {
    return attrs;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) attrs.get(key);
  }
}
