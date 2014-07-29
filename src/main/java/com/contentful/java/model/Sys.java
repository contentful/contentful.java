package com.contentful.java.model;

import java.util.Date;

/**
 * Class representing a collection of system attributes.
 */
@SuppressWarnings("UnusedDeclaration")
public class Sys {
    // Type of resource
    public String type;

    // Unique ID of resource
    public String id;

    // Version of resource.
    public Integer revision;

    // Time entity was created.
    public Date createdAt;

    // Time entity was updated.
    public Date updatedAt;

    // Content Type
    public CDAContentType contentType;

    // Type of Link
    public String linkType;

    // todo Link to object's Space
}
