package com.contentful.java;

import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientResult;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAListResult;

import java.util.HashMap;

/**
 * Test of all Entries fetching methods via {@link com.contentful.java.api.CDAClient}.
 */
public class AssetsTest extends AbsTestCase {
    public void testFetchAssets() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchAssets(new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }

    public void testFetchAssetsMatching() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        HashMap<String, String> query = new HashMap<String, String>();
        client.fetchAssetsMatching(query, new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }

    public void testFetchAssetWithIdentifier() throws Exception {
        TestClientResult<CDAAsset> result = new TestClientResult<CDAAsset>();

        client.fetchAssetWithIdentifier("nyancat", new TestCallback<CDAAsset>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }
}
