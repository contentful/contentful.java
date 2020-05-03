package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Collection of CDA resources.
 */
@JsonTypeName("Array")
public class CDAArray extends ArrayResource {
  private static final long serialVersionUID = 6596224363025698245L;
  @JsonProperty
  int total;

  @JsonProperty
  int skip;

  @JsonProperty
  int limit;

  @JsonProperty
  Includes includes;

  @JsonProperty
  private List<CDAError> errors;

  CDAMetadata metadata;

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
    @SerializedName("Asset") @JsonProperty("Asset") List<CDAAsset> assets;

    @SerializedName("Entry")  @JsonProperty("Entry")  List<CDAEntry> entries;
  }
}
