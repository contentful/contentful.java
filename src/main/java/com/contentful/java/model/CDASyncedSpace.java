package com.contentful.java.model;

import java.util.List;

/**
 * A class to represent the result of a Space synchronization.
 */
public class CDASyncedSpace extends CDABaseItem {
    public List<CDAAsset> assets;

    public List<CDAEntry> entries;

    public String nextSyncUrl;

    public List<CDABaseItem> items;
}
