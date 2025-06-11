package com.contentful.java.cda;

import java.io.Serializable;
import java.util.List;

public class CDAMetadata implements Serializable {
    private static final long serialVersionUID = -2852530837647649035L;
    private List<CDATag> tags;
    private List<CDATaxonomyConcept> concepts;

    public List<CDATag> getTags() {
        return tags;
    }

    public List<CDATaxonomyConcept> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<CDATaxonomyConcept> concepts) {
        this.concepts = concepts;
    }

    @Override
    public String toString() {
        return "CDAMetadata{}";
    }
}