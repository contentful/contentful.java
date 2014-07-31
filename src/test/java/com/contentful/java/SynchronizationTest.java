package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.*;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDASyncedSpace;

import java.util.Map;

/**
 * Tests for consuming the Sync API.
 */
public class SynchronizationTest extends AbsTestCase {
    public void testSynchronization() throws Exception {
        TestClientResult<CDASyncedSpace> result = new TestClientResult<CDASyncedSpace>();

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_initial.json"));

        // #1 - perform initial synchronization
        customClient.performInitialSynchronization(new TestCallback<CDASyncedSpace>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(3, result.value.items.size());

        CDAEntry entry = (CDAEntry) result.value.items.get(0);
        assertEquals("Yiltiquoar", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(9999), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));

        entry = (CDAEntry) result.value.items.get(1);
        assertEquals("Tzayclibbon", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(2405), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));

        entry = (CDAEntry) result.value.items.get(2);
        assertEquals("Za'ha'zah", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(2789), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));


        // #2 - get delta update
        CDASyncedSpace initialSyncResult = result.value;
        result = new TestClientResult<CDASyncedSpace>();

        customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_update.json"));

        customClient.performSynchronization(initialSyncResult, new TestCallback<CDASyncedSpace>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(3, result.value.items.size());

        entry = (CDAEntry) result.value.items.get(0);
        assertEquals("Ooctaiphus", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(2), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));

        entry = (CDAEntry) result.value.items.get(1);
        assertEquals("Yiltiquoar", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(666666), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));

        entry = (CDAEntry) result.value.items.get(2);
        assertEquals("Za'ha'zah", ((Map) entry.fieldsMap.get("name")).get(Constants.DEFAULT_LOCALE));
        assertEquals(Double.valueOf(2789), ((Map) entry.fieldsMap.get("age")).get(Constants.DEFAULT_LOCALE));

        // #3 - empty update
        CDASyncedSpace updatedSpace = result.value;

        result = new TestClientResult<CDASyncedSpace>();

        customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_update_empty.json"));


        customClient.performSynchronization(updatedSpace, new TestCallback<CDASyncedSpace>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(3, result.value.items.size());
    }
}
