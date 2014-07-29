package com.contentful.java.model;

/**
 * TBD
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAFile {
    public Sys sys;
    public String fileName;
    public String contentType;
    public String url;

    /**
     * File details
     */
    public static class Details {
        // todo image, video, ...
        public Long size;
    }

    public Details details;
}
