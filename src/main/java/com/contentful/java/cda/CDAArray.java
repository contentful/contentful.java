package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public final class CDAArray extends CDAResource {
  int total;

  int skip;

  int limit;

  List<CDAResource> items;

  Map<String, CDAAsset> assets;

  Map<String, CDAEntry> entries;

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

  public List<CDAResource> items() {
    return items;
  }

  public Map<String, CDAAsset> assets() {
    return assets;
  }

  public Map<String, CDAEntry> entries() {
    return entries;
  }

  static class Includes {
    @SerializedName("Asset") List<CDAAsset> assets;

    @SerializedName("Entry") List<CDAEntry> entries;
  }
}
