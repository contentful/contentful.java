package com.contentful.java.api;

import com.contentful.java.model.ArrayResource;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDASyncedSpace;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Custom type adapter for de-serializing array resources.
 */
class ArrayResourceTypeAdapter implements JsonDeserializer<ArrayResource> {
    private CDAClient client;
    private Gson gson;

    ArrayResourceTypeAdapter(CDAClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    @Override
    public ArrayResource deserialize(JsonElement jsonElement,
                                     Type type,
                                     JsonDeserializationContext context) throws JsonParseException {

        ArrayResource result = null;

        if (CDAArray.class.equals(type)) {
            try {
                result = deserialize(CDAArray.class, jsonElement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (CDASyncedSpace.class.equals(type)) {
            try {
                result = deserialize(CDASyncedSpace.class, jsonElement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    <T extends ArrayResource> T deserialize(Class<T> clazz, JsonElement jsonElement) throws Exception {
        return new ArrayParser<T>(
                gson.fromJson(jsonElement, clazz),
                client.getSpace())
                .call();
    }
}
