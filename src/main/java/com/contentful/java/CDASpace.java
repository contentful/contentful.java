package com.contentful.java;

import java.util.List;

/**
 * Class representing a single Space.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDASpace extends CDABaseItem {
    String name;
    List<Locale> locales;

    public String getName() {
        return name;
    }

    public List<Locale> getLocales() {
        return locales;
    }
}
