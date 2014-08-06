package com.contentful.java.model;

/**
 * Class representing a single Asset resource.
 */
public class CDAAsset extends ResourceWithMap {
    // Asset URL
    private String url;

    // MIME type
    private String mimeType;

    /**
     * Gets the URL of this Asset.
     *
     * @return String representing the URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of this Asset.
     *
     * @param url String representing the URL.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the MIME type of this Asset.
     *
     * @return String representing the MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type of this Asset.
     *
     * @param mimeType String representing the MIME type.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
