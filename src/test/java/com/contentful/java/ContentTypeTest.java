package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.lib.TestClientResult;
import com.contentful.java.model.CDAContentType;
import com.contentful.java.model.CDAListResult;

import java.util.Map;

/**
 * Set of tests for fetching Content Types.
 */
public class ContentTypeTest extends AbsTestCase {
    public void testFetchContentTypes() throws Exception {
        TestClientResult<CDAListResult> result = new TestClientResult<CDAListResult>();

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_types.json"));

        customClient.fetchContentTypes(new TestCallback<CDAListResult>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals(2, result.value.total);
        assertEquals(0, result.value.skip);
        assertEquals(100, result.value.limit);
        assertEquals(2, result.value.items.size());

        // 1st item (City)
        CDAContentType item = (CDAContentType) result.value.items.get(0);
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
        item = (CDAContentType) result.value.items.get(1);
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
        TestClientResult<CDAContentType> result = new TestClientResult<CDAContentType>();

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_type_with_id.json"));

        customClient.fetchContentTypeWithIdentifier("MOCK", new TestCallback<CDAContentType>(result));
        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals("Cat", result.value.name);
        assertEquals("name", result.value.displayField);
        assertEquals("Meow.", result.value.description);
        assertEquals("cat", result.value.sys.id);
        assertEquals(8, result.value.fieldsList.size());

        assertEquals("name", result.value.fieldsList.get(0).get("id"));
        assertEquals("likes", result.value.fieldsList.get(1).get("id"));
        assertEquals("color", result.value.fieldsList.get(2).get("id"));
        assertEquals("bestFriend", result.value.fieldsList.get(3).get("id"));
        assertEquals("birthday", result.value.fieldsList.get(4).get("id"));
        assertEquals("lifes", result.value.fieldsList.get(5).get("id"));
        assertEquals("lives", result.value.fieldsList.get(6).get("id"));
        assertEquals("image", result.value.fieldsList.get(7).get("id"));
    }
}
