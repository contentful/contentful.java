package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class CDAArray extends ArrayResource {
  int total;

  int skip;

  int limit;

  Includes includes;

  public int total() {
    return total;
  }

  public int skip() {
    return skip;
  }

  public int limit() {
    return limit;
  }

  static class Includes {
    @SerializedName("Asset") List<CDAAsset> assets;

    @SerializedName("Entry") List<CDAEntry> entries;
  }
}
