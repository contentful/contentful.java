package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Class representing a single Content Type resource.
 */
public class CDAContentType extends ResourceWithList<Map> {
    private String displayField;
    private String name;

    @SerializedName("description")
    private String userDescription;

    public CDAContentType(String displayField, String name, String userDescription) {
        this.displayField = displayField;
        this.name = name;
        this.userDescription = userDescription;
    }

    public String getDisplayField() {
        return displayField;
    }

    public String getName() {
        return name;
    }

    public String getUserDescription() {
        return userDescription;
    }
}
