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
import com.contentful.java.cda.lib.TestClientFactory;
import com.contentful.java.cda.model.CDAArray;
import java.util.HashMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Paging tests.
 */
public class PagingTest extends AbsTestCase {
  @Test public void testArrayPaging() throws Exception {
    // first request
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_paging_1.json"))
        .build();

    HashMap<String, String> query = new HashMap<String, String>();
    query.put("limit", "6");
    CDAArray firstPage = client.fetchEntriesMatchingBlocking(query);

    // second request
    client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_paging_2.json"))
        .build();

    CDAArray secondPage = client.fetchArrayNextPageBlocking(firstPage);
    assertNotNull(secondPage);
    assertEquals(6, secondPage.getSkip());
    assertEquals(5, secondPage.getItems().size());

    // third request
    client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_paging_3.json"))
        .build();

    CDAArray thirdPage = client.fetchArrayNextPageBlocking(secondPage);
    assertEquals(0, thirdPage.getItems().size());
  }
}
