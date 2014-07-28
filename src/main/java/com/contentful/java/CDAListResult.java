package com.contentful.java;

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

    List<? extends CDABaseItem> items;
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

    public List<? extends CDABaseItem> getItems() {
        return items;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
