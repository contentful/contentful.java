package com.contentful.java.lib;

/**
 * Library constants.
 */
public class Constants {
    public enum CDAResourceType {
        Array,
        Asset,
        ContentType,
        Entry,
        Link,
        Space,
        DeletedAsset,
        DeletedEntry
    }

    public static final String HTTPS = "HTTPS";
    public static final String HTTP_HEADER_AUTH = "Authorization";
    public static final String HTTP_OAUTH_PATTERN = "Bearer %s";

    public static final String SERVER_HOSTNAME = "cdn.contentful.com";
    public static final String SERVER_URI = String.format("%s://%s", HTTPS, SERVER_HOSTNAME);

    public static final String DEFAULT_LOCALE = "en-US";

    public static final String IDLE_THREAD_NAME = "Contentful-Idle";
}
