package com.contentful.java.lib;

import com.contentful.java.model.CDAArray;

import java.net.URI;
import java.util.HashMap;

/**
 * SDK utilities
 */
public class Utils {
    // todo tmp

    /**
     * Populates a {@link java.util.Map} object with items to fetch the next
     * batch of items from a previous {@link com.contentful.java.model.CDAArray} item.
     *
     * @param listResult {@link com.contentful.java.model.CDAArray} instance which was successfully executed,
     *                   meaning {@link com.contentful.java.api.CDACallback#onSuccess} was
     *                   called.
     * @return {@link java.util.Map} instance containing original query string parameters
     * and updated pagination parameters (skip/limit).
     */
    public static HashMap<String, String> getNextBatchQueryMapForList(CDAArray listResult) {
        // todo refactor
/*
        Response response = listResult.getResponse();

        // ensure this instance has a reference to a valid response object
        if (response == null) {
            throw new IllegalArgumentException("Invalid CDAListResult instance! (empty or unsuccessful response)");
        }

        // extract pagination parameters
        int skip = listResult.skip;
        int limit = listResult.limit;
        int total = listResult.total;

        // calculate next offset
        int nextOffset = skip + limit;

        // ensure next batch is available
        if (nextOffset >= total) {
            return null;
        }

        return prepareQueryMap(URI.create(response.getUrl()), nextOffset, limit);
*/
        return null;
    }

    // todo tmp

    /**
     * TBD (paging)
     */
    private static HashMap<String, String> prepareQueryMap(URI uri, int nextOffset, int limit) {
        // prepare the new map
        HashMap<String, String> queryMap = new HashMap<String, String>();
        String query = uri.getQuery();

        if (query != null) {
            // iterate through all the query string parameters from the original request and add them
            // to the new map, while stripping any pagination related arguments from it as those will
            // be specified next.
            String[] params = query.split("&");

            for (String p : params) {
                String[] kv = p.split("=");

                if ("skip".equalsIgnoreCase(kv[0]) ||
                        "limit".equalsIgnoreCase(kv[0])) {

                    continue;
                }

                queryMap.put(kv[0], kv[1]);
            }
        }

        // set new pagination parameters
        queryMap.put("skip", Integer.toString(nextOffset));
        queryMap.put("limit", Integer.toString(limit));

        return queryMap;
    }

}

