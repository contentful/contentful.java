package com.contentful.java.model;

import com.contentful.java.lib.Constants;

import java.util.ArrayList;

/**
 * Created by tomxor on 01/08/14.
 */
public class CDASpace extends ResourceWithMap {
    String defaultLocale = Constants.DEFAULT_LOCALE; // todo tom TMP!!
    ArrayList<Locale> locales;
    String name;

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
