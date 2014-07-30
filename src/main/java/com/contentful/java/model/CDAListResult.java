package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;
import retrofit.client.Response;

import java.util.List;

/**
 * Class representing a collection of items fetched from the CDA.
 */
public class CDAListResult {
    public Sys sys;
    public int total;
    public int skip;
    public int limit;

    public List<CDABaseItem> items;
    private Response response;

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
