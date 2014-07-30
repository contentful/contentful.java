package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.model.CDAClient;
import com.contentful.java.model.CDAListResult;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tomxor on 30/07/14.
 */
public class ClientTest extends AbsTestCase {
    private RetrofitError retrofitError;

    public void testClientProvider() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = {null};

        CDAClient client = new CDAClient.Builder()
                .setSpaceKey("NON EXISTING")
                .setAccessToken("NON EXISTING")
                .setClient(new MockClient("result_test_client_provider.json"))
                .build();

        client.registerCustomClass("cat", NyanCat.class);

        client.fetchEntries(new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onSuccess(CDAListResult cdaListResult, Response response) {
                result[0] = cdaListResult;
                super.onSuccess(cdaListResult, response);
            }
        });

        cdl.await();

        assertTrue(result[0].getItems().size() == 1);
        NyanCat cat = (NyanCat) result[0].getItems().get(0);

        assertEquals("Nyan Cat", cat.fields.name);
        assertEquals(2, cat.fields.likes.size());
        assertEquals("rainbows", cat.fields.likes.get(0));
        assertEquals("fish", cat.fields.likes.get(1));
        assertEquals("rainbow", cat.fields.color);
        assertEquals(new Date(1301954400000L), cat.fields.birthday);
        assertEquals(1337, (int) cat.fields.lives);

        NyanCat bestFriend = cat.fields.bestFriend;
        assertTrue(cat == bestFriend.fields.bestFriend);
    }
}
