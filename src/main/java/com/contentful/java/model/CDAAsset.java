package com.contentful.java.model;

import com.contentful.java.annotations.CDAFields;

/**
 * Class representing a single Asset
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAAsset extends CDABaseItem {
    public static class Fields {
        public String title;
        public String description;
        public CDAFile file;
    }

    @CDAFields
    public Fields fields;
}
