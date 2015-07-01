package com.contentful.java.cda;

public final class SynchronizedSpace extends ArrayResource {
  String nextPageUrl;

  String nextSyncUrl;

  public String nextSyncUrl() {
    return nextSyncUrl;
  }

  String nextPageUrl() {
    return nextPageUrl;
  }
}
