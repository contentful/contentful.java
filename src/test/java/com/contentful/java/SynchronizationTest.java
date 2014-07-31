package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.lib.TestClientResult;
import com.contentful.java.model.CDASyncedSpace;

/**
 * Created by tomxor on 31/07/14.
 */
public class SynchronizationTest extends AbsTestCase {
    public void testSynchronization() throws Exception {
        TestClientResult<CDASyncedSpace> result = new TestClientResult<CDASyncedSpace>();

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_initial.json"));

        customClient.performSynchronization(true, new TestCallback<CDASyncedSpace>(result));

        result.cdl.await();
        verifyResultNotEmpty(result);
    }
}
