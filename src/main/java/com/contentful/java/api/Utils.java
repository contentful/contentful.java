package com.contentful.java.api;

import com.contentful.java.model.CDAArray;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * SDK utilities
 */
class Utils {
    /**
     * Extracts to CDA endpoint used to to fetch an array by inspecting it's original request URL.
     *
     * @param array {@link CDAArray} instance.
     * @return String representing the last path segment of the original URL.
     */
    static String getNextPageType(CDAArray array) {
        String url = assertArray(array);
        String result = null;

        try {
            URI uri = new URI(url);
            String[] split = uri.getPath().split("/");
            result = split[split.length - 1];
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Helper method to ensure an array has it's original URL associated with it, otherwise
     * throws an {@link java.lang.IllegalArgumentException}.
     *
     * @param array {@link CDAArray} instance.
     * @return String representing the original URL.
     */
    private static String assertArray(CDAArray array) {
        String url = array.getOriginalUrl();

        if (url == null) {
            throw new IllegalArgumentException("Invalid array instance! (empty or unsuccessful response)");
        }

        return url;
    }

    /**
     * Prepares a query map to be used for fetching the next page of an array.
     *
     * @param array {@link CDAArray} instance returned by a successful request.
     * @return Map containing original query string parameters and updated pagination parameters (skip/limit).
     */
    static HashMap<String, String> getNextBatchQueryMapForArray(CDAArray array) {
        assertArray(array);

        // extract pagination parameters
        int skip = array.getSkip();
        int limit = array.getLimit();

        // calculate next offset
        int nextOffset = skip + limit;

        return prepareQueryMap(URI.create(array.getOriginalUrl()), nextOffset, limit);
    }

    /**
     * Helper method for {@link #getNextBatchQueryMapForArray} that actually creates the query map.
     *
     * @param uri        Original request URL.
     * @param nextOffset {@code skip} value to be used.
     * @param limit      {@code limit} value to be used.
     * @return Map representing the query.
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

