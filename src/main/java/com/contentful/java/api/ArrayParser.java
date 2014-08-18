package com.contentful.java.api;

import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A custom Callable used internally for preparing array result objects.
 *
 * This will attempt to resolve all links within an {@link ArrayResource} instance,
 * while iterating through all of it's normal and included resources.
 */
class ArrayParser<T extends ArrayResource> implements Callable<T> {
    private final T source;
    private final CDASpace space;

    public ArrayParser(T source, CDASpace space) {
        this.source = source;
        this.space = space;
    }

    @Override
    public T call() throws Exception {
        HashMap<String, CDAResource> assets = new HashMap<String, CDAResource>();
        HashMap<String, CDAResource> entries = new HashMap<String, CDAResource>();

        ArrayList<CDAResource> items;

        if (source instanceof CDAArray) {
            items = ((CDAArray) source).getItems();

            CDAArray.Includes includes = ((CDAArray) source).getIncludes();

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
        } else if (source instanceof CDASyncedSpace) {
            items = ((CDASyncedSpace) source).getItems();
        } else {
            throw new IllegalArgumentException("Invalid result item.");
        }

        for (CDAResource item : items) {
            // Parse the resource - for Sync this will set the fields maps.
            parseResource(item);

            // Store the resource in the proper array according to it's UID.
            if (item instanceof CDAAsset) {
                assets.put((String) item.getSys().get("id"), item);
            } else if (item instanceof CDAEntry) {
                entries.put((String) item.getSys().get("id"), item);
            }
        }

        // Iterate through all Entries and attempt to resolve contained links.
        for (Map.Entry<String, CDAResource> entry : entries.entrySet()) {
            CDAResource item = entry.getValue();
            resolveResourceLinks(item, assets, entries);
        }

        return source;
    }

    /**
     * For Sync this will set all base fields for this Resource.
     *
     * @param resource Resource to be parsed.
     */
    private void parseResource(CDAResource resource) {
        if (source instanceof CDASyncedSpace) {
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

    /**
     * Attempts to resolve all links referenced by a resource.
     *
     * @param resource Resource instance.
     * @param assets   Mapping of Asset UIDs to objects.
     * @param entries  Mapping of Entry UIDs to objects.
     */
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
                    } else if (value instanceof List) {
                        List list = (List) value;

                        for (int i = 0; i < list.size(); i++) {
                            Object item = list.get(i);

                            if (item instanceof Map) {
                                CDAResource match = getMatchForField((Map) item, assets, entries);

                                if (match != null) {
                                    list.set(i, match);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets a single field for a resource which references a different resource (link) and
     * attempts to resolve it.
     *
     * @param map     Map representing the field's value (the link).
     * @param assets  Mapping of Asset UIDs to objects.
     * @param entries Mapping of Entry UIDs to objects.
     * @return {@link CDAResource} or a subclass of it, depending on the resource type, or null in case
     * the link is not resolvable from this context.
     */
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
}
