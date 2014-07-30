package com.contentful.java.serialization;

import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;
import com.contentful.java.utils.Utils;
import com.google.gson.*;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * A custom Deserializer to be used with Gson.
 * This class will attempt to de-serialize JSON strings to their corresponding class types
 * as defined on the SDK or configured by the user through the
 * {@link CDAClient#registerCustomClass(String, Class)} method.
 */
public class BaseDeserializer implements JsonDeserializer<CDABaseItem> {
    private CDAClient client;

    public BaseDeserializer(CDAClient client) {
        super();
        this.client = client;
    }

    @Override
    public CDABaseItem deserialize(JsonElement jsonElement,
                                   Type type,
                                   JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JSONObject jsonObject = new JSONObject(jsonElement.toString());

        // Content Type
        String itemType = getType(jsonObject);
        Constants.CDAType cdaType = Constants.CDAType.valueOf(itemType);

        // UID
        String id = getId(jsonObject);

        CDABaseItem result = null;

        if (Constants.CDAType.Asset.equals(cdaType)) {
            // Asset
            result = CDAClient.getBaseGson().fromJson(jsonElement, CDAAsset.class);
        } else if (Constants.CDAType.Entry.equals(cdaType)) {
            // Entry
            Class<?> clazz = client.getCustomTypesMap().get(id);

            if (clazz == null) {
                // no custom class registered for this Content Type
                result = CDAClient.getBaseGson().fromJson(jsonElement, CDAEntry.class);
            } else {
                // custom class registered for this Content Type
                result = jsonDeserializationContext.deserialize(jsonElement, clazz);
            }
        } else if (Constants.CDAType.Link.equals(cdaType)) {
            // Link
            String linkType = getLinkType(jsonObject);

            if (Constants.CDAType.Entry.equals(Constants.CDAType.valueOf(linkType))) {
                result = CDAClient.getBaseGson().fromJson(jsonElement, CDAEntry.class);
            } else if (Constants.CDAType.Asset.equals(Constants.CDAType.valueOf(linkType))) {
                result = CDAClient.getBaseGson().fromJson(jsonElement, CDAAsset.class);
            }
        } else if (Constants.CDAType.ContentType.equals(cdaType)) {
            result = CDAClient.getBaseGson().fromJson(jsonElement, CDAContentType.class);
        } else {
            result = CDAClient.getBaseGson().fromJson(jsonElement, type);
        }

        // also set fields map
        if (result != null) {
            if (jsonElement instanceof JsonObject) {
                result.fieldsMap = Utils.createFieldsMap(
                        jsonDeserializationContext,
                        (JsonObject) jsonElement);
            }
        }

        return result;
    }

    /**
     * Extract the "linkType" system attribute for a specific object.
     *
     * @param source {@link org.json.JSONObject} source to parse.
     * @return {@link java.lang.String} representing the "linkType", null on failure.
     */
    private String getLinkType(JSONObject source) {
        JSONObject sys = source.optJSONObject("sys");

        if (sys != null) {
            return sys.optString("linkType");
        }

        return null;
    }

    /**
     * Extract the "type" system attribute for a specific object.
     *
     * @param source {@link org.json.JSONObject} source to parse.
     * @return {@link java.lang.String} representing the type, null on failure.
     */
    private String getType(JSONObject source) {
        JSONObject sys = source.optJSONObject("sys");

        if (sys != null) {
            return sys.optString("type");
        }

        return null;
    }

    /**
     * Extract the Content Type ID out of a list of object's system attributes.
     *
     * @param source {@link org.json.JSONObject} source to parse.
     * @return {@link java.lang.String} representing the ID, null on failure.
     */
    private String getId(JSONObject source) {
        JSONObject sys = source.optJSONObject("sys");

        if (sys != null) {
            JSONObject contentType = sys.optJSONObject("contentType");

            if (contentType != null) {
                sys = contentType.optJSONObject("sys");

                if (sys != null) {
                    return sys.optString("id");
                }
            }
        }

        return null;
    }
}