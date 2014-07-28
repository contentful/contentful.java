package com.contentful.java;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * A custom Deserializer to be used with Gson.
 * This class will attempt to de-serialize JSON strings to their corresponding class types
 * as defined by the SDK or by the user through the {@link CDAClient#registerCustomClass(String, Class)}
 * method.
 */
public class EntryDeserializer implements JsonDeserializer<CDABaseItem> {
    private CDAClient client;

    public EntryDeserializer(CDAClient client) {
        super();
        this.client = client;
    }

    @Override
    public CDABaseItem deserialize(JsonElement jsonElement,
                                   Type type,
                                   JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = (JsonObject) jsonElement;

        String cdaType = getType(jsonObject);
        String id = getId(jsonObject);

        CDABaseItem result = null;

        if (Constants.CDAType.Asset.equals(Constants.CDAType.valueOf(cdaType))) {
            // Asset
            result = jsonDeserializationContext.deserialize(jsonElement, CDAAsset.class);
        } else if (Constants.CDAType.Entry.equals(Constants.CDAType.valueOf(cdaType))) {
            // Entry
            Class<?> clazz = client.getCustomTypesMap().get(id);

            if (clazz == null) {
                result = CDAClient.getGsonWithDateAdapter().fromJson(jsonElement, CDAEntry.class);
            } else {
                result = jsonDeserializationContext.deserialize(jsonElement, clazz);
            }
        }

        // also set fields map
        if (result != null) {
            result.fieldsMap = Utils.createFieldsMap(
                    jsonDeserializationContext,
                    (JsonObject) jsonElement);
        }

        return result;
    }

    private String getType(JsonObject jsonObject) {
        String type = null;

        try {
            // todo tom parse this correctly
            type = jsonObject
                    .getAsJsonObject("sys")
                    .get("type")
                    .getAsString();
        } catch (ClassCastException ignore) {
        } catch (IllegalStateException ignore) {
        } catch (NullPointerException ignore) {
        }

        return type;
    }

    private String getId(JsonObject jsonObject) {
        String result = null;

        try {
            // todo tom parse this correctly
            result = jsonObject
                    .getAsJsonObject("sys")
                    .getAsJsonObject("contentType")
                    .getAsJsonObject("sys")
                    .get("id")
                    .getAsString();
        } catch (ClassCastException ignore) {
        } catch (IllegalStateException ignore) {
        } catch (NullPointerException ignore) {
        }

        return result;
    }
}