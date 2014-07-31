package com.contentful.java.model;

import com.contentful.java.utils.Utils;

import java.util.Map;

/**
 * Base class for all CDA entities.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDABaseItem {
    public Sys sys;
    public Map<String, ?> fieldsMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CDABaseItem)) return false;

        CDABaseItem that = (CDABaseItem) o;

        return sys.id.equals(that.sys.id) &&
                sys.type.equals(that.sys.type);
    }

    @Override
    public int hashCode() {
        return Utils.getUniqueIdForItem(this).hashCode();
    }
}
