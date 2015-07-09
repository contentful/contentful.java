package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

import static com.contentful.java.cda.Constants.LOCALE;

public class CDAResource {
  @SerializedName("sys")
  Map<String, ? super Object> attrs;

  public String id() {
    return getAttribute("id");
  }

  public CDAType type() {
    String type = getAttribute("type");
    return CDAType.valueOf(type.toUpperCase(LOCALE));
  }

  public Map<String, ? super Object> attrs() {
    return attrs;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) attrs.get(key);
  }
}
