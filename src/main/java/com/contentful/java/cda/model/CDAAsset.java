/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda.model;

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
