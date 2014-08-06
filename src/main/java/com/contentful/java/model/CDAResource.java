package com.contentful.java.model;

import com.contentful.java.lib.Constants;

import java.util.Map;

/**
 * Base class for CDA resources.
 */
public class CDAResource {
    private Map<String, Object> sys;

    protected String locale;

    public CDAResource() {
        this.locale = Constants.DEFAULT_LOCALE; // todo use Locale of current Space
    }

    public Map getSys() {
        return sys;
    }

    public void setSys(Map<String, Object> sys) {
        this.sys = sys;
    }

    // todo get/set locale

    // todo resolveWithSuccess

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

    private static String uniqueIdForResource(CDAResource resource) {
        Map sysMap = resource.getSys();
        return String.format("%s:%s", sysMap.get("id"), sysMap.get("type"));
    }
}
