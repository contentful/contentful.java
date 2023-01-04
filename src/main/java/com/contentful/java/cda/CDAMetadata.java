package com.contentful.java.cda;

import java.io.Serializable;
import java.util.List;
public class CDAMetadata implements Serializable {
    private static final long serialVersionUID = -2852530837647649035L;
    public List<CDATag> getTags() {
        return tags;
    }

    public void setTags(List<CDATag> tags) {
        this.tags = tags;
    }
    List<CDATag> tags;

    @Override
    public String toString() {
        return "CDAMetadata{}";
    }
}