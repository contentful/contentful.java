package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Collection of CDA resources.
 */
public class CDAArray extends ArrayResource {
  private static final long serialVersionUID = 6596224363025698245L;
  int total;

  int skip;

  int limit;

  Includes includes;

  private List<CDAError> errors;

  /**
   * @return total number of resources (linked excluded).
   */
  public int total() {
    return total;
  }

  /**
   * @return number of resources to be skipped.
   */
  public int skip() {
    return skip;
  }

  /**
   * @return limit attribute. How many max resources got requested?
   */
  public int limit() {
    return limit;
  }

  /**
   * @return a list of errors if any present
   */
  public List<CDAError> getErrors() {
    return errors;
  }

  public void setErrors(List<CDAError> errors) {
    this.errors = errors;
  }

  static class Includes {
    @SerializedName("Asset") List<CDAAsset> assets;

    @SerializedName("Entry") List<CDAEntry> entries;
  }
}
