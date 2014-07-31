package com.contentful.java;

import com.contentful.java.lib.Constants;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientResult;
import com.contentful.java.model.*;

import java.util.HashMap;

/**
 * Set of tests for fetching Resources with types defined at runtime.
 */
public class ResourcesTest extends AbsTestCase {
    public void testFetchResourcesOfTypeAsset() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchResourcesOfType(Constants.CDAResourceType.Asset, new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertTrue(result.value.total > 0);

        for (CDABaseItem item : result.value.items) {
            assertTrue(item instanceof CDAAsset);
            assertEquals(Constants.CDAResourceType.Asset.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeEntry() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchResourcesOfType(Constants.CDAResourceType.Entry, new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertTrue(result.value.total > 0);

        for (CDABaseItem item : result.value.items) {
            assertTrue(item instanceof CDAEntry);
            assertEquals(Constants.CDAResourceType.Entry.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeContentType() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchResourcesOfType(Constants.CDAResourceType.ContentType,
                new TestCallback<CDAListResult>(result));

        result.cdl.await();
        verifyResultNotEmpty(result);

        assertTrue(result.value.total > 0);

        for (CDABaseItem item : result.value.items) {
            assertTrue(item instanceof CDAContentType);
            assertEquals(Constants.CDAResourceType.ContentType.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeAssetMatching() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "happycat");

        client.fetchResourcesOfTypeMatching(Constants.CDAResourceType.Asset,
                query,
                new TestCallback<CDAListResult>(result));

        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(1, result.value.total);
        assertEquals(1, result.value.items.size());

        CDAAsset asset = (CDAAsset) result.value.items.get(0);
        assertEquals(Constants.CDAResourceType.Asset.toString(), asset.sys.type);
        assertEquals("happycat", asset.sys.id);
        assertEquals("Happy Cat", asset.fields.title.getValue());
    }

    public void testFetchResourcesOfTypeEntryMatching() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        client.fetchResourcesOfTypeMatching(Constants.CDAResourceType.Entry,
                query,
                new TestCallback<CDAListResult>(result));

        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(1, result.value.total);
        assertEquals(1, result.value.items.size());

        CDAEntry entry = (CDAEntry) result.value.items.get(0);
        assertEquals(Constants.CDAResourceType.Entry.toString(), entry.sys.type);
        assertEquals("nyancat", entry.sys.id);
        assertEquals("Nyan Cat", entry.fieldsMap.get("name"));
    }
}
