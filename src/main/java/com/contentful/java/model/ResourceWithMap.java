package com.contentful.java.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a CDA resource having a map of fields.
 */
public class ResourceWithMap extends CDAResource {
    // Map of fields in their raw form as retrieved from the CDA.
    private Map<String, Object> rawFields;

    // Custom map prepared out of the original fields sectioned by different locales.
    private HashMap<String, Map> localizedFieldsMap = new HashMap<String, Map>();

    /**
     * Sets the raw fields map of this resource.
     *
     * @param rawFields Map of fields as retrieved from the CDA.
     */
    public void setRawFields(Map<String, Object> rawFields) {
        this.rawFields = rawFields;
    }

    /**
     * Gets the raw fields map of this resource.
     *
     * @return Map of fields as retrieved from the CDA.
     */
    public Map<String, Object> getRawFields() {
        return rawFields;
    }

    /**
     * Gets a localized map of fields.
     *
     * @return A custom map prepared out of the original fields sectioned by different locales.
     */
    public HashMap<String, Map> getLocalizedFieldsMap() {
        return localizedFieldsMap;
    }

    /**
     * Convenience method to get a Map of fields using the default / defined locale.
     * If no locale was set, the fields map will be retrieved with the default Space locale.
     *
     * @return Map of fields.
     */
    public Map getFields() {
        return localizedFieldsMap.get(this.locale);
    }
}
