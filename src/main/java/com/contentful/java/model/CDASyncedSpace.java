package com.contentful.java.model;

import java.net.URI;
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

    /**
     * Gets the next sync URL.
     *
     * @return String representing the next sync URL for this Space.
     */
    public String getNextSyncUrl() {
        return nextSyncUrl;
    }

    /**
     * Gets the sync token from this Space's {@code nextSyncUrl} value.
     *
     * @return String representing the next token to be used for the next sync request.
     */
    public String getSyncToken() {
        if (nextSyncUrl == null) {
            return null;
        }

        URI uri = URI.create(nextSyncUrl);

        String query = uri.getQuery();

        if (query == null) {
            return null;
        }

        String[] split = query.split("=");

        if (split.length < 2) {
            return null;
        }

        return split[1];
    }
}
