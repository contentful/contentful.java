package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test Entries.
 */
public class EntriesTest extends AbsTestCase {
    @Test
    public void testFetchEntries() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_entries.json"));

        client.fetchEntries(callback);

        callback.await();
        verifyResultNotEmpty(callback);

        CDAArray result = callback.value;
        ArrayList<CDAResource> items = result.getItems();

        assertEquals(11, items.size());
    }

    @Test
    public void testFetchEntriesMatching() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_entries_matching.json"));

        client.fetchEntriesMatching(query, callback);
        callback.await();

        verifyResultNotEmpty(callback);

        CDAArray result = callback.value;
        ArrayList<CDAResource> items = result.getItems();

        assertEquals(1, items.size());

        Object item = items.get(0);
        assertTrue(item instanceof CDAEntry);
        verifyNyanCatEntry((CDAEntry) item);
    }

    @Test
    public void testFetchEntryWithIdentifier() throws Exception {
        TestCallback<CDAEntry> callback = new TestCallback<CDAEntry>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_entry_nyancat.json"));

        client.fetchEntryWithIdentifier("nyancat", callback);
        callback.await();

        verifyResultNotEmpty(callback);
        verifyNyanCatEntry(callback.value);
    }

    @Test
    public void testFetchEntryOfCustomClass() throws Exception {
        TestCallback<NyanCat> callback = new TestCallback<NyanCat>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_entry_nyancat.json"));

        client.registerCustomClass("cat", NyanCat.class);

        client.fetchEntryWithIdentifier("nyancat", callback);

        callback.await();
        verifyResultNotEmpty(callback);

        verifyNyanCatEntryWithClass(callback.value);
    }

    @Test
    public void testFetchEntriesWithLinks() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        // use a new client instance
        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_fetch_entries_with_includes.json"));

        // register custom class
        client.registerCustomClass("cat", NyanCat.class);

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");
        query.put("include", "1");

        client.fetchEntriesMatching(query, callback);
        callback.await();
        verifyResultNotEmpty(callback);

        ArrayList<CDAResource> items = callback.value.getItems();
        assertTrue(items.size() == 11);

        NyanCat cat = (NyanCat) items.get(2);
        NyanCat bestFriend = cat.getBestFriend();

        assertNotNull(bestFriend);
        assertTrue(bestFriend.getBestFriend() == cat);

        CDAEntry jake = (CDAEntry) items.get(9);

        Object value = jake.getFields().get("image");
        assertNotNull(value);
        assertTrue(value instanceof CDAAsset);

        assertEquals(
                "https://images.contentful.com/cfexampleapi/4hlteQAXS8iS0YCMU6QMWg/2a4d826144f014109364ccf5c891d2dd/jake.png",
                ((CDAAsset) value).getUrl());

        assertEquals("image/png", ((CDAAsset) value).getMimeType());
    }

    private void verifyNyanCatEntry(CDAEntry entry) {
        // name
        assertTrue("Nyan Cat".equals(entry.getFields().get("name")));

        // color
        assertTrue("rainbow".equals(entry.getFields().get("color")));

        // lives
        assertTrue(Double.valueOf(1337).equals(entry.getFields().get("lives")));

        // likes
        List<String> likes = (List<String>) entry.getFields().get("likes");
        assertNotNull(likes);
        assertTrue(likes.size() == 2);
        assertEquals("rainbows", likes.get(0));
        assertEquals("fish", likes.get(1));

        // birthday
        assertEquals("2011-04-04T22:00:00+00:00", entry.getFields().get("birthday"));
    }

    public static void verifyNyanCatEntryWithClass(NyanCat cat) {
        // name
        assertTrue("Nyan Cat".equals(cat.getName()));

        // color
        assertTrue("rainbow".equals(cat.getColor()));

        // lives
        assertTrue(Integer.valueOf(1337).equals(cat.getLives()));

        // likes
        List<String> likes = cat.getLikes();
        assertNotNull(likes);
        assertTrue(likes.size() == 2);
        assertEquals("rainbows", likes.get(0));
        assertEquals("fish", likes.get(1));

        // birthday
        assertEquals("2011-04-04T22:00:00+00:00", cat.getBirthday());
    }
}
