package com.contentful.java.api;

import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAResource;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * SDK utilities
 */
class Utils {
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

    private static String assertArray(CDAArray array) {
        String url = array.getOriginalUrl();

        if (url == null) {
            throw new IllegalArgumentException("Invalid array instance! (empty or unsuccessful response)");
        }

        return url;
    }

    /**
     * Populates a {@link java.util.Map} object with items to fetch the next
     * batch of items from a previous {@link com.contentful.java.model.CDAArray} item.
     *
     * @param array {@link com.contentful.java.model.CDAArray} instance which was successfully executed,
     *              meaning {@link com.contentful.java.api.CDACallback#onSuccess} was
     *              called.
     * @return {@link java.util.Map} instance containing original query string parameters
     * and updated pagination parameters (skip/limit).
     */
    public static HashMap<String, String> getNextBatchQueryMapForArray(CDAArray array) {
        assertArray(array);

        // extract pagination parameters
        int skip = array.getSkip();
        int limit = array.getLimit();
        int total = array.getTotal();

        // calculate next offset
        int nextOffset = skip + limit;

        return prepareQueryMap(URI.create(array.getOriginalUrl()), nextOffset, limit);
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

    static void saveResourceToFile(CDAResource resource, File file) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(resource);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static CDAResource readResourceFromFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream oos = null;
        CDAResource result = null;

        try {
            fis = new FileInputStream(file);
            oos = new ObjectInputStream(fis);

            result = (CDAResource) oos.readObject();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}

