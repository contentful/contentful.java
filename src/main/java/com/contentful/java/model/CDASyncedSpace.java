package com.contentful.java.model;

import java.util.ArrayList;

/**
 * A class to represent the result of a Space synchronization.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDASyncedSpace extends ArrayResource {
    private ArrayList<CDAResource> items;

    private String nextSyncUrl;

    public ArrayList<CDAResource> getItems() {
        return items;
    }

    public String getNextSyncUrl() {
        return nextSyncUrl;
    }
}
