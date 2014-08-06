package com.contentful.java.model;

import com.contentful.java.lib.Constants;

import java.util.ArrayList;

/**
 * Class representing a single Space resource.
 */
public class CDASpace extends ResourceWithMap {
    private String defaultLocale = Constants.DEFAULT_LOCALE; // todo tom TMP!!
    private ArrayList<Locale> locales;
    private String name;

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public ArrayList<Locale> getLocales() {
        return locales;
    }

    public String getName() {
        return name;
    }
}
