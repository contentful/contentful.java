package com.contentful.java.lib;

import com.contentful.java.model.CDAEntry;

import java.util.List;

/**
 * Sample model class for CDA Entry.
 */
public class NyanCat extends CDAEntry {
    public String getName() {
        return (String) getFields().get("name");
    }

    public List<String> getLikes() {
        return (List<String>) getFields().get("likes");
    }

    public String getColor() {
        return (String) getFields().get("color");
    }

    public String getBirthday() {
        return (String) getFields().get("birthday");
    }

    public Integer getLives() {
        return ((Double) getFields().get("lives")).intValue();
    }

    public NyanCat getBestFriend() {
        return (NyanCat) getFields().get("bestFriend");
    }
}
