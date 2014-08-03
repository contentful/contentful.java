package com.contentful.java.model;

import java.util.ArrayList;

/**
 * A class to represent the result of a Space synchronization.
 */
public class CDASyncedSpace extends ArrayResource {
    ArrayList<CDAResource> items;

    String nextSyncUrl;

    public ArrayList<CDAResource> getItems() {
        return items;
    }

    public String getNextSyncUrl() {
        return nextSyncUrl;
    }
}
