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

package com.contentful.java.cda;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.concurrent.Executor;

/**
 * SDK utilities
 */
class Utils {
  static final Decoder DECODER = new Decoder() {
    public String decode(String url) throws UnsupportedEncodingException {
      return URLDecoder.decode(url, "UTF-8");
    }
  };

  private Utils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Asserts that the given {@code object} with name {@code param} is not null, throws
   * {@link IllegalArgumentException} otherwise.
   */
  static void assertNotNull(Object object, String param) {
    if (object == null) {
      throw new IllegalArgumentException(String.format(
          "%s may not be null.", param));
    }
  }

  static String getQueryParamFromUrl(String url, String param) {
    return getQueryParamFromUrl(url, param, DECODER);
  }

  static String getQueryParamFromUrl(String url, String param, Decoder decoder) {
    URI uri = URI.create(url);
    String query = uri.getQuery();

    if (query == null) {
      return null;
    }

    String[] pairs = query.split("&");
    for (String pair : pairs) {
      String[] split = pair.split("=");
      if (split.length != 2) {
        continue;
      }

      if (param.equalsIgnoreCase(split[0])) {
        try {
          return decoder.decode(split[1]);
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return null;
  }

  interface Decoder {
    String decode(String url) throws UnsupportedEncodingException;
  }

  static class SynchronousExecutor implements Executor {
    public void execute(Runnable runnable) {
      runnable.run();
    }
  }
}

