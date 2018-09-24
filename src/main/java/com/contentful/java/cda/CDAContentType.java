package com.contentful.java.cda;

import java.util.List;

/**
 * Represents a single content type.
 */
public class CDAContentType extends CDAResource {
  private static final long serialVersionUID = 7901798878659781364L;
  List<CDAField> fields;

  String name;

  String displayField;

  String description;

  /**
   * @return list of fields.
   */
  public List<CDAField> fields() {
    return fields;
  }

  /**
   * @return name of this content type.
   */
  public String name() {
    return name;
  }

  /**
   * @return field to be used for displaying.
   */
  public String displayField() {
    return displayField;
  }

  /**
   * @return description of this content type.
   */
  public String description() {
    return description;
  }

  /**
   * Convert this object into a human readable string.
   *
   * @return a string, containing id, name and description of this type.
   */
  @Override public String toString() {
    return "CDAContentType{"
        + "id='" + id() + '\''
        + ", name='" + name + '\''
        + ", description='" + description + '\''
        + '}';
  }
}
