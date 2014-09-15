package com.contentful.java.lib;

/**
 * Library constants.
 */
public class Constants {
    public static enum CDAResourceType {
        Asset,
        ContentType,
        DeletedAsset,
        DeletedEntry,
        Entry,
        Link,
        Space
    }

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
    public static final String CDA_SERVER_HOSTNAME = "cdn.contentful.com";
    public static final String CDA_SERVER_URI = String.format("%s://%s", SCHEME_HTTPS, CDA_SERVER_HOSTNAME);
    public static final String CDA_SERVER_URI_NOSSL = String.format("%s://%s", SCHEME_HTTP, CDA_SERVER_HOSTNAME);
    public static final String DEFAULT_LOCALE = "en-US";

    // Idle thread name
    public static final String IDLE_THREAD_NAME = "Contentful-Idle";

    // Resource types
    public static final String PATH_ASSETS = "assets";
    public static final String PATH_ENTRIES = "entries";
}
