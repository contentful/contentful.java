package com.contentful.java.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Base class for CDA resources.
 */
public class CDAResource implements Serializable {
    private Map<String, Object> sys;

    protected String locale;

    /**
     * Gets this resource's system attributes.
     *
     * @return Map populated with this resource's system attributes.
     */
    public Map getSys() {
        return sys;
    }

    /**
     * Sets this resource's system attribtues map.
     *
     * @param sys Map instance.
     */
    public void setSys(Map<String, Object> sys) {
        this.sys = sys;
    }

    /**
     * Gets the current locale for this resource.
     *
     * @return String representing the current locale for this resource.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the current locale for this resource.
     *
     * @param locale String representing the locale to be set.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CDAResource)) return false;

        CDAResource that = (CDAResource) o;

        return uniqueIdForResource(this).equals(uniqueIdForResource(that));
    }

    @Override
    public int hashCode() {
        return uniqueIdForResource(this).hashCode();
    }

    /**
     * Gets a UID for a resource combining it's {@code id} and {@code type} system attributes values.
     *
     * @param resource Resource instance.
     * @return String representing the UID.
     */
    private static String uniqueIdForResource(CDAResource resource) {
        Map sysMap = resource.getSys();
        return String.format("%s:%s", sysMap.get("id"), sysMap.get("type"));
    }
}
