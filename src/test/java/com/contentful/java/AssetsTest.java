package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests for fetching Asset resources.
 */
public class AssetsTest extends AbsTestCase {
    @Test
    public void testFetchAssets() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_assets.json"));

        client.fetchAssets(callback);

        callback.await();
        verifyResultNotEmpty(callback);

        CDAArray result = callback.value;
        ArrayList<CDAResource> items = result.getItems();

        assertEquals(2, items.size());

        CDAAsset item = (CDAAsset) items.get(0);
        assertEquals("https://test.url.com/file_1.png", item.getUrl());
        assertEquals("image/png", item.getMimeType());

        item = (CDAAsset) items.get(1);
        assertEquals("https://test.url.com/file_2.png", item.getUrl());
        assertEquals("image/png", item.getMimeType());
    }

    @Test
    public void testFetchAssetsMatching() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "jake");

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_assets_matching.json"));

        client.fetchAssetsMatching(query, callback);
        callback.await();

        verifyResultNotEmpty(callback);

        CDAArray result = callback.value;
        ArrayList<CDAResource> items = result.getItems();

        assertEquals(1, items.size());

        CDAAsset asset = (CDAAsset) items.get(0);

        assertEquals(
                "https://images.contentful.com/cfexampleapi/4hlteQAXS8iS0YCMU6QMWg/2a4d826144f014109364ccf5c891d2dd/jake.png",
                asset.getUrl());

        assertEquals("image/png", asset.getMimeType());
    }

    @Test
    public void testFetchAssetWithIdentifier() throws Exception {
        TestCallback<CDAAsset> callback = new TestCallback<CDAAsset>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_asset_with_identifier.json"));

        client.fetchAssetWithIdentifier("fake", callback);

        callback.await();
        verifyResultNotEmpty(callback);

        CDAAsset asset = callback.value;

        assertEquals("https://images.contentful.com/fake.png", asset.getUrl());
        assertEquals("image/png", asset.getMimeType());
    }
}
