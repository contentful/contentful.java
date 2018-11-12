package com.contentful.java.cda;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** Represents a single content type field. */
public class CDAField implements Serializable {
  private static final long serialVersionUID = -2852530837647669035L;
  String name;

  String id;

  String type;

  String linkType;

  boolean disabled;

  boolean required;

  boolean localized;

  Map<String, Object> items;

  List<Map<String, Object>> validations;

  /** @return name of this content type. */
  public String name() {
    return name;
  }

  /** @return the id of this object. */
  public String id() {
    return id;
  }

  /** @return the type. **/
  public String type() {
    return type;
  }

  /** @return the link type. **/
  public String linkType() {
    return linkType;
  }

  /** @return true if this object is disabled. */
  public boolean isDisabled() {
    return disabled;
  }

  /** @return true if this object is required. */
  public boolean isRequired() {
    return required;
  }

  /** @return true if this object is localized. */
  public boolean isLocalized() {
    return localized;
  }

  /** @return a map of items, this field contains. */
  public Map<String, Object> items() {
    return items;
  }

  /** @return a map of validations, defined on this object. */
  public List<Map<String, Object>> validations() {
    return validations;
  }
}
