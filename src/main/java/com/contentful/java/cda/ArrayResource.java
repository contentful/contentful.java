package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An abstraction of CDAResources combined into one array.
 *
 * @see CDAResource
 */
public abstract class ArrayResource extends CDAResource {
  private static final long serialVersionUID = -2702554830040250962L;
  @JsonProperty(defaultValue = "[]")
  List<CDAResource> items = new ArrayList<>();
  @JsonIgnore
  Map<String, CDAAsset> assets;
  @JsonIgnore
  Map<String, CDAEntry> entries;

  /**
   * @return items in this resource.
   */
  public List<CDAResource> items() {
    return items;
  }

  /**
   * @return assets mapped by asset id (includes linked resources).
   */
  public Map<String, CDAAsset> assets() {
    return assets;
  }

  /**
   * @return entries mapped by entry id (includes linked resources).
   */
  public Map<String, CDAEntry> entries() {
    return entries;
  }
}
