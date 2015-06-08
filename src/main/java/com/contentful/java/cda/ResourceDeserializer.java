package com.contentful.java.cda;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

import static com.contentful.java.cda.Constants.LOCALE;
import static com.contentful.java.cda.Util.classForType;

final class ResourceDeserializer implements JsonDeserializer<CDAResource> {
  @Override public CDAResource deserialize(JsonElement json, Type classType,
      JsonDeserializationContext context) throws JsonParseException {
    return context.deserialize(json, classForType(extractType(json)));
  }

  private CDAType extractType(JsonElement json) {
    String type = json.getAsJsonObject().get("sys").getAsJsonObject()
        .get("type")
        .getAsString();

    return CDAType.valueOf(type.toUpperCase(LOCALE));
  }
}
