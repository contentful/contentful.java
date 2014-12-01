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

package com.contentful.java.cda

import com.contentful.java.cda.lib.TestCallback
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.contentful.java.cda.model.CDAContentType
import org.junit.Test as test
import com.contentful.java.cda.Constants.CDAFieldType

/**
 * Content Type Tests.
 */
class ContentTypeTests : BaseTest() {
    test fun testFetchAll() {
        enqueue("content_type_fetch_all_response.json")

        val result = assertTestCallback(
                client!!.contentTypes().async().fetchAll(TestCallback()))

        assertEquals(1, result.getTotal())
        assertEquals(1, result.getItems().size)

        assertTrue(result.getItems()[0] is CDAContentType)
        val city = result.getItems()[0] as CDAContentType

        assertEquals("City", city.getName())
        assertEquals("name", city.getDisplayField())

        // Fields
        val fields = city.getFields()
        assertEquals(2, fields.size)

        // Field: Name
        assertEquals("Name", fields[0].get("name"))
        assertEquals("name", fields[0].get("id"))
        assertEquals("Text", fields[0].get("type"))
        assertEquals(true, fields[0].get("required"))

        // Field: Center
        assertEquals("Center", fields[1].get("name"))
        assertEquals("center", fields[1].get("id"))
        assertEquals("Location", fields[1].get("type"))
        assertEquals(true, fields[1].get("required"))


        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/content_types", recordedRequest.getPath())
    }

    test fun testFetchAllWithQuery() {
        enqueue("content_type_fetch_all_response.json")

        assertTestCallback(client!!.contentTypes().async().fetchAll(linkedMapOf(
                Pair("sys.id[ne]", "whatever"),
                Pair("content_type", "city")), TestCallback()))

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/content_types?sys.id[ne]=whatever&content_type=city",
                recordedRequest.getPath())
    }

    test fun testFetchOne() {
        enqueue("content_type_fetch_one_response.json")

        val result = assertTestCallback(client!!.contentTypes().async().fetchOne(
                "ct", TestCallback())) as CDAContentType

        assertEquals("ctname", result.getName())
        assertEquals("ctdesc", result.getUserDescription())
        assertEquals("t", result.getDisplayField())

        // Fields
        val fields = result.getFields()
        assertEquals(10, fields.size)

        val fieldTypes = listOf(
                CDAFieldType.Text,
                CDAFieldType.Symbol,
                CDAFieldType.Integer,
                CDAFieldType.Number,
                CDAFieldType.Boolean,
                CDAFieldType.Date,
                CDAFieldType.Location,
                CDAFieldType.Link,
                CDAFieldType.Object,
                CDAFieldType.Array)

        fieldTypes.withIndices().forEach {
            val field = fields[it.first]
            assertEquals("id${it.first}", field["id"])
            assertEquals("name${it.first}", field["name"])
            it.second.assertFrom(field["type"]!!)
        }

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/content_types/ct", recordedRequest.getPath())
    }

    fun Constants.CDAFieldType.assertFrom(obj: Any) {
        assertTrue(equals(Constants.CDAFieldType.valueOf(obj.toString())))
    }
}