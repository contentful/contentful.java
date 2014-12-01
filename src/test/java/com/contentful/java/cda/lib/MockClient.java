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

package com.contentful.java.cda.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Mock client class that returns a successful response with a response body
 * read from a file within resources folder.
 */
public class MockClient implements Client {
  private static final Pattern PATTERN_SPACE = Pattern.compile("^/spaces/([a-zA-Z0-9\\-]+)/?$");

  private final String resourceFileName;
  private final String spaceFileName;

  public MockClient(String resourceFileName) {
    this(resourceFileName, "space.json");
  }

  public MockClient(String resourceFileName, String spaceFileName) {
    this.resourceFileName = resourceFileName;
    this.spaceFileName = spaceFileName;
  }

  @SuppressWarnings("unchecked")
  @Override public Response execute(Request request) throws IOException {
    InputStream is = null;
    String responseString = "";

    try {
      boolean isSpace = PATTERN_SPACE.matcher(URI.create(request.getUrl()).getPath()).matches();
      String filename;

      if (isSpace) {
        filename = spaceFileName;
      } else {
        filename = resourceFileName;
      }

      is = MockClient.class.getResourceAsStream(File.separator + filename);
      responseString = IOUtils.toString(is);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ignore) {
        }
      }
    }

    return new Response(request.getUrl(), 200, "OK", Collections.EMPTY_LIST,
        new TypedByteArray("application/json", responseString.getBytes()));
  }
}
