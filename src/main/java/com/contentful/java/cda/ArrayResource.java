package com.contentful.java.cda;

import java.util.List;
import java.util.Map;

public abstract class ArrayResource extends CDAResource {
  List<CDAResource> items;

  Map<String, CDAAsset> assets;

  Map<String, CDAEntry> entries;

  public List<CDAResource> items() {
    return items;
  }

  public Map<String, CDAAsset> assets() {
    return assets;
  }

  public Map<String, CDAEntry> entries() {
    return entries;
  }
}
