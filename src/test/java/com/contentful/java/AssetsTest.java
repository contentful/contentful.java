package com.contentful.java;

import com.contentful.java.lib.TestCallback;
import retrofit.RetrofitError;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Test of all Entries fetching methods via {@link CDAClient}.
 */
public class AssetsTest extends AbsTestCase {
    private RetrofitError retrofitError;

    public void testFetchAssets() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        client.fetchAssets(new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                AssetsTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }

    public void testFetchAssetsMatching() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        HashMap<String, String> query = new HashMap<String, String>();

        client.fetchAssetsMatching(query, new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                AssetsTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }

    public void testFetchAssetWithIdentifier() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        client.fetchAssetWithIdentifier("nyancat", new TestCallback<CDAAsset>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                AssetsTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }
}
