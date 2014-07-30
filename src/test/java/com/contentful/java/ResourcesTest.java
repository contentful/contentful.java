package com.contentful.java;

import com.contentful.java.lib.Constants;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.model.*;
import retrofit.client.Response;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Set of tests for fetching Resources with types defined at runtime.
 */
public class ResourcesTest extends AbsTestCase {
    public void testFetchResourcesOfTypeAsset() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        client.fetchResourcesOfType(Constants.CDAResourceType.Asset, new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onSuccess(CDAListResult cdaListResult, Response response) {
                result[0] = cdaListResult;
                super.onSuccess(cdaListResult, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertTrue(result[0].total > 0);

        for (CDABaseItem item : result[0].items) {
            assertTrue(item instanceof CDAAsset);
            assertEquals(Constants.CDAResourceType.Asset.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeEntry() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        client.fetchResourcesOfType(Constants.CDAResourceType.Entry, new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onSuccess(CDAListResult cdaListResult, Response response) {
                result[0] = cdaListResult;
                super.onSuccess(cdaListResult, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertTrue(result[0].total > 0);

        for (CDABaseItem item : result[0].items) {
            assertTrue(item instanceof CDAEntry);
            assertEquals(Constants.CDAResourceType.Entry.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeContentType() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        client.fetchResourcesOfType(Constants.CDAResourceType.ContentType, new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onSuccess(CDAListResult cdaListResult, Response response) {
                result[0] = cdaListResult;
                super.onSuccess(cdaListResult, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertTrue(result[0].total > 0);

        for (CDABaseItem item : result[0].items) {
            assertTrue(item instanceof CDAContentType);
            assertEquals(Constants.CDAResourceType.ContentType.toString(), item.sys.type);
        }
    }

    public void testFetchResourcesOfTypeAssetMatching() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "happycat");

        client.fetchResourcesOfTypeMatching(Constants.CDAResourceType.Asset,
                query,
                new TestCallback<CDAListResult>(cdl) {
                    @Override
                    protected void onSuccess(CDAListResult cdaListResult, Response response) {
                        result[0] = cdaListResult;
                        super.onSuccess(cdaListResult, response);
                    }
                });

        cdl.await();

        assertNotNull(result[0]);
        assertEquals(1, result[0].total);
        assertEquals(1, result[0].items.size());

        CDAAsset asset = (CDAAsset) result[0].items.get(0);
        assertEquals(Constants.CDAResourceType.Asset.toString(), asset.sys.type);
        assertEquals("happycat", asset.sys.id);
        assertEquals("Happy Cat", asset.fields.title);
    }

    public void testFetchResourcesOfTypeEntryMatching() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        client.fetchResourcesOfTypeMatching(Constants.CDAResourceType.Entry,
                query,
                new TestCallback<CDAListResult>(cdl) {
                    @Override
                    protected void onSuccess(CDAListResult cdaListResult, Response response) {
                        result[0] = cdaListResult;
                        super.onSuccess(cdaListResult, response);
                    }
                });

        cdl.await();

        assertNotNull(result[0]);
        assertEquals(1, result[0].total);
        assertEquals(1, result[0].items.size());

        CDAEntry entry = (CDAEntry) result[0].items.get(0);
        assertEquals(Constants.CDAResourceType.Entry.toString(), entry.sys.type);
        assertEquals("nyancat", entry.sys.id);
        assertEquals("Nyan Cat", entry.fieldsMap.get("name"));
    }
}
