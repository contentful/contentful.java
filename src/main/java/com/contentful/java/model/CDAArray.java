package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a type of result which may contain multiple resources.
 */
public class CDAArray extends CDAResource {
    // Limit parameter.
    int limit;

    // Skip parameter.
    int skip;

    // Total number of items.
    int total;

    // List of items.
    ArrayList<CDAResource> items;

    // Included resources.
    Includes includes;

    /**
     * Gets the list of resource items associated with this instance.
     * Note items may contain mixed types (i.e. Assets, Entries, ...).
     *
     * @return List of {@link CDAResource} instances.
     */
    public ArrayList<CDAResource> getItems() {
        return items;
    }

    /**
     * Gets the limit parameter for this array result.
     *
     * @return Integer representing the limit parameter.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the skip parameter for this array result.
     *
     * @return Integer representing the skip parameter.
     */
    public int getSkip() {
        return skip;
    }

    /**
     * Gets the total parameter for this array result.
     *
     * @return Integer representing the amount of items returned by the server.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the resources included with this array result.
     *
     * @return {@link Includes} instance representing the included items.
     */
    public Includes getIncludes() {
        return includes;
    }

    // Included resources class
    public static class Includes {
        @SerializedName("Asset")
        public List<CDAAsset> assets;

        @SerializedName("Entry")
        public List<CDAEntry> entries;

        /**
         * Gets a list of included Assets.
         *
         * @return List representing the Assets included with this array result.
         */
        public List<CDAAsset> getAssets() {
            return assets;
        }

        /**
         * Gets a list of included Entries.
         *
         * @return List representing the Entries included with this array result.
         */
        public List<CDAEntry> getEntries() {
            return entries;
        }
    }
}
