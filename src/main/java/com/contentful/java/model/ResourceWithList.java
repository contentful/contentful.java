package com.contentful.java.model;

import java.util.List;

/**
 * Class representing a CDA resource having an array of fields.
 */
public class ResourceWithList<T> extends CDAResource {
    // List of fields
    List<T> fields;

    /**
     * Gets the fields list for this resource.
     *
     * @return List of fields.
     */
    public List<T> getFields() {
        return fields;
    }

    /**
     * Sets the fields list for this resource.
     *
     * @param fields List of fields.
     */
    public void setFields(List<T> fields) {
        this.fields = fields;
    }
}
