package com.contentful.java.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * A custom Deserializer to be used with Gson.
 * This class will attempt to de-serialize date strings according to the ISO8601 standard
 * using the Joda-Time library.
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        String dateStr = jsonElement.getAsString();
        DateTime result = DateTime.parse(dateStr);

        return result == null ? null : result.toDate();
    }
}
