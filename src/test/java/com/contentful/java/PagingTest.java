package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Paging tests.
 */
public class PagingTest extends AbsTestCase {
    @Test
    public void testArrayPaging() throws Exception {
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
