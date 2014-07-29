package com.contentful.java;

import com.contentful.java.lib.TestClientFactory;
import junit.framework.TestCase;

/**
 * Created by tomxor on 28/07/14.
 */
public abstract class AbsTestCase extends TestCase {
    protected CDAClient client;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        client = TestClientFactory.newInstance();
    }
}
