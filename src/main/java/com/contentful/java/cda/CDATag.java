package com.contentful.java.cda;


/**
 * Represents a single locale.
 */
public class CDATag extends CDAResource {
  private static final long serialVersionUID = -5710267672379169622L;

  String name;

  /**
   * @return human readable name of this tag.
   */
  public String name() {
    return name;
  }

  /**
   * @return a human readable string, representing the object.
   */
  @Override public String toString() {
    return "CDATag { " + super.toString() + " "
        + "}";
  }
}
