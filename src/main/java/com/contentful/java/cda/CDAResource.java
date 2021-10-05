package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

import static com.contentful.java.cda.Constants.LOCALE;

/**
 * This class represents a basic resource, serving as a base class
 * for CDAContentType, CDASpace and, indirectly, for CDAEntry.
 */
public abstract class CDAResource implements Serializable {
  private static final long serialVersionUID = -160701290783423915L;
  @SerializedName("sys")
  Map<String, Object> attrs;

  /**
   * @return a string representing this object's id.
   */
  public String id() {
    return getAttribute("id");
  }

  /**
   * @return the type of this resource.
   */
  public CDAType type() {
    String type = getAttribute("type");
    return CDAType.valueOf(type.toUpperCase(LOCALE));
  }

  /**
   * @return all of the attributes, this object holds.
   */
  public Map<String, Object> attrs() {
    return attrs;
  }

  /**
   * Retrieve a specific attribute of this resource.
   *
   * @param key a string key associated with the value to be retrieved.
   * @param <T> the type of the attribute to be retrieved.
   * @return the actual value of the attribute, or null, if there the key was not found.
   */
  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) attrs.get(key);
  }


  /**
   * @return a human readable string, representing the object.
   */
  @Override public String toString() {
    return "CDAResource { "
        + "attrs = " + attrs() + ", "
        + "id = " + id() + ", "
        + "type = " + type() + " "
        + "}";
  }
}
