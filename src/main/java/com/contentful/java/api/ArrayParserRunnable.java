package com.contentful.java.api;

import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom runnable used internally for preparing array result objects.
 *
 * This will attempt to resolve all links within a {@link CDAArray} instance,
 * while iterating through all of it's normal and included resources.
 *
 * Note: this does some <b>extensive</b> parsing, hence the {@link CDAClient} class
 * spawns any instances of it in the context of a background thread.
 */
class ArrayParserRunnable<T extends ArrayResource> implements Runnable {
    private final T result;
    private final CDACallback<T> callback;
    private CDASpace space;
    private final Response response;

    public ArrayParserRunnable(T result,
                               CDACallback<T> callback,
                               CDASpace space,
                               Response response) {

        this.result = result;
        this.callback = callback;
        this.space = space;
        this.response = response;
    }

    @Override
    public void run() {
        HashMap<String, CDAResource> assets = new HashMap<String, CDAResource>();
        HashMap<String, CDAResource> entries = new HashMap<String, CDAResource>();

        ArrayList<CDAResource> items;

        if (result instanceof CDAArray) {
            items = ((CDAArray) result).getItems();

            CDAArray.Includes includes = ((CDAArray) result).getIncludes();

            if (includes != null) {
                List<CDAAsset> includedAssets = includes.getAssets();
                List<CDAEntry> includedEntries = includes.getEntries();

                if (includedAssets != null) {
                    for (CDAResource item : includedAssets) {
                        assets.put((String) item.getSys().get("id"), item);
                    }
                }

                if (includedEntries != null) {
                    for (CDAResource item : includedEntries) {
                        entries.put((String) item.getSys().get("id"), item);
                    }
                }
            }
        } else if (result instanceof CDASyncedSpace) {
            items = ((CDASyncedSpace) result).getItems();
        } else {
            throw new IllegalArgumentException("Invalid result item.");
        }

        for (CDAResource item : items) {
            parseResource(item);

            if (item instanceof CDAAsset) {
                assets.put((String) item.getSys().get("id"), item);
            } else if (item instanceof CDAEntry) {
                entries.put((String) item.getSys().get("id"), item);
            }
        }

        for (Map.Entry<String, CDAResource> entry : entries.entrySet()) {
            CDAResource item = entry.getValue();
            resolveResourceLinks(item, assets, entries);
        }

        onFinish();
    }

    private void parseResource(CDAResource resource) {
        if (result instanceof CDASyncedSpace) {
            if (resource instanceof ResourceWithMap) {
                ResourceWithMap res = (ResourceWithMap) resource;

                HashMap<String, Map> localizedFields = res.getLocalizedFieldsMap();
                Map<String, Object> rawFields = res.getRawFields();

                // create a map for every locale
                for (Locale locale : space.getLocales()) {
                    localizedFields.put(locale.code, new HashMap<String, Object>());
                }

                // iterate through all fields
                for (String key : rawFields.keySet()) {
                    for (Locale locale : space.getLocales()) {
                        Map map = (Map) rawFields.get(key);
                        Object value = map.get(locale.code);

                        if (value != null) {
                            localizedFields.get(locale.code).put(key, value);
                        }
                    }
                }
            }
        }
    }

    private void resolveResourceLinks(CDAResource resource, HashMap<String, CDAResource> assets, HashMap<String, CDAResource> entries) {
        if (resource instanceof ResourceWithMap) {
            ResourceWithMap res = (ResourceWithMap) resource;
            HashMap<String, Map> localizedFields = res.getLocalizedFieldsMap();

            for (Map.Entry<String, Map> entry : localizedFields.entrySet()) {
                Map fields = entry.getValue();

                for (Object k : fields.keySet()) {
                    Object value = fields.get(k);

                    if (value instanceof Map) {
                        CDAResource match = getMatchForField((Map) value, assets, entries);

                        if (match != null) {
                            fields.put(k, match);
                        }
                    }
                }
            }
        }
    }

    private CDAResource getMatchForField(Map map, HashMap<String, CDAResource> assets, HashMap<String, CDAResource> entries) {
        CDAResource result = null;

        Map sys = (Map) map.get("sys");

        if (sys != null) {
            String type = (String) sys.get("type");

            if (Constants.CDAResourceType.Link.equals(Constants.CDAResourceType.valueOf(type))) {
                Constants.CDAResourceType linkType = Constants.CDAResourceType.valueOf((String) sys.get("linkType"));
                String id = (String) sys.get("id");

                if (Constants.CDAResourceType.Asset.equals(linkType)) {
                    result = assets.get(id);
                } else if (Constants.CDAResourceType.Entry.equals(linkType)) {
                    result = entries.get(id);
                } else if (Constants.CDAResourceType.Space.equals(linkType)) {
                    result = space;
                }
            }
        }

        return result;
    }

    void onFinish() {
        if (!callback.isCancelled()) {
            callback.success(result, response);
        }
    }
}
