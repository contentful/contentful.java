package com.contentful.java.model;

import java.util.ArrayList;

/**
 * Class representing a single Space resource.
 */
public class CDASpace extends CDAResource {
    private String defaultLocale;
    private ArrayList<Locale> locales;
    private String name;

    public CDASpace(String defaultLocale, ArrayList<Locale> locales, String name) {
        this.defaultLocale = defaultLocale;
        this.locales = locales;
        this.name = name;
    }

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
