package com.contentful.java.cda;

import java.util.List;

/** Represents a single content type. */
public class CDAContentType extends CDAResource {
  List<CDAField> fields;

  String name;

  String displayField;

  String description;

  /** Fields List. */
  public List<CDAField> fields() {
    return fields;
  }

  /** Name */
  public String name() {
    return name;
  }

  /** Display Field */
  public String displayField() {
    return displayField;
  }

  /** Description */
  public String description() {
    return description;
  }

  @Override public String toString() {
    return "CDAContentType{" +
        "id='" + id() + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
