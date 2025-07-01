package com.contentful.java.cda.rich;

import java.io.Serializable;

/**
 * A leaf node of the rich text hierarchy.
 */
public class CDARichNode implements Serializable {
    private String nodeType;

    /**
     * Get the original node type from the API response.
     * @return the node type (e.g., "embedded-entry-block", "embedded-entry-inline", etc.)
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Set the node type from the API response.
     * @param nodeType the original node type
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
