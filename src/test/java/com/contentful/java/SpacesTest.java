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
import com.contentful.java.lib.Constants;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDASpace;
import com.contentful.java.model.Locale;
import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for fetching Space resources.
 */
public class SpacesTest extends AbsTestCase {
  @Test public void testFetchSpace() throws Exception {
    TestCallback<CDASpace> callback = new TestCallback<CDASpace>();

    TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_space.json"))
        .build()
        .fetchSpace(callback);

    callback.await();

    verifyResultNotEmpty(callback);
    verifySpace(callback.value);
  }

  @Test public void testFetchSpaceBlocking() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_fetch_space.json"))
        .build();

    CDASpace result = client.fetchSpaceBlocking();
    verifySpace(result);
  }

  void verifySpace(CDASpace space) {
    assertNotNull(space);
    assertEquals("Contentful Example API", space.getName());
    assertEquals("Space", space.getSys().get("type"));
    assertEquals("cfexampleapi", space.getSys().get("id"));

    ArrayList<Locale> locales = space.getLocales();
    assertEquals(2, locales.size());

    // English
    Locale locale = locales.get(0);
    assertEquals(Constants.DEFAULT_LOCALE, locale.code);
    assertEquals("English", locale.name);
    assertTrue(locale.isDefault);

    // Klingon
    locale = locales.get(1);
    assertEquals("tlh", locale.code);
    assertEquals("Klingon", locale.name);
    assertFalse(locale.isDefault);
  }
}
