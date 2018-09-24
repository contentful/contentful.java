package com.contentful.java.cda;

/**
 * Represents a single space.
 */
public class CDASpace extends CDAResource {
  private static final long serialVersionUID = 8920494351623297673L;
  String name;

  /**
   * @return name of this space.
   */
  public String name() {
    return name;
  }

  /**
   * Create a String from this object.
   *
   * @return a String containing the id and name of this space
   */
  @Override public String toString() {
    return "CDASpace{"
        + "id='" + id() + '\''
        + ", name='" + name + '\''
        + '}';
  }
}
