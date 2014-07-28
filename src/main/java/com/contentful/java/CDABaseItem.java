package com.contentful.java;

import java.util.Map;

/**
 * Base class for Contentful entities (i.e. Asset, Entry, Space, ...)
 */
@SuppressWarnings("UnusedDeclaration")
public class CDABaseItem {
    public Sys sys;

    public Map<String, ?> fieldsMap;
}
