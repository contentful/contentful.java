package com.contentful.java.cda;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CDATaxonomyConcept extends CDAResource {
    private static final long serialVersionUID = -2852530837647669036L;

    @SerializedName("prefLabel")
    protected Map<String, String> prefLabel;

    /**
     * @return the preferred label map containing locale-to-label mappings.
     */
    public Map<String, String> prefLabel() {
        return prefLabel;
    }

    /**
     * Get the preferred label for a specific locale.
     * @param locale the locale code (e.g., "en-US")
     * @return the label for the specified locale, or null if not found
     */
    public String getPrefLabel(String locale) {
        return prefLabel != null ? prefLabel.get(locale) : null;
    }

    @Override
    public String toString() {
        return "CDATaxonomyConcept{"
                + "attrs=" + attrs + '\''
                + ", prefLabel=" + prefLabel
                + '}';
    }
}