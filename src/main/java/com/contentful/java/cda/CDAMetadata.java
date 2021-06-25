package com.contentful.java.cda;

import java.util.List;
import java.util.Map;

public class CDAMetadata {
    public List<CDAField> getTags() {
        return tags;
    }

    public void setTags(List<CDAField> tags) {
        this.tags = tags;
    }
    List<CDAField> tags;

    @Override
    public String toString() {
        return "CDAMetadata{" +
                "tags=" + tags +
                '}';
    }
}
