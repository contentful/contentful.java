package com.contentful.java;

import com.contentful.java.lib.Constants;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for fetching resources of various types.
 */
public class ResourcesTest extends AbsTestCase {
    @Test
    public void testFetchResourcesOfTypeAsset() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        TestClientFactory
                .newInstance()
                .build()
                .fetchResourcesOfType(Constants.CDAResourceType.Asset, callback);

        callback.await();
        verifyResultNotEmpty(callback);

        assertTrue(callback.value.getTotal() > 0);

        for (CDAResource item : callback.value.getItems()) {
            assertTrue(item instanceof CDAAsset);
            assertEquals(Constants.CDAResourceType.Asset.toString(), item.getSys().get("type"));
        }
    }

    @Test
    public void testFetchResourcesOfTypeEntry() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        TestClientFactory
                .newInstance()
                .build()
                .fetchResourcesOfType(Constants.CDAResourceType.Entry, callback);

        callback.await();
        verifyResultNotEmpty(callback);

        assertTrue(callback.value.getTotal() > 0);

        for (CDAResource item : callback.value.getItems()) {
            assertTrue(item instanceof CDAEntry);
            assertEquals(Constants.CDAResourceType.Entry.toString(), item.getSys().get("type"));
        }
    }

    @Test
    public void testFetchResourcesOfTypeContentType() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        TestClientFactory
                .newInstance()
                .build()
                .fetchResourcesOfType(Constants.CDAResourceType.ContentType, callback);

        callback.await();
        verifyResultNotEmpty(callback);

        assertTrue(callback.value.getTotal() > 0);

        for (CDAResource item : callback.value.getItems()) {
            assertTrue(item instanceof CDAContentType);

            assertEquals(Constants.CDAResourceType.ContentType.toString(),
                    item.getSys().get("type"));
        }
    }

    @Test
    public void testFetchResourcesOfTypeAssetMatching() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "happycat");

        TestClientFactory
                .newInstance()
                .build()
                .fetchResourcesOfTypeMatching(Constants.CDAResourceType.Asset, query, callback);

        callback.await();
        verifyResultNotEmpty(callback);

        assertEquals(1, callback.value.getTotal());

        ArrayList<CDAResource> items = callback.value.getItems();
        assertEquals(1, items.size());

        CDAAsset asset = (CDAAsset) items.get(0);
        assertEquals(Constants.CDAResourceType.Asset.toString(), asset.getSys().get("type"));
        assertEquals("happycat", asset.getSys().get("id"));
        assertEquals("Happy Cat", asset.getFields().get("title"));
    }

    @Test
    public void testFetchResourcesOfTypeEntryMatching() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        TestClientFactory
                .newInstance()
                .build()
                .fetchResourcesOfTypeMatching(Constants.CDAResourceType.Entry, query, callback);

        callback.await();
        verifyResultNotEmpty(callback);

        assertEquals(1, callback.value.getTotal());

        ArrayList<CDAResource> items = callback.value.getItems();
        assertEquals(1, items.size());

        CDAEntry entry = (CDAEntry) items.get(0);
        assertEquals(Constants.CDAResourceType.Entry.toString(), entry.getSys().get("type"));
        assertEquals("nyancat", entry.getSys().get("id"));
        assertEquals("Nyan Cat", entry.getFields().get("name"));
    }
}
