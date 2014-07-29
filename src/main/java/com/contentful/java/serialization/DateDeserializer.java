package com.contentful.java.serialization;

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
 * todo: perhaps JodaTime could provide a better solution than this.
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String[] ISO8601_PATTERNS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss+"
    };

    private static final String DEFAULT_TIMEZONE = "UTC";

    @Override
    public Date deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        Date result = null;
        String dateStr = jsonElement.getAsString();

        for (String pattern : ISO8601_PATTERNS) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

            try {
                result = sdf.parse(dateStr);
            } catch (ParseException ignore) {
            }

            if (result != null) {
                break;
            }
        }

        return result;
    }
}
