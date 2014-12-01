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

/**
 * Library constants.
 */
public class Constants {
  private Constants() {
  }

  public static enum CDAResourceType {
    Asset,
    ContentType,
    DeletedAsset,
    DeletedEntry,
    Entry,
    Link,
    Space
  }

  @SuppressWarnings("UnusedDeclaration")
  public static enum CDAFieldType {
    Array,
    Boolean,
    Date,
    Integer,
    Link,
    Location,
    Number,
    Object,
    Symbol,
    Text
  }

  // HTTP constants
  public static final String SCHEME_HTTP = "http";
  public static final String SCHEME_HTTPS = "https";
  public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
  public static final String HTTP_HEADER_AUTH = "Authorization";
  public static final String HTTP_OAUTH_PATTERN = "Bearer %s";

  // Configuration
  public static final String ENDPOINT_CDA = "cdn.contentful.com";
  public static final String ENDPOINT_PREVIEW = "preview.contentful.com";
  public static final String DEFAULT_LOCALE = "en-US";

  // Idle thread name
  public static final String IDLE_THREAD_NAME = "Contentful-Idle";

  // Resource types
  public static final String PATH_ASSETS = "assets";
  public static final String PATH_ENTRIES = "entries";
}
