package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class representing a single Locale.
 */
@SuppressWarnings("UnusedDeclaration")
public class Locale implements Serializable {
    public String code;

    @SerializedName("default")
    public boolean isDefault;

    public String name;
}
