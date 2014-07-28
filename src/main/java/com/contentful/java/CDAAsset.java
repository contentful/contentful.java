package com.contentful.java;

/**
 * Class representing a single Asset
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAAsset extends CDABaseItem {
    static class Fields {
        public String title;
        public String description;
        public CDAFile file;
    }

    public Fields fields;
}
