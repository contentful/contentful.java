package com.contentful.java.cda;

import java.util.Set;

/** Represents results for synchronization via the Sync API. */
public class SynchronizedSpace extends ArrayResource {
  String nextPageUrl;

  String nextSyncUrl;

  Set<String> deletedAssets;

  Set<String> deletedEntries;

  /** Next Sync URL */
  public String nextSyncUrl() {
    return nextSyncUrl;
  }

  /** Url of next page, containing more data. */
  String nextPageUrl() {
    return nextPageUrl;
  }

  /** Deleted asset ids. */
  public Set<String> deletedAssets() {
    return deletedAssets;
  }

  /** Deleted entry ids. */
  public Set<String> deletedEntries() {
    return deletedEntries;
  }
}
