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

import com.contentful.java.cda.lib.Constants;
import com.contentful.java.cda.lib.MockClient;
import com.contentful.java.cda.lib.NyanCat;
import com.contentful.java.cda.lib.TestCallback;
import com.contentful.java.cda.lib.TestClientFactory;
import com.contentful.java.cda.lib.TestException;
import com.contentful.java.cda.model.CDAArray;
import com.contentful.java.cda.model.CDAResource;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static org.junit.Assert.assertTrue;

/**
 * Tests using custom clients and client providers.
 */
public class ClientTest extends AbsTestCase {
  @Test public void testClientProvider() throws Exception {
    TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

    CDAClient client = TestClientFactory.newInstance()
        .setClient(new MockClient("result_test_client_provider.json"))
        .build();

    client.registerCustomClass("cat", NyanCat.class);

    client.fetchEntries(callback);
    callback.await();
    verifyResultNotEmpty(callback);

    ArrayList<CDAResource> items = callback.value.getItems();

    assertTrue(items.size() == 1);
    NyanCat cat = (NyanCat) items.get(0);

    EntriesTest.verifyNyanCatEntryWithClass(cat);
    assertTrue(cat.getBestFriend().getBestFriend() == cat);
  }

  @Test(expected = TestException.class) public void testCustomErrorHandler() throws Exception {
    TestClientFactory.newInstance().setClient(new Client() {
      @Override public Response execute(Request request) throws IOException {
        throw new RuntimeException();
      }
    }).setErrorHandler(new ErrorHandler() {
      @Override public Throwable handleError(RetrofitError retrofitError) {
        return new TestException();
      }
    }).build().fetchSpaceBlocking();
  }

  @Test(expected = RetrofitError.class) public void testSynchronousException() throws Exception {
    CDAClient client =
        TestClientFactory.newInstance().setAccessToken("error").setSpaceKey("error").build();

    client.fetchEntriesBlocking();
  }

  @SuppressWarnings("unchecked") @Test public void testNoSSL() throws Exception {
    final Boolean[] res = new Boolean[] { null };

    CDAClient client = TestClientFactory.newInstance().noSSL().setClient(new Client() {
      @Override public Response execute(Request request) throws IOException {
        URI uri = URI.create(request.getUrl());
        res[0] = Constants.SCHEME_HTTP.equalsIgnoreCase(uri.getScheme());

        return new Response(request.getUrl(), 200, "OK", Collections.EMPTY_LIST,
            new TypedString("{}"));
      }
    }).build();

    client.fetchSpaceBlocking();
    assertTrue(res[0]);
  }

  @Test public void testPreview() throws Exception {
    CDAClient client = TestClientFactory.newInstance()
        .setAccessToken("e5e8d4c5c122cf28fc1af3ff77d28bef78a3952957f15067bbc29f2f0dde0b50")
        .preview()
        .build();

    client.fetchSpaceBlocking();
  }
}
