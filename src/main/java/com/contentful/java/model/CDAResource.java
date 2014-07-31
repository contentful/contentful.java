package com.contentful.java.model;

import com.contentful.java.lib.Constants;

import java.util.Map;

/**
 * Created by tomxor on 01/08/14.
 */
public class CDAResource {
    Map<String, Object> sys;

    String locale;

    public CDAResource() {
        this.locale = Constants.DEFAULT_LOCALE; // todo use Locale of current Space
    }

    public Map getSys() {
        return sys;
    }

    public void setSys(Map<String, Object> sys) {
        this.sys = sys;
    }

    // todo get/set locale

    // todo resolveWithSuccess
}
