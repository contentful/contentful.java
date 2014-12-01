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
import org.junit.Test as test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Space Tests.
 */
class SpaceTests : BaseTest() {
    test fun testFetch() {
        enqueue("space_fetch_response.json")

        val result = assertTestCallback(
                client!!.spaces().async().fetch(TestCallback()))

        assertEquals(result, client!!.getSpace())

        assertEquals("Contentful Example API", result.getName())
        assertEquals("Space", result.getSys()["type"])
        assertEquals("cfexampleapi", result.getSys()["id"])
        assertEquals("en-US", result.getDefaultLocale())

        val locales = result.getLocales()
        assertEquals(2, locales.size)
        assertEquals("en-US", locales[0].getCode())
        assertEquals("English", locales[0].getName())
        assertTrue(locales[0].isDefault())

        assertEquals("tlh", locales[1].getCode())
        assertEquals("Klingon", locales[1].getName())
        assertFalse(locales[1].isDefault())

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("GET", recordedRequest.getMethod())
        assertEquals("/spaces/spaceid", recordedRequest.getPath())
    }
}