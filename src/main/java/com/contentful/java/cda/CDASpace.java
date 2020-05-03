package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents a single space.
 */
@JsonTypeName("Space")
public class CDASpace extends CDAResource {
  private static final long serialVersionUID = 8920494351623297673L;
  @JsonProperty
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
