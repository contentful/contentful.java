package com.contentful.java.cda;

import java.util.List;

public final class CDAContentType extends CDAResource {
  List<CDAField> fields;

  String name;

  String displayField;

  String description;

  public List<CDAField> fields() {
    return fields;
  }

  public String name() {
    return name;
  }

  public String displayField() {
    return displayField;
  }

  public String description() {
    return description;
  }
}
