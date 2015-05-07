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

import com.contentful.java.cda.model.CDAAsset
import com.contentful.java.cda.lib.TestCallback
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test as test

/**
 * Asset Tests.
 */
class AssetTests : BaseTest() {
    test fun testFetchAll() {
        enqueue("asset_fetch_all_response.json")

        val result = assertTestCallback(
                client!!.assets().async().fetchAll(TestCallback()))

        assertEquals(1, result.getTotal())
        assertEquals(1, result.getItems().size)

        assertTrue(result.getItems()[0] is CDAAsset)
        val jake = result.getItems()[0] as CDAAsset
        assertEquals("Jake", jake.getFields().get("title"))
        assertEquals("image/png", jake.getMimeType())

        val file = jake.getFields().get("file") as Map<*, *>
        assertEquals("jake.png", file.get("fileName"))
        assertEquals("image/png", file.get("contentType"))

        val details = file.get("details") as Map<*, *>
        assertEquals(20480.toDouble(), details.get("size"))

        val image = details.get("image") as Map<*, *>
        assertEquals(100.toDouble(), image.get("width"))
        assertEquals(161.toDouble(), image.get("height"))
        assertEquals("//images.contentful.com/cfexampleapi/a/b/jake.png", file.get("url"))
        assertEquals(
                "${client!!.getHttpScheme()}://images.contentful.com/cfexampleapi/a/b/jake.png",
                jake.getUrl())

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/assets", recordedRequest.getPath())
    }

    test fun testFetchAllWithQuery() {
        enqueue("asset_fetch_all_response.json")

        assertTestCallback(client!!.assets().async().fetchAll(linkedMapOf(
                Pair("sys.id[ne]", "whatever"),
                Pair("content_type", "cat")),
                TestCallback()))

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/assets?sys.id[ne]=whatever&content_type=cat",
                recordedRequest.getPath())
    }

    test fun testFetchOne() {
        enqueue("asset_fetch_one_response.json")

        val result = assertTestCallback(
                client!!.assets().async().fetchOne(
                        "nyancat", TestCallback())) as CDAAsset

        val fields = result.getFields()

        assertEquals("Nyan Cat", fields.get("title"))

        val file = fields.get("file") as Map<*, *>
        assertEquals("Nyan_cat_250px_frame.png", file.get("fileName"))
        assertEquals("image/png", file.get("contentType"))

        val details = file.get("details") as Map<*, *>
        assertEquals(12273.toDouble(), details.get("size"))

        val image = details.get("image") as Map<*, *>
        assertEquals(1.toDouble(), image.get("width"))
        assertEquals(2.toDouble(), image.get("height"))
        assertEquals("//images.contentful.com/cfexampleapi/a/b/Nyan_cat_250px_frame.png",
                file.get("url"))

        val scheme = client!!.getHttpScheme()
        assertEquals("${scheme}://images.contentful.com/cfexampleapi/a/b/Nyan_cat_250px_frame.png",
                result.getUrl())

        assertEquals("image/png", result.getMimeType())

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/assets/nyancat", recordedRequest.getPath())
    }
}