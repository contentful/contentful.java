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

import org.junit.Test as test
import com.contentful.java.cda.lib.TestCallback
import com.contentful.java.cda.model.CDASyncedSpace
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.contentful.java.cda.model.CDAEntry
import com.squareup.okhttp.mockwebserver.RecordedRequest
import com.squareup.okhttp.mockwebserver.MockResponse
import com.contentful.java.cda.model.CDAAsset

/**
 * Sync Tests.
 */
class SyncTests : BaseTest() {
    test fun testWithSyncToken() {
        enqueue("space_fetch_response.json")
        enqueue("sync_initial_response.json")

        assertTestCallback(client!!.synchronization().async().performWithToken(
                "token", TestCallback()))

        // Request
        server!!.takeRequest()
        val request = server!!.takeRequest()
        assertEquals("GET", request.getMethod())
        assertEquals("/spaces/spaceid/sync?sync_token=token", request.getPath())
    }

    test fun testRemoteSynchronization() {
        val cli = CDAClient.Builder()
                .setAccessToken("9caa9b36fbb4250508b8e3322861dbddd7527bc02ba28f44b926c5d99f22d2ab")
                .setSpaceKey("hvjkfbzcwrfn")
                .build()

        val result = assertTestCallback(cli.synchronization().async().performInitial(
                TestCallback()))

        assertTestCallback(cli.synchronization().async().performWithSpace(result, TestCallback()))
    }

    test fun testSynchronization() {
        // Initial
        enqueue("space_fetch_response.json")
        enqueue("sync_initial_response.json")
        val first = assertTestCallback(
                client!!.synchronization().async().performInitial(TestCallback()))

        server!!.takeRequest()
        verifySyncFirst(first, server!!.takeRequest())

        // Update
        enqueue("space_fetch_response.json")
        enqueue("sync_update_response.json")
        val second = assertTestCallback(client!!.synchronization().async().performWithSpace(
                        first, TestCallback()))

        server!!.takeRequest()
        verifySyncSecond(second, server!!.takeRequest())
    }

    fun verifySyncFirst(result: CDASyncedSpace, request: RecordedRequest) {
        val items = result.getItems()
        assertEquals(4, items.size)

        assertTrue(items[0] is CDAEntry)
        assertTrue(items[1] is CDAEntry)
        assertTrue(items[2] is CDAEntry)
        assertTrue(items[3] is CDAAsset)

        val yiltiquoar = items[0] as CDAEntry
        assertEquals("Yiltiquoar", yiltiquoar.getFields()["name"])
        assertEquals(9999.toDouble(), yiltiquoar.getFields()["age"])

        val tzayclibbon = items[1] as CDAEntry
        assertEquals("Tzayclibbon", tzayclibbon.getFields()["name"])
        assertEquals(2405.toDouble(), tzayclibbon.getFields()["age"])

        val zahazah = items[2] as CDAEntry
        assertEquals("Za'ha'zah", zahazah.getFields()["name"])
        assertEquals(2789.toDouble(), zahazah.getFields()["age"])

        val asset = items[3] as CDAAsset
        assertEquals("ab", asset.getFields()["title"])
        assertEquals("image/jpeg", asset.getMimeType())
        assertEquals("${client!!.getHttpScheme()}://images.contentful.com/a/b/c/d.jpg",
                asset.getUrl())

        // Request
        assertEquals("GET", request.getMethod())
        assertEquals("/spaces/spaceid/sync?initial=true", request.getPath())
    }

    fun verifySyncSecond(result: CDASyncedSpace, request: RecordedRequest) {
        val items = result.getItems()
        assertEquals(3, items.size)

        assertTrue(items[0] is CDAEntry)
        assertTrue(items[1] is CDAEntry)
        assertTrue(items[2] is CDAEntry)

        val ooctaiphus = items[0] as CDAEntry
        assertEquals("Ooctaiphus", ooctaiphus.getFields()["name"])
        assertEquals(2.toDouble(), ooctaiphus.getFields()["age"])

        val yiltiquoar = items[1] as CDAEntry
        assertEquals("Yiltiquoar", yiltiquoar.getFields()["name"])
        assertEquals(666666.toDouble(), yiltiquoar.getFields()["age"])

        val zahazah = items[2] as CDAEntry
        assertEquals("Za'ha'zah", zahazah.getFields()["name"])
        assertEquals(2789.toDouble(), zahazah.getFields()["age"])

        assertEquals("FAKE", result.getSyncToken())

        // Request
        assertEquals("GET", request.getMethod())
        assertEquals("/spaces/spaceid/sync?sync_token=FAKE", request.getPath())
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsPerformWithInvalidSpace() {
        server!!.enqueue(MockResponse().setResponseCode(200))

        try {
            client!!.synchronization().performWithSpace(CDASyncedSpace())
        } catch(e: IllegalArgumentException) {
            assertEquals("performWithSpace() called for a space with no sync token.",
                    e.getMessage())
            throw e
        }
    }
}
