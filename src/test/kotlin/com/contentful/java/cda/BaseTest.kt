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

import com.squareup.okhttp.mockwebserver.MockWebServer
import retrofit.RestAdapter
import org.junit.Before as before
import org.junit.After as after
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import com.contentful.java.cda.lib.TestCallback
import com.squareup.okhttp.mockwebserver.MockResponse
import com.contentful.java.cda.lib.TestUtils

/**
 * BaseTest.
 */
open class BaseTest {
    var server: MockWebServer? = null
    var client: CDAClient? = null

    before fun setUp() {
        // MockWebServer
        server = MockWebServer()
        server!!.play()

        // Client
        client = CDAClient.Builder()
                .setAccessToken("token")
                .setSpaceKey("spaceid")
                .setEndpoint(getServerUrl())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .noSSL()
                .build()

        enqueue("space_fetch_response.json")
        client!!.assets().ensureSpace(true)
        server!!.takeRequest()
    }

    after fun tearDown() {
        server!!.shutdown()
    }

    fun <T> assertTestCallback(cb: CDACallback<T>): T {
        if (cb !is TestCallback) {
            throw IllegalArgumentException("callback should be an instance of TestCallback.")
        }
        cb.await()
        assertNull(cb.error)
        if (cb.allowEmpty) {
            return null
        }
        assertNotNull(cb.value)
        return cb.value!!
    }

    fun enqueue(fileName: String) {
        server!!.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody(TestUtils.fileToString(fileName)))
    }

    fun getServerUrl(): String {
        val url = server!!.getUrl("/")
        return url.toString().substring(url.getProtocol().length() + 3)
    }
}
