package com.contentful.java;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import retrofit.client.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomxor on 25/07/14.
 */
public class Utils {
    /**
     * Populates a {@link java.util.Map} object with items to fetch the next
     * batch of items from a previous {@link CDAListResult} item.
     *
     * @param listResult {@link CDAListResult} instance which was successfully executed,
     *                   meaning {@link CDACallback#onSuccess} was
     *                   called.
     * @return {@link java.util.Map} instance containing original query string parameters
     * and updated pagination parameters (skip/limit).
     */
    public static HashMap<String, String> getNextBatchQueryMapForList(CDAListResult listResult) {
        Response response = listResult.getResponse();

        // ensure this instance has a reference to a valid response object
        if (response == null) {
            throw new IllegalArgumentException("Invalid CDAListResult instance! (empty or unsuccessful response)");
        }

        // extract pagination parameters
        int skip = listResult.getSkip();
        int limit = listResult.getLimit();
        int total = listResult.getTotal();

        // calculate next offset
        int nextOffset = skip + limit;

        // ensure next batch is available
        if (nextOffset >= total) {
            return null;
        }

        return prepareQueryMap(URI.create(listResult.getResponse().getUrl()), nextOffset, limit);
    }

    /**
     * TBD
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

    /**
     * TBD
     */
    public static Map<String, ?> createFieldsMap(JsonDeserializationContext jsonDeserializationContext,
                                                 JsonObject jsonObject) {

        JsonObject fields = jsonObject.getAsJsonObject("fields");
        return jsonDeserializationContext.deserialize(fields, Map.class); // todo use TypeToken ?
    }

    public static boolean isEntry(CDABaseItem item) {
        return item instanceof CDAEntry;
    }

    public static boolean isAsset(CDABaseItem item) {
        return item instanceof CDAAsset;
    }
}

