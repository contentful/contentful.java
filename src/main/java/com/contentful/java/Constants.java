package com.contentful.java;

/**
 * Library constants.
 */
public class Constants {
    public enum CDAType {
        Array,
        Asset,
        Entry,
        Link,
        Space
    }

    public static final String HTTPS = "HTTPS";
    public static final String HTTP_HEADER_AUTH = "Authorization";
    public static final String HTTP_OAUTH_PATTERN = "Bearer %s";

    public static final String SERVER_HOSTNAME = "cdn.contentful.com";
    public static final String SERVER_URI = String.format("%s://%s", HTTPS, SERVER_HOSTNAME);
}
