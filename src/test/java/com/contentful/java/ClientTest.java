package com.contentful.java;

import com.contentful.java.lib.*;
import com.contentful.java.model.CDAListResult;

import java.util.Date;

/**
 * Created by tomxor on 30/07/14.
 */
public class ClientTest extends AbsTestCase {
    public void testClientProvider() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_client_provider.json"));

        client.registerCustomClass("cat", NyanCat.class);

        client.fetchEntries(new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertTrue(result.value.items.size() == 1);
        NyanCat cat = (NyanCat) result.value.items.get(0);

        assertEquals("Nyan Cat", cat.fields.name);
        assertEquals(2, cat.fields.likes.size());
        assertEquals("rainbows", cat.fields.likes.get(0).getValue());
        assertEquals("fish", cat.fields.likes.get(1).getValue());
        assertEquals("rainbow", cat.fields.color);
        assertEquals(new Date(1301954400000L), cat.fields.birthday);
        assertEquals(1337, (int) cat.fields.lives);

        NyanCat bestFriend = cat.fields.bestFriend;
        assertTrue(cat == bestFriend.fields.bestFriend);
    }
}
