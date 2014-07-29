package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;
import retrofit.client.Response;

import java.util.List;

/**
 * Class representing a collection of items fetched from the CDA.
 */
public class CDAListResult {
    Sys sys;
    int total;
    int skip;
    int limit;

    List<CDABaseItem> items;
    Response response;

    public Sys getSys() {
        return sys;
    }

    public int getTotal() {
        return total;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }

    public List<CDABaseItem> getItems() {
        return items;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    // Includes
    public static class Includes {
        @SerializedName("Asset")
        public List<CDAAsset> assets;

        @SerializedName("Entry")
        public List<CDAEntry> entries;
    }

    public Includes includes;
}
