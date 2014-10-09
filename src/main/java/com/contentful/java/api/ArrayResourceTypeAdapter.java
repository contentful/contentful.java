/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.api;

import com.contentful.java.model.ArrayResource;
import com.contentful.java.model.CDAArray;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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

  @Override public ArrayResource deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext context) throws JsonParseException {
    ArrayResource result = gson.fromJson(jsonElement, type);

    if (CDAArray.class.equals(type)) {
      try {
        result = parseArray((CDAArray) result);
      } catch (Exception e) {
        throw new JsonParseException(e);
      }
    }

    return result;
  }

  /**
   * Executes an {@code ArrayParser} with the given {@code source}, inferring the class type.
   *
   * @param source array instance to be parsed
   * @param <T> type of result object expected to be returned
   * @return result of the {@code ArrayParser} execution
   * @throws Exception in case of an error
   */
  <T extends ArrayResource> T parseArray(T source) throws Exception {
    return new ArrayParser<T>(source, client.getSpace()).call();
  }
}
