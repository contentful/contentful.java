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

package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAResource;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for fetching Asset resources.
 */
public class AssetsTest extends AbsTestCase {
  @Test public void testFetchAssets() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_assets.json"))
        .build();

    client.fetchAssets(callback);

    callback.await();
    verifyResultNotEmpty(callback);
    verifyAssets(callback.value);
  }

  @Test public void testFetchAssetsBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_assets.json"))
        .build();

    CDAArray result = client.fetchAssetsBlocking();
    verifyAssets(result);
  }

  @Test public void testFetchAssetsMatching() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    HashMap<String, String> query = new HashMap<String, String>();
    query.put("sys.id", "jake");

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_assets_matching.json"))
        .build();

    client.fetchAssetsMatching(query, callback);
    callback.await();

    verifyResultNotEmpty(callback);
    verifyAssetsMatching(callback.value);
  }

  @Test public void testFetchAssetsMatchingBlocking() throws Exception {
    HashMap<String, String> query = new HashMap<String, String>();
    query.put("sys.id", "jake");

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_assets_matching.json"))
        .build();

    CDAArray result = client.fetchAssetsMatchingBlocking(query);
    verifyAssetsMatching(result);
  }

  @Test public void testFetchAssetWithIdentifier() throws Exception {
    TestCallback<CDAAsset> callback = new TestCallback<CDAAsset>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_asset_with_identifier.json"))
        .build();

    client.fetchAssetWithIdentifier("fake", callback);

    callback.await();
    verifyResultNotEmpty(callback);
    verifyAssetWithIdentifier(callback.value);
  }

  @Test public void testFetchAssetWithIdentifierBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_asset_with_identifier.json"))
        .build();

    CDAAsset result = client.fetchAssetWithIdentifierBlocking("fake");
    verifyAssetWithIdentifier(result);
  }

  void verifyAssets(CDAArray result) {
    assertNotNull(result);
    ArrayList<CDAResource> items = result.getItems();

    assertEquals(2, items.size());

    CDAAsset item = (CDAAsset) items.get(0);
    assertEquals("https://test.url.com/file_1.png", item.getUrl());
    assertEquals("image/png", item.getMimeType());

    item = (CDAAsset) items.get(1);
    assertEquals("https://test.url.com/file_2.png", item.getUrl());
    assertEquals("image/png", item.getMimeType());
  }

  void verifyAssetsMatching(CDAArray result) {
    assertNotNull(result);
    ArrayList<CDAResource> items = result.getItems();

    assertEquals(1, items.size());

    CDAAsset asset = (CDAAsset) items.get(0);

    assertEquals(
        "https://images.contentful.com/cfexampleapi/4hlteQAXS8iS0YCMU6QMWg/2a4d826144f014109364ccf5c891d2dd/jake.png",
        asset.getUrl());

    assertEquals("image/png", asset.getMimeType());
  }

  void verifyAssetWithIdentifier(CDAAsset result) {
    assertNotNull(result);

    assertEquals("https://images.contentful.com/fake.png", result.getUrl());
    assertEquals("image/png", result.getMimeType());
  }
}
