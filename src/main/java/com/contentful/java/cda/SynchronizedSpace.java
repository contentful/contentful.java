package com.contentful.java.cda;

import java.util.Set;

/**
 * Represents results for synchronization via the Sync API.
 */
public class SynchronizedSpace extends ArrayResource {
  private static final long serialVersionUID = 8618757744312417604L;
  String nextPageUrl;

  String nextSyncUrl;

  Set<String> deletedAssets;

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
