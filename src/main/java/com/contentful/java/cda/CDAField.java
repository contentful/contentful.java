package com.contentful.java.cda;

import java.util.Map;

public final class CDAField {
  String name;

  String id;

  String type;

  String linkType;

  boolean disabled;

  boolean required;

  boolean localized;

  Map items;

  Map validations;

  public String name() {
    return name;
  }

  public String id() {
    return id;
  }

  public String type() {
    return type;
  }

  public String linkType() {
    return linkType;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isLocalized() {
    return localized;
  }

  public Map items() {
    return items;
  }

  public Map validations() {
    return validations;
  }
}
