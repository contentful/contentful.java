package com.contentful.java.api;

import com.contentful.java.model.ArrayResource;
import com.contentful.java.model.CDAArray;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Custom type adapter for de-serializing array resources.
 */
class ArrayResourceTypeAdapter implements JsonDeserializer<ArrayResource> {
    private final CDAClient client;
    private final Gson gson;

    ArrayResourceTypeAdapter(CDAClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    @Override
    public ArrayResource deserialize(JsonElement jsonElement,
                                     Type type,
                                     JsonDeserializationContext context) throws JsonParseException {

        ArrayResource result = gson.fromJson(jsonElement, type);

        if (CDAArray.class.equals(type)) {
            try {
                result = parseArray(CDAArray.class, (CDAArray) result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    <T extends ArrayResource> T parseArray(Class<T> clazz, T source) throws Exception {
        return new ArrayParser<T>(
                source,
                client.getSpace())
                .call();
    }
}
