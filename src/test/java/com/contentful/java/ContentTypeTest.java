package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAContentType;
import com.contentful.java.model.CDAListResult;
import retrofit.client.Response;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tomxor on 30/07/14.
 */
public class ContentTypeTest extends AbsTestCase {
    public void testFetchContentTypes() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        final CDAListResult[] result = new CDAListResult[]{null};

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_types.json"));

        client.fetchContentTypes(new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onSuccess(CDAListResult cdaListResult, Response response) {
                result[0] = cdaListResult;
                super.onSuccess(cdaListResult, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertEquals(2, result[0].getTotal());
        assertEquals(0, result[0].getSkip());
        assertEquals(100, result[0].getLimit());
        assertEquals(2, result[0].getItems().size());

        // 1st item (City)
        CDAContentType item = (CDAContentType) result[0].getItems().get(0);
        assertEquals("City", item.name);
        assertNull(item.description);
        assertEquals("name", item.displayField);
        assertEquals("1t9IbcfdCk6m04uISSsaIK", item.sys.id);

        // field #1
        Map<String, ?> field = item.fieldsList.get(0);
        assertEquals("Name", field.get("name"));
        assertEquals("name", field.get("id"));
        assertEquals("Text", field.get("type"));
        assertTrue((Boolean) field.get("required"));

        // field #2
        field = item.fieldsList.get(1);
        assertEquals("Center", field.get("name"));
        assertEquals("center", field.get("id"));
        assertEquals("Location", field.get("type"));
        assertTrue((Boolean) field.get("required"));

        // 2nd item (Cat)
        item = (CDAContentType) result[0].getItems().get(1);
        assertEquals("Cat", item.name);
        assertEquals("Meow!", item.description);
        assertEquals("name", item.displayField);
        assertEquals("63k4qdEi9aI8IQUGaYGg4O", item.sys.id);

        // field #1
        field = item.fieldsList.get(0);
        assertEquals("name", field.get("id"));
        assertEquals("Name", field.get("name"));
        assertEquals("Text", field.get("type"));
        assertTrue((Boolean) field.get("required"));
        assertFalse((Boolean) field.get("localized"));
    }
}
