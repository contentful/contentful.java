package com.contentful.java.exceptions;

/**
 * Exception to be thrown when attempting to get a string with a non-existing Locale
 * out of a {@link com.contentful.java.model.LocalizedString} object.
 */
public class LocaleNotFoundException extends Throwable {
    public LocaleNotFoundException(String locale) {
        super("Locale \"" + locale + "\" not found!");
    }
}
