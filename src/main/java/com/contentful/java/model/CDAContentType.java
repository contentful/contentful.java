package com.contentful.java.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Class representing a single Content Type.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAContentType extends CDABaseItem {
    public String name;
    public String description;
    public String displayField;

    @SerializedName("fields")
    public List<Map<String, ?>> fieldsList;
}
