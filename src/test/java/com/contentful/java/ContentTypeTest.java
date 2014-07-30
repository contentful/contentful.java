package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAClient;
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

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_types.json"));

        customClient.fetchContentTypes(new TestCallback<CDAListResult>(cdl) {
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

    public void testFetchContentTypeWithIdentifier() throws Exception {
        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_type_with_id.json"));

        final CDAContentType[] result = new CDAContentType[]{null};
        CountDownLatch cdl = new CountDownLatch(1);

        customClient.fetchContentTypeWithIdentifier("MOCK", new TestCallback<CDAContentType>(cdl) {
            @Override
            protected void onSuccess(CDAContentType cdaContentType, Response response) {
                result[0] = cdaContentType;
                super.onSuccess(cdaContentType, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertEquals("Cat", result[0].name);
        assertEquals("name", result[0].displayField);
        assertEquals("Meow.", result[0].description);
        assertEquals("cat", result[0].sys.id);
        assertEquals(8, result[0].fieldsList.size());

        assertEquals("name", result[0].fieldsList.get(0).get("id"));
        assertEquals("likes", result[0].fieldsList.get(1).get("id"));
        assertEquals("color", result[0].fieldsList.get(2).get("id"));
        assertEquals("bestFriend", result[0].fieldsList.get(3).get("id"));
        assertEquals("birthday", result[0].fieldsList.get(4).get("id"));
        assertEquals("lifes", result[0].fieldsList.get(5).get("id"));
        assertEquals("lives", result[0].fieldsList.get(6).get("id"));
        assertEquals("image", result[0].fieldsList.get(7).get("id"));
    }
}
