package com.contentful.java.cda;

/** Represents results for synchronization via the Sync API. */
public final class SynchronizedSpace extends ArrayResource {
  String nextPageUrl;

  String nextSyncUrl;

  /** Next Sync URL */
  public String nextSyncUrl() {
    return nextSyncUrl;
  }

  String nextPageUrl() {
    return nextPageUrl;
  }
}
