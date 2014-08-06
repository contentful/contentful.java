package com.contentful.java;

import com.contentful.java.lib.TestCallback;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Base class for all test cases.
 */
public abstract class AbsTestCase {
    void verifyResultNotEmpty(TestCallback result) {
        // check that error is empty
        assertNull(result.error);

        // check that result is not empty
        assertNotNull(result.value);
    }
}
