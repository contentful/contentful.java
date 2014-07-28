package com.contentful.java.lib;

import com.contentful.java.CDAEntry;

import java.util.Date;
import java.util.List;

/**
 * Created by tomxor on 28/07/14.
 */
public class NyanCat extends CDAEntry {
    public static class Fields {
        public String name;
        public List<String> likes;
        public String color;
        public Date birthday;
        public Integer lives;
    }

    public Fields fields;
}
