package com.contentful.java;

import com.google.gson.annotations.SerializedName;

/**
 * Class representing a single Locale.
 */
@SuppressWarnings("UnusedDeclaration")
public class Locale {
    String code;

    @SerializedName("default")
    boolean isDefault;

    String name;
}
