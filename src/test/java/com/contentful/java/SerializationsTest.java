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
import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.ResourceUtils;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAResource;
import com.contentful.java.model.CDASpace;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Serialization and persistence tests.
 */
public class SerializationsTest extends AbsTestCase {
  @Test public void testSingleResource() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_asset_with_identifier.json"))
        .build();

    CDAAsset asset = client.fetchAssetWithIdentifierBlocking("");
    assertNotNull(asset);

    File f = new File("asset.dat");
    f.deleteOnExit();

    ResourceUtils.saveResourceToFile(asset, f);
    asset = (CDAAsset) ResourceUtils.readResourceFromFile(f);
    assertNotNull(asset);

    Map fields = asset.getFields();
    assertNotNull(fields);

    assertEquals("fake", fields.get("title"));
    assertEquals("https://images.contentful.com/fake.png", asset.getUrl());
    assertTrue(asset.getSys().get("space") instanceof CDASpace);
  }

  @Test public void testArray() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_fetch_entries_with_includes.json"))
        .build();

    client.registerCustomClass("cat", NyanCat.class);

    CDAArray array = client.fetchEntriesBlocking();

    File f = new File("array.dat");
    f.deleteOnExit();

    ResourceUtils.saveResourceToFile(array, f);
    array = (CDAArray) ResourceUtils.readResourceFromFile(f);
    assertNotNull(array);

    ArrayList<CDAResource> items = array.getItems();
    assertEquals(11, items.size());

    CDAResource res = items.get(2);
    assertTrue(res instanceof NyanCat);
    NyanCat cat = (NyanCat) res;
    assertTrue(cat.getBestFriend().getBestFriend() == cat);

    res = items.get(5);
    assertTrue(res instanceof NyanCat);
    cat = (NyanCat) res;
    assertTrue(cat.getBestFriend().getBestFriend() == cat);
  }
}
