package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.lib.TestClientResult;
import com.contentful.java.model.CDABaseItem;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAListResult;

import java.util.Date;
import java.util.HashMap;

/**
 * Test of all Entries fetching methods via {@link CDAClient}.
 */
public class EntriesTest extends AbsTestCase {
    public void testFetchEntries() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchEntries(new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }

    public void testFetchEntriesMatching() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        client.fetchEntriesMatching(query, new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }

    public void testFetchEntryWithIdentifier() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        client.fetchEntryWithIdentifier("nyancat", new TestCallback<CDAEntry>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);
    }

    public void testFetchEntryOfCustomClass() throws Exception {
        TestClientResult<NyanCat> result = new TestClientResult<NyanCat>();

        // use a new client instance
        CDAClient customClient = TestClientFactory.newInstance();

        // register custom class
        customClient.registerCustomClass("cat", NyanCat.class);

        customClient.fetchEntryWithIdentifier("nyancat", new TestCallback<NyanCat>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        // name
        assertTrue("Nyan Cat".equals(result.value.fields.name));

        // color
        assertTrue("rainbow".equals(result.value.fields.color));

        // lives
        assertTrue(Integer.valueOf(1337).equals(result.value.fields.lives));

        // likes
        assertNotNull(result.value.fields.likes);
        assertTrue(result.value.fields.likes.size() == 2);
        assertEquals("rainbows", result.value.fields.likes.get(0).getValue());
        assertEquals("fish", result.value.fields.likes.get(1).getValue());

        // birthday
        assertEquals(new Date(1301954400000L), result.value.fields.birthday);
    }

    public void testFetchEntriesWithLinks() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        // use a new client instance
        CDAClient customClient = TestClientFactory.newInstance();

        // register custom class
        customClient.registerCustomClass("cat", NyanCat.class);

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");
        query.put("include", "1");

        customClient.fetchEntriesMatching(query, new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertTrue(result.value.items.size() > 0);

        CDABaseItem item = result.value.items.get(0);
        assertTrue(item instanceof NyanCat);

        NyanCat cat = (NyanCat) item;
        assertNotNull(cat.fields.bestFriend);

        NyanCat bestFriend = cat.fields.bestFriend;
        assertTrue(bestFriend.fields.bestFriend == cat);
    }
}
