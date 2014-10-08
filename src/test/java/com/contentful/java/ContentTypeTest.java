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
import com.contentful.java.model.CDAContentType;
import com.contentful.java.model.CDAResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for fetching Content Type resources.
 */
public class ContentTypeTest extends AbsTestCase {
  @Test public void testFetchContentTypes() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_content_types.json"))
        .build();

    client.fetchContentTypes(callback);
    callback.await();
    verifyResultNotEmpty(callback);
    verifyContentTypes(callback.value);
  }

  @Test public void testFetchContentTypesBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_content_types.json"))
        .build();

    CDAArray result = client.fetchContentTypesBlocking();
    verifyContentTypes(result);
  }

  @Test public void testFetchContentTypeWithIdentifier() throws Exception {
    TestCallback<CDAContentType> callback = new TestCallback<CDAContentType>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_content_type_with_id.json"))
        .build();

    client.fetchContentTypeWithIdentifier("MOCK", callback);
    callback.await();
    verifyResultNotEmpty(callback);
    verifyContentTypeWithIdentifier(callback.value);
  }

  @Test public void testFetchContentTypeWithIdentifierBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_content_type_with_id.json"))
        .build();

    client.fetchContentTypeWithIdentifierBlocking("MOCK");
  }

  @Test public void testNoDisplayField() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_no_display_field.json"))
        .build();

    client.fetchContentTypesBlocking();
  }

  void verifyContentTypes(CDAArray result) {
    assertNotNull(result);

    assertEquals(2, result.getTotal());
    assertEquals(0, result.getSkip());
    assertEquals(100, result.getLimit());

    ArrayList<CDAResource> items = result.getItems();
    assertEquals(2, items.size());

    // 1st item (City)
    CDAContentType item = (CDAContentType) items.get(0);
    assertEquals("City", item.getName());
    assertNull(item.getUserDescription());
    assertEquals("name", item.getDisplayField());
    assertEquals("1t9IbcfdCk6m04uISSsaIK", item.getSys().get("id"));

    // field #1
    List<Map> fields = item.getFields();
    Map field = fields.get(0);
    assertEquals("Name", field.get("name"));
    assertEquals("name", field.get("id"));
    assertEquals("Text", field.get("type"));
    assertTrue((Boolean) field.get("required"));

    // field #2
    field = fields.get(1);
    assertEquals("Center", field.get("name"));
    assertEquals("center", field.get("id"));
    assertEquals("Location", field.get("type"));
    assertTrue((Boolean) field.get("required"));

    // 2nd item (Cat)
    item = (CDAContentType) items.get(1);
    assertEquals("Cat", item.getName());
    assertEquals("Meow!", item.getUserDescription());
    assertEquals("name", item.getDisplayField());
    assertEquals("63k4qdEi9aI8IQUGaYGg4O", item.getSys().get("id"));

    // field #1
    fields = item.getFields();
    field = fields.get(0);
    assertEquals("name", field.get("id"));
    assertEquals("Name", field.get("name"));
    assertEquals("Text", field.get("type"));
    assertTrue((Boolean) field.get("required"));
    assertFalse((Boolean) field.get("localized"));
  }

  void verifyContentTypeWithIdentifier(CDAContentType result) {
    assertNotNull(result);

    assertEquals("Cat", result.getName());
    assertEquals("name", result.getDisplayField());
    assertEquals("Meow.", result.getUserDescription());
    assertEquals("cat", result.getSys().get("id"));

    List<Map> fields = result.getFields();
    assertEquals(8, fields.size());

    assertEquals("name", fields.get(0).get("id"));
    assertEquals("likes", fields.get(1).get("id"));
    assertEquals("color", fields.get(2).get("id"));
    assertEquals("bestFriend", fields.get(3).get("id"));
    assertEquals("birthday", fields.get(4).get("id"));
    assertEquals("lifes", fields.get(5).get("id"));
    assertEquals("lives", fields.get(6).get("id"));
    assertEquals("image", fields.get(7).get("id"));
  }
}
