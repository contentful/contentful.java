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

    /**
     * Gets the display field of this Content Type.
     *
     * @return String representing the field's name.
     */
    public String getDisplayField() {
        return displayField;
    }

    /**
     * Gets the name of this Content Type.
     *
     * @return String representing the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this Content Type.
     * Note this attribute is optional for the time this is being written so it may or may not return null.
     *
     * @return String representing the description.
     */
    public String getUserDescription() {
        return userDescription;
    }
}
