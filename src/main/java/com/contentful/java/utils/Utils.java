package com.contentful.java.utils;

import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDABaseItem;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAListResult;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit.client.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * SDK utilities
 */
public class Utils {
    /**
     * Populates a {@link java.util.Map} object with items to fetch the next
     * batch of items from a previous {@link com.contentful.java.model.CDAListResult} item.
     *
     * @param listResult {@link com.contentful.java.model.CDAListResult} instance which was successfully executed,
     *                   meaning {@link com.contentful.java.api.CDACallback#onSuccess} was
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

        return prepareQueryMap(URI.create(response.getUrl()), nextOffset, limit);
    }

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

    /**
     * Sets all fields as a {@link java.util.Map} for any class extending {@link com.contentful.java.model.CDABaseItem}
     * out of a {@link com.google.gson.JsonObject} instance.
     *
     * @param jsonDeserializationContext De-serialization context.
     * @param jsonObject                 Object to read values from.
     * @return Map instance representing the fields for this CDA object.
     */
    public static Map<String, ?> createFieldsMap(JsonDeserializationContext jsonDeserializationContext,
                                                 JsonObject jsonObject) {

        Object fields = jsonObject.get("fields");

        if (fields != null && fields instanceof JsonObject) {
            return jsonDeserializationContext.deserialize((JsonElement) fields, Map.class);
        }

        return null;
    }

    /**
     * Determine whether an item is a {@link CDAEntry} subclass.
     *
     * @param item Item to be checked.
     * @return Boolean indicating whether this item is a subclass of {@link CDAEntry}.
     */
    public static boolean isEntry(CDABaseItem item) {
        return item instanceof CDAEntry;
    }

    /**
     * Determine whether an item is a {@link CDAAsset} subclass.
     *
     * @param item Item to be checked.
     * @return Boolean indicating whether this item is a subclass of {@link CDAAsset}.
     */
    public static boolean isAsset(CDABaseItem item) {
        return item instanceof CDAAsset;
    }
}

