package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Collection of CDA resources. */
public class CDAArray extends ArrayResource {
  int total;

  int skip;

  int limit;

  Includes includes;

  /** Total number of resources (linked excluded). */
  public int total() {
    return total;
  }

  /** Skip attribute. */
  public int skip() {
    return skip;
  }

  /** Limit attribute. */
  public int limit() {
    return limit;
  }

  static class Includes {
    @SerializedName("Asset") List<CDAAsset> assets;

    @SerializedName("Entry") List<CDAEntry> entries;
  }
}
