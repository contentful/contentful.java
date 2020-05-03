package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Set;

/**
 * Represents results for synchronization via the Sync API.
 */
@JsonTypeName("Array")
public class SynchronizedSpace extends ArrayResource {
  private static final long serialVersionUID = 8618757744312417604L;
  @JsonProperty(defaultValue = "")
  String nextPageUrl;
  @JsonProperty(defaultValue = "")
  String nextSyncUrl;
  @JsonIgnore
  Set<String> deletedAssets;
  @JsonIgnore
  Set<String> deletedEntries;

  /**
   * @return url to the next sync.
   */
  public String nextSyncUrl() {
    return nextSyncUrl;
  }

  /**
   * @return url of next page, containing more data.
   */
  String nextPageUrl() {
    return nextPageUrl;
  }

  /**
   * @return deleted asset ids.
   */
  public Set<String> deletedAssets() {
    return deletedAssets;
  }

  /**
   * @return deleted entry ids.
   */
  public Set<String> deletedEntries() {
    return deletedEntries;
  }
}
