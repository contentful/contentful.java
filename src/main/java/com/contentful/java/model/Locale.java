package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class representing a single Locale.
 */
@SuppressWarnings("UnusedDeclaration")
public class Locale {
    public String code;

    @SerializedName("default")
    public boolean isDefault;

    public String name;
}
