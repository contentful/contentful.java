package com.contentful.java.api;

import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Custom type adapter for de-serializing resources.
 */
class ResourceTypeAdapter implements JsonDeserializer<CDAResource> {
    // Client reference.
    private final CDAClient client;

    public ResourceTypeAdapter(CDAClient client) {
        this.client = client;
    }

    @Override
    public CDAResource deserialize(JsonElement jsonElement,
                                   Type type,
                                   JsonDeserializationContext context) throws JsonParseException {

        JsonObject sys = jsonElement.getAsJsonObject()
                .getAsJsonObject("sys");

        Constants.CDAResourceType resourceType =
                Constants.CDAResourceType.valueOf(sys.get("type").getAsString());

        CDAResource result;

        if (Constants.CDAResourceType.Asset.equals(resourceType)) {
            result = deserializeAsset(jsonElement, context, sys);
        } else if (Constants.CDAResourceType.Entry.equals(resourceType)) {
            result = deserializeEntry(jsonElement, context, sys);
        } else if (Constants.CDAResourceType.ContentType.equals(resourceType)) {
            result = deserializeContentType(jsonElement, context, sys);
        } else {
            result = deserializeResource(jsonElement, context, sys);
        }

        return result;
    }

    /**
     * De-serialize a resource of unknown type.
     *
     * @param jsonElement JsonElement representing the resource in JSON form.
     * @param context     De-serialization context.
     * @param sys         JsonObject representing the system attributes of the resource in JSON form.
     * @return {@link CDAAsset} instance.
     * @throws JsonParseException on failure.
     */
    private CDAResource deserializeResource(JsonElement jsonElement,
                                            JsonDeserializationContext context,
                                            JsonObject sys) {

        CDAResource result = new CDAResource();
        setBaseFields(result, sys, jsonElement, context);
        return result;
    }


    /**
     * De-serialize a resource of type Asset.
     *
     * @param jsonElement JsonElement representing the resource in JSON form.
     * @param context     De-serialization context.
     * @param sys         JsonObject representing the system attributes of the resource in JSON form.
     * @return {@link CDAAsset} instance.
     * @throws JsonParseException on failure.
     */
    private CDAAsset deserializeAsset(JsonElement jsonElement,
                                      JsonDeserializationContext context,
                                      JsonObject sys) throws JsonParseException {

        CDAAsset result = new CDAAsset();
        setBaseFields(result, sys, jsonElement, context);

        Map fileMap = (Map) result.getFields().get("file");
        result.setUrl(String.format("%s:%s", Constants.SCHEME_HTTPS, fileMap.get("url")));
        result.setMimeType((String) fileMap.get("contentType"));

        return result;
    }

    /**
     * De-serialize a resource of type Entry.
     *
     * This method should return an {@link CDAEntry} object or in case the resource
     * matches a previously registered custom class via {@link CDAClient#registerCustomClass}
     * an object of the custom class type will be created.
     *
     * @param jsonElement JsonElement representing the resource in JSON form.
     * @param context     De-serialization context.
     * @param sys         JsonObject representing the system attributes of the resource in JSON form.
     * @return {@link CDAEntry} instance or a subclass of it.
     * @throws JsonParseException on failure.
     */
    private CDAEntry deserializeEntry(JsonElement jsonElement,
                                      JsonDeserializationContext context,
                                      JsonObject sys) throws JsonParseException {

        CDAEntry result;

        String contentTypeId = sys.get("contentType").getAsJsonObject()
                .get("sys").getAsJsonObject()
                .get("id").getAsString();

        Class<?> clazz = client.getCustomTypesMap().get(contentTypeId);

        if (clazz == null) {
            // Create a regular Entry, no custom class was registered.
            result = new CDAEntry();
        } else {
            // Use custom class registered for this Content Type.
            try {
                result = (CDAEntry) clazz.newInstance();
            } catch (InstantiationException e) {
                throw new JsonParseException(e);
            } catch (IllegalAccessException e) {
                throw new JsonParseException(e);
            }
        }

        if (result != null) {
            setBaseFields(result, sys, jsonElement, context);
        }

        return result;
    }

    /**
     * De-serialize a resource of type Content Type.
     *
     * @param jsonElement JsonElement representing the resource in JSON form.
     * @param context     De-serialization context.
     * @param sys         JsonObject representing the system attributes of the resource in JSON form.
     * @return {@link CDAContentType} instance.
     * @throws JsonParseException on failure.
     */
    private CDAContentType deserializeContentType(JsonElement jsonElement,
                                                  JsonDeserializationContext context,
                                                  JsonObject sys) {

        // display field
        JsonObject attrs = jsonElement.getAsJsonObject();
        String displayField = attrs.get("displayField").getAsString();

        // name
        String name = attrs.get("name").getAsString();

        // description (optional)
        String userDescription = null;
        JsonElement descriptionField = attrs.get("description");

        if (descriptionField != null) {
            userDescription = descriptionField.getAsString();
        }

        CDAContentType result = new CDAContentType(displayField, name, userDescription);

        setBaseFields(result, sys, jsonElement, context);

        return result;
    }

    /**
     * Sets the base fields for a {@link CDAResource} object.
     * This will set the list fields based on the target's type, depending on whether
     * it is an instance of the {@link ResourceWithMap} class or the {@link ResourceWithList},
     * different results will be provided.
     *
     * This method will also set the map of system attributes for the resource.
     *
     * @param target      Target {@link CDAResource} object to set fields for.
     * @param sys         JsonObject representing the system attributes of the resource in JSON form.
     * @param jsonElement JsonElement representing the resource in JSON form.
     * @param context     De-serialization context.
     * @throws JsonParseException on failure.
     */
    private void setBaseFields(CDAResource target,
                               JsonObject sys,
                               JsonElement jsonElement,
                               JsonDeserializationContext context) throws JsonParseException {

        // System attributes
        Map<String, Object> sysMap = context.deserialize(sys, Map.class);
        sysMap.put("space", client.getSpace());
        target.setSys(sysMap);

        // Fields
        JsonElement fields = jsonElement.getAsJsonObject().get("fields");

        if (target instanceof ResourceWithMap) {
            ResourceWithMap res = (ResourceWithMap) target;

            res.setRawFields(context.<Map<String, Object>>deserialize(
                    fields.getAsJsonObject(),
                    Map.class));

            res.getLocalizedFieldsMap().put(
                    client.getSpace().getDefaultLocale(),
                    res.getRawFields());
        } else if (target instanceof ResourceWithList) {
            ResourceWithList<Object> res = (ResourceWithList<Object>) target;

            res.setFields(context.<List<Object>>deserialize(
                    fields.getAsJsonArray(),
                    List.class));
        }
    }
}
