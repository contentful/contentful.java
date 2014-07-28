package com.contentful.java;

/**
 * TBD
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAFile extends CDABaseItem {
    public String fileName;
    public String contentType;
    public String url;

    /**
     * File details
     */
    static class Details {
        // todo image, video, ...
        public Long size;
    }

    public Details details;
}
