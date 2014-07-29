package com.contentful.java;

import java.util.Date;

/**
 * Class representing a collection of system attributes.
 */
public class Sys {
    // Type of resource
    public String type;

    // Unique ID of resource
    public String id;

    // todo Link space - Link to resource's Space (except Spaces) .

    // todo Link contentType - Link to Entry's Content Type (Entries only).

    // Version of resource.
    public Integer revision;

    // Time entity was created.
    public Date createdAt;

    // Time entity was updated.
    public Date updatedAt;

    public CDAContentType contentType;

    public String linkType;
}
