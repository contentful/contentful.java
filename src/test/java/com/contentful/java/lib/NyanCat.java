package com.contentful.java.lib;

import com.contentful.java.annotations.CDAFields;
import com.contentful.java.model.CDAEntry;

import java.util.Date;
import java.util.List;

/**
 * Sample model class for CDA Entry.
 */
public class NyanCat extends CDAEntry {
    public static class Fields {
        public String name;
        public List<String> likes;
        public String color;
        public Date birthday;
        public Integer lives;
        public NyanCat bestFriend;
    }

    @CDAFields
    public Fields fields;
}
