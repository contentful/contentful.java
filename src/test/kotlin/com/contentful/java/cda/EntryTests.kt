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
import org.junit.Test as test
import com.contentful.java.cda.model.CDAEntry
import com.contentful.java.cda.model.CDAAsset
import com.contentful.java.cda.model.CDAArray
import kotlin.test.assertNotNull
import retrofit.RestAdapter
import kotlin.test.assertNull

/**
 * Entry Tests.
 */
class EntryTests : BaseTest() {
    test fun testFetchAllWithUnresolvedLink() {
        enqueue("entry_fetch_all_unresolved_link.json")
        val entry = client!!.entries().fetchAll().getItems()[0] as CDAEntry
        assertTrue(entry.getFields().get("linked") is Map<*, *>)

        val list = entry.getFields().get("linked_list") as List<*>
        assertEquals(2, list.size())
        assertTrue(list[0] is Map<*, *>)
    }

    test fun testNullifyUnresolvedLinks() {
        val cli = CDAClient.Builder()
                .setAccessToken("token")
                .setSpaceKey("spaceid")
                .setEndpoint(getServerUrl())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .nullifyUnresolvedLinks()
                .noSSL()
                .build()

        enqueue("space_fetch_response.json")
        enqueue("entry_fetch_all_unresolved_link.json")

        val entry = cli!!.entries().fetchAll().getItems()[0] as CDAEntry
        assertNull(entry.getFields().get("linked"))

        val list = entry.getFields().get("linked_list") as List<*>
        assertEquals(1, list.size())
        assertTrue(list[0] is CDAEntry)
    }

    test fun testCustomClass() {
        val cli = CDAClient.Builder()
                .setAccessToken("token")
                .setSpaceKey("space")
                .setEndpoint(getServerUrl())
                .noSSL()
                .setCustomClasses(hashMapOf(
                        Pair("dog", javaClass<Dog>())))
                .build()

        enqueue("space_fetch_response.json")
        enqueue("entry_fetch_one_response.json")

        val entry = cli.entries().fetchOne("dog")
        assertTrue(entry is Dog)
        val result = entry as Dog
        assertEquals("Doge", result.name)
        assertNotNull(result.image)
        assertEquals("Asset", (result.image!!["sys"] as Map<*, *>)["linkType"])
    }

    test fun testFetchAll() {
        enqueue("entry_fetch_all_response.json")

        val result = assertTestCallback(
                client!!.entries().async().fetchAll(TestCallback()))

        verifyFetchAll(result, client!!)

        val path = "/spaces/spaceid/entries"
        assertEquals("${server!!.getUrl(path)}", result.getOriginalUrl())

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals(path, recordedRequest.getPath())
    }

    test fun testFetchAllWithQuery() {
        enqueue("entry_fetch_all_response.json")

        assertTestCallback(client!!.entries().async().fetchAll(linkedMapOf(
                Pair("sys.id[ne]", "whatever"),
                Pair("content_type", "cat")),
                TestCallback()))

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/entries?sys.id[ne]=whatever&content_type=cat",
                recordedRequest.getPath())
    }

    test fun testFetchOne() {
        enqueue("entry_fetch_one_response.json")

        val result = assertTestCallback(
                client!!.entries().async().fetchOne(
                        "jake", TestCallback())) as CDAEntry

        assertEquals("en-US", result.getLocale())

        val fields = result.getFields()
        assertEquals("Doge", fields.get("name"))
        assertEquals("such json\nwow", fields.get("description"))

        // image
        assertTrue(fields.get("image") is Map<*, *>)
        val imageSys = (fields.get("image") as Map<*, *>).get("sys") as Map<*, *>
        assertEquals("Link", imageSys.get("type"))
        assertEquals("Asset", imageSys.get("linkType"))
        assertEquals("1x0xpXu4pSGS4OukSyWGUK", imageSys.get("id"))

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid/entries/jake", recordedRequest.getPath())
    }

    class object {
        fun verifyFetchAll(result: CDAArray, client: CDAClient) {
            assertEquals(2, result.getTotal())
            assertEquals(0, result.getSkip())
            assertEquals(100, result.getLimit())
            assertEquals(2, result.getItems().size)

            assertTrue(result.getItems()[0] is CDAEntry)
            val jake = result.getItems()[0] as CDAEntry
            val fields = jake.getFields()
            assertEquals("Jake", fields.get("name"))
            assertEquals("Bacon pancakes, makin' bacon pancakes!", fields.get("description"))

            // Asset Link
            assertTrue(fields.get("image") is CDAAsset)
            val image = fields.get("image") as CDAAsset
            assertEquals(
                    "${client.getHttpScheme()}://images.contentful.com/cfexampleapi/a/b/jake.png",
                    image.getUrl())
            assertEquals("image/png", image.getMimeType())
            assertEquals("Jake", image.getFields().get("title"))
            assertEquals(4, (image.getFields().get("file") as Map<*, *>).size)

            // Entry Link
            assertTrue(fields.get("entry") is CDAEntry)
            val entry = fields.get("entry") as CDAEntry
            assertEquals("Doge", entry.getFields()["name"])
            assertEquals("such json\nwow", entry.getFields()["description"])
        }
    }

    class Dog() : CDAEntry() {
        var name: String? = null
            get() = getField("name")

        var image: Map<*, *>? = null
            get() = getField("image")

        [suppress("UNCHECKED_CAST")]
        private fun <T> getField(name: String) = getFields()?.let { it[name] as T }
    }
}