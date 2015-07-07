package com.contentful.java.cda;

import java.util.List;
import java.util.Map;

abstract class ArrayResource extends CDAResource {
  List<CDAResource> items;

  Map<String, CDAAsset> assets;

  Map<String, CDAEntry> entries;

  /** Resources list. */
  public List<CDAResource> items() {
    return items;
  }

  /** Assets mapped by asset id. */
  public Map<String, CDAAsset> assets() {
    return assets;
  }

  /** Entries mapped by entry id. */
  public Map<String, CDAEntry> entries() {
    return entries;
  }
}
