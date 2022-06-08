package com.contentful.java.cda;

import java.util.List;
public class CDAMetadata {
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
