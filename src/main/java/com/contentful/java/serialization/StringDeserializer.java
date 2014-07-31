package com.contentful.java.serialization;

import com.contentful.java.model.LocalizedString;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A class that handles de-serialization of Strings.
 * Given a normal JSON String, this will produce a {@link LocalizedString} with
 * the String value using the default Locale, on other cases all available localized Strings will be
 * added to the object's mapping using {@link LocalizedString#add}.
 */
public class StringDeserializer implements JsonDeserializer<LocalizedString> {
    @SuppressWarnings("unchecked")
    @Override
    public LocalizedString deserialize(JsonElement jsonElement,
                                       Type type,
                                       JsonDeserializationContext context) throws JsonParseException {

        LocalizedString result;

        if (jsonElement instanceof JsonObject) {
            result = LocalizedString.fromMap((Map<String, String>) context.deserialize(jsonElement, Map.class));
        } else {
            result = LocalizedString.fromString((String) context.deserialize(jsonElement, String.class));
        }

        return result;
    }
}
