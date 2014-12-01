/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda;

import com.contentful.java.cda.lib.MockClient;
import com.contentful.java.cda.lib.NyanCat;
import com.contentful.java.cda.lib.TestCallback;
import com.contentful.java.cda.lib.TestClientFactory;
import com.contentful.java.cda.model.CDAArray;
import com.contentful.java.cda.model.CDAAsset;
import com.contentful.java.cda.model.CDAEntry;
import com.contentful.java.cda.model.CDAResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for fetching Entry resources.
 */
public class EntriesTest extends AbsTestCase {
  @Test public void testFetchEntries() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries.json"))
        .build();

    client.fetchEntries(callback);

    callback.await();
    verifyResultNotEmpty(callback);
    verifyEntries(callback.value);
  }

  @Test public void testFetchEntriesBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries.json"))
        .build();

    CDAArray result = client.fetchEntriesBlocking();
    verifyEntries(result);
  }

  @Test public void testFetchEntriesMatching() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    HashMap<String, String> query = new HashMap<String, String>();
    query.put("sys.id", "nyancat");

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries_matching.json"))
        .build();

    client.fetchEntriesMatching(query, callback);
    callback.await();

    verifyResultNotEmpty(callback);
    verifyEntriesMatching(callback.value);
  }

  @Test public void testEntriesMatchingBlocking() throws Exception {
    HashMap<String, String> query = new HashMap<String, String>();
    query.put("sys.id", "nyancat");

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries_matching.json"))
        .build();

    CDAArray result = client.fetchEntriesMatchingBlocking(query);
    verifyEntriesMatching(result);
  }

  @Test public void testFetchEntryWithIdentifier() throws Exception {
    TestCallback<CDAEntry> callback = new TestCallback<CDAEntry>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entry_nyancat.json"))
        .build();

    client.fetchEntryWithIdentifier("nyancat", callback);
    callback.await();

    verifyResultNotEmpty(callback);
    verifyNyanCatEntry(callback.value);
  }

  @Test public void testFetchEntryWithIdentifierBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entry_nyancat.json"))
        .build();

    CDAEntry result = client.fetchEntryWithIdentifierBlocking("nyancat");
    verifyNyanCatEntry(result);
  }

  @Test public void testFetchEntryOfCustomClass() throws Exception {
    TestCallback<NyanCat> callback = new TestCallback<NyanCat>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entry_nyancat.json"))
        .build();

    client.registerCustomClass("cat", NyanCat.class);

    client.fetchEntryWithIdentifier("nyancat", callback);

    callback.await();
    verifyResultNotEmpty(callback);

    verifyNyanCatEntryWithClass(callback.value);
  }

  @Test public void testFetchEntryOfCustomClassBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entry_nyancat.json"))
        .build();

    client.registerCustomClass("cat", NyanCat.class);

    CDAEntry result = client.fetchEntryWithIdentifierBlocking("nyancat");
    verifyNyanCatEntryWithClass(result);
  }

  @Test public void testFetchEntriesWithLinks() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    // use a new client instance
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries_with_includes.json"))
        .build();

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

  @Test public void testFetchEntriesWithNestedLinks() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries_with_nested_links.json"))
        .build();

    CDAArray result = client.fetchEntriesBlocking();
    assertNotNull(result);

    ArrayList<CDAResource> items = result.getItems();
    assertEquals(1, items.size());

    CDAEntry entry = (CDAEntry) items.get(0);
    ArrayList pictures = (ArrayList) entry.getFields().get("pictures");
    assertTrue(pictures.get(0) instanceof CDAAsset);
  }

  void verifyEntries(CDAArray result) {
    assertNotNull(result);
    assertEquals(11, result.getItems().size());
  }

  void verifyEntriesMatching(CDAArray result) {
    assertNotNull(result);

    ArrayList<CDAResource> items = result.getItems();

    assertEquals(1, items.size());

    Object item = items.get(0);
    assertTrue(item instanceof CDAEntry);
    verifyNyanCatEntry((CDAEntry) item);
  }

  @SuppressWarnings("unchecked")
  void verifyNyanCatEntry(CDAEntry entry) {
    assertNotNull(entry);

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

  static void verifyNyanCatEntryWithClass(CDAEntry entry) {
    assertNotNull(entry);
    assertTrue(entry instanceof NyanCat);

    NyanCat cat = (NyanCat) entry;

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
