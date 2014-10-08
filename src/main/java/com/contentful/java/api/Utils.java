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

import com.contentful.java.model.CDAArray;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

/**
 * SDK utilities
 */
class Utils {
  private Utils() {
  }

  static final String SDK_PROPERTIES = "sdk.properties";
  static final String PROP_VERSION_NAME = "version.name";

  /**
   * Returns the last path segment of the original URL which was used to fetch the given array.
   */
  static String getNextPageType(CDAArray array) {
    String url = assertArray(array);
    String result = null;

    try {
      URI uri = new URI(url);
      String[] split = uri.getPath().split("/");
      result = split[split.length - 1];
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Ensures an array has it's original URL associated with it, otherwise throws an exception.
   *
   * @param array {@code CDAArray} instance
   * @return string representing the original url
   */
  private static String assertArray(CDAArray array) {
    String url = array.getOriginalUrl();

    if (url == null) {
      throw new IllegalArgumentException(
          "Invalid array instance! (empty or unsuccessful response)");
    }

    return url;
  }

  /**
   * Prepares a query map to be used for fetching the next page of an array
   *
   * @param array existing, previously fetched array
   * @return map containing original query string parameters and updated pagination parameters for
   * the next request (skip/limit)
   */
  static HashMap<String, String> getNextBatchQueryMapForArray(CDAArray array) {
    assertArray(array);

    // extract pagination parameters
    int skip = array.getSkip();
    int limit = array.getLimit();

    // calculate next offset
    int nextOffset = skip + limit;

    return prepareQueryMap(URI.create(array.getOriginalUrl()), nextOffset, limit);
  }

  /**
   * Helper method for {@link #getNextBatchQueryMapForArray} that actually creates the query map.
   *
   * @param uri Original request URL.
   * @param nextOffset {@code skip} value to be used.
   * @param limit {@code limit} value to be used.
   * @return Map representing the query.
   */
  private static HashMap<String, String> prepareQueryMap(URI uri, int nextOffset, int limit) {
    // Prepare the new map
    HashMap<String, String> queryMap = new HashMap<String, String>();
    String query = uri.getQuery();

    if (query != null) {
      // Iterate through all the query string parameters from the original request and add them
      // to the new map, while stripping any pagination related arguments from it as those will
      // be specified next.
      String[] params = query.split("&");

      for (String p : params) {
        String[] kv = p.split("=");

        if ("skip".equalsIgnoreCase(kv[0]) || "limit".equalsIgnoreCase(kv[0])) {

          continue;
        }

        queryMap.put(kv[0], kv[1]);
      }
    }

    // Set new pagination parameters
    queryMap.put("skip", Integer.toString(nextOffset));
    queryMap.put("limit", Integer.toString(limit));

    return queryMap;
  }

  static String getFromProperties(String field) throws IOException {
    Properties properties = new Properties();
    properties.load(Utils.class.getClassLoader().getResourceAsStream(SDK_PROPERTIES));
    return properties.getProperty(field);
  }
}

