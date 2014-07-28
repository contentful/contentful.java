package com.contentful.java;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A custom Deserializer to be used with Gson.
 * This class will attempt to de-serialize date strings according to the ISO8601 standard.
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String PATTERN_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DEFAULT_TIMEZONE = "UTC";

    @Override
    public Date deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        Date result = null;
        String dateStr = jsonElement.getAsString();
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_ISO8601);
        sdf.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
