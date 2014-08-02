package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAContentType;
import com.contentful.java.model.CDAResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for fetching Content Type resources.
 */
public class ContentTypeTest extends AbsTestCase {
    @Test
    public void testFetchContentTypes() throws Exception {
        TestCallback<CDAArray> callback = new TestCallback<CDAArray>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_types.json"));

        client.fetchContentTypes(callback);
        callback.await();
        verifyResultNotEmpty(callback);

        CDAArray result = callback.value;

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

    @Test
    public void testFetchContentTypeWithIdentifier() throws Exception {
        TestCallback<CDAContentType> callback = new TestCallback<CDAContentType>();

        client = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_content_type_with_id.json"));

        client.fetchContentTypeWithIdentifier("MOCK", callback);
        callback.await();
        verifyResultNotEmpty(callback);

        assertEquals("Cat", callback.value.getName());
        assertEquals("name", callback.value.getDisplayField());
        assertEquals("Meow.", callback.value.getUserDescription());
        assertEquals("cat", callback.value.getSys().get("id"));

        List<Map> fields = callback.value.getFields();
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
