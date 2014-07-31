package com.contentful.java.model;

import com.contentful.java.exceptions.LocaleNotFoundException;
import com.contentful.java.lib.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for localized Strings.
 */
@SuppressWarnings("UnusedDeclaration")
public class LocalizedString {
    private String locale;
    private Map<String, String> values;

    public static LocalizedString fromString(String str) {
        LocalizedString instance = new LocalizedString();
        instance.add(instance.locale, str);
        return instance;
    }

    public static LocalizedString fromMap(Map<String, String> map) {
        LocalizedString instance = new LocalizedString();
        instance.values = map;
        return instance;
    }

    public LocalizedString() {
        this.locale = Constants.DEFAULT_LOCALE;
        this.values = new HashMap<String, String>();
    }

    public void add(String locale, String string) {
        values.put(locale, string);
    }

    public String getValue() {
        return values.get(this.locale);
    }

    public String getValue(String locale) throws LocaleNotFoundException {
        ensureLocale(locale);
        return values.get(locale);
    }

    public void setLocale(String locale) throws LocaleNotFoundException {
        ensureLocale(locale);
        this.locale = locale;
    }

    private void ensureLocale(String locale) throws LocaleNotFoundException {
        if (!values.containsKey(locale)) {
            throw new LocaleNotFoundException(locale);
        }
    }
}
