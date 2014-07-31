package com.contentful.java.model;

import java.util.List;

/**
 * A class to represent the result of a Space synchronization.
 */
public class CDASyncedSpace {
    public List<Object> assets; // todo tmp

    public List<Object> entries;

    public String nextSyncUrl;

//    public List<CDABaseItem> items;
}
