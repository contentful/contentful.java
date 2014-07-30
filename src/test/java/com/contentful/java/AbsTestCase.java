package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.TestClientFactory;
import junit.framework.TestCase;

/**
 * Base class for test cases, just sets up a {@link CDAClient} for now.
 */
public abstract class AbsTestCase extends TestCase {
    protected CDAClient client;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        client = TestClientFactory.newInstance();
    }
}
