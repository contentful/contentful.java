package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAResource;
import com.contentful.java.model.CDASyncedSpace;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests for consuming the Sync API.
 */
public class SynchronizationTest extends AbsTestCase {
    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testSynchronization() throws Exception {
        TestCallback<CDASyncedSpace> callback = new TestCallback<CDASyncedSpace>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_initial.json"));

        // #1 - perform initial synchronization
        client.performInitialSynchronization(callback);
        callback.await();
        verifyResultNotEmpty(callback);

        ArrayList<CDAResource> items = callback.value.getItems();
        assertEquals(3, items.size());


        CDAEntry entry = (CDAEntry) items.get(0);
        assertEquals("Yiltiquoar", entry.getFields().get("name"));
        assertEquals(Double.valueOf(9999), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(1);
        assertEquals("Tzayclibbon", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2405), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(2);
        assertEquals("Za'ha'zah", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2789), entry.getFields().get("age"));

        // #2 - get delta update
        CDASyncedSpace initialSyncResult = callback.value;
        callback = new TestCallback<CDASyncedSpace>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_update.json"));

        client.performSynchronization(initialSyncResult, callback);
        callback.await();
        verifyResultNotEmpty(callback);

        items = callback.value.getItems();
        assertEquals(3, items.size());

        entry = (CDAEntry) items.get(0);
        assertEquals("Ooctaiphus", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(1);
        assertEquals("Yiltiquoar", entry.getFields().get("name"));
        assertEquals(Double.valueOf(666666), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(2);
        assertEquals("Za'ha'zah", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2789), entry.getFields().get("age"));

        // #3 - empty update
        CDASyncedSpace updatedSpace = callback.value;

        callback = new TestCallback<CDASyncedSpace>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_sync_update_empty.json"));

        client.performSynchronization(updatedSpace, callback);
        callback.await();
        verifyResultNotEmpty(callback);

        assertEquals(3, callback.value.getItems().size());
    }
}
