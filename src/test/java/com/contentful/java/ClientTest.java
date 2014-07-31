package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAResource;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Client tests.
 */
public class ClientTest extends AbsTestCase {
    @Test
    public void testClientProvider() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_client_provider.json"));

        client.registerCustomClass("cat", NyanCat.class);

        client.fetchEntries(callback);
        callback.await();
        verifyResultNotEmpty(callback);

        ArrayList<CDAResource> items = callback.value.getItems();

        assertTrue(items.size() == 1);
        NyanCat cat = (NyanCat) items.get(0);

        EntriesTest.verifyNyanCatEntryWithClass(cat);
        assertTrue(cat.getBestFriend().getBestFriend() == cat);
    }
}
