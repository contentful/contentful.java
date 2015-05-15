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
import com.contentful.java.cda.model.CDAEntry
import com.contentful.java.cda.model.CDAResource
import com.contentful.java.cda.model.CDASpace
import com.squareup.okhttp.mockwebserver.MockResponse
import org.mockito.Mockito
import retrofit.RestAdapter
import retrofit.RetrofitError
import rx.Observable
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test as test

/**
 * Client Tests.
 */
class ClientTests : BaseTest() {
    test fun testCancelledCallback() {
        enqueue("space_fetch_response.json")

        val cdl = CountDownLatch(1)
        var called = false

        val cb = object : CDACallback<CDASpace>() {
            override fun onSuccess(result: CDASpace?) {
                called = true
                cdl.countDown()
            }

            override fun onFailure(retrofitError: RetrofitError?) {
                called = true
                cdl.countDown()
            }
        }

        cb.cancel()
        client!!.spaces().async().fetch(cb)
        cdl.await(3, TimeUnit.SECONDS)

        assertFalse(called)
    }

    test fun testCallbackRetrofitError() {
        val badClient = CDAClient.builder()
                .setSpaceKey("space")
                .setAccessToken("token")
                .setCallbackExecutor { it.run() }
                .setClient { cli ->
                    throw RetrofitError.unexpectedError(cli.getUrl(), IOException()) }
                .build()

        val cb = TestCallback<CDASpace>()
        badClient.spaces().async().fetch(cb)
        cb.await()
        assertNotNull(cb.error)
    }

    test fun testCallbackGeneralError() {
        var error: Throwable? = null

        val cb = object : CDACallback<CDASpace>() {
            override fun onSuccess(result: CDASpace?) {
            }

            override fun onFailure(retrofitError: RetrofitError?) {
                super<CDACallback>.onFailure(retrofitError)
                error = retrofitError
            }
        }

        Observable.defer {
            Observable.just(CDASpace(null, null, null))
        }.doOnEach {
            throw RuntimeException()
        }.subscribe(
                RxExtensions.ActionSuccess<CDASpace>(client!!.callbackExecutor, cb),
                RxExtensions.ActionError(client!!.callbackExecutor, cb))

        assertTrue(error is RetrofitError)
    }

    test fun testPreview() {
        val cli = CDAClient.builder()
                .setSpaceKey("cfexampleapi")
                .setAccessToken("e5e8d4c5c122cf28fc1af3ff77d28bef78a3952957f15067bbc29f2f0dde0b50")
                .preview()
                .build()

        cli.spaces().fetch()
    }

    test fun testAccessToken() {
        server!!.enqueue(MockResponse().setResponseCode(200))
        client!!.spaces().fetch()

        // Request
        val recordedRequest = server!!.takeRequest()
        assertEquals("Bearer token", recordedRequest.getHeader("Authorization"))
    }

    test fun testUserAgent() {
        server!!.enqueue(MockResponse().setResponseCode(200))
        client!!.spaces().fetch()

        val prefix = "contentful.java/"
        val versionName = PropertiesReader().getField(Constants.PROP_VERSION_NAME)

        // Request
        val recordedRequest = server!!.takeRequest()

        assertEquals("${prefix}${versionName}", recordedRequest.getHeader("User-Agent"))
    }

    test(expected = javaClass<RuntimeException>())
    fun testUserAgentThrowsRuntimeExceptionOnFailure() {
        try {
            val reader = Mockito.mock(javaClass<PropertiesReader>())

            Mockito.`when`(reader.getField(Constants.PROP_VERSION_NAME))
                    .thenThrow(javaClass<IOException>())

            CDAClient.sUserAgent = null
            client!!.createUserAgent(reader)
        } catch(e: RuntimeException) {
            assertEquals("Unable to retrieve version name.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsNoAccessToken() {
        try {
            CDAClient.builder().build()
        } catch (e: IllegalArgumentException) {
            assertEquals("Access token must be defined.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsNoSpace() {
        try {
            CDAClient.builder().setAccessToken("token").build()
        } catch (e: IllegalArgumentException) {
            assertEquals("Space ID must be defined.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullAccessToken() {
        try {
            CDAClient.builder().setAccessToken(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setAccessToken() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetSpaceKey() {
        try {
            CDAClient.builder().setSpaceKey(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setSpaceKey() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullCallbackExecutor() {
        try {
            CDAClient.builder().setCallbackExecutor(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setCallbackExecutor() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullCustomClasses() {
        try {
            CDAClient.builder().setCustomClasses(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setCustomClasses() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullClient() {
        try {
            CDAClient.builder().setClient(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setClient() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullLogLevel() {
        try {
            CDAClient.builder().setLogLevel(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setLogLevel() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullClientProvider() {
        try {
            CDAClient.builder().setClientProvider(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setClientProvider() with null.", e.getMessage())
            throw e
        }
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun failsSetNullEndPoint() {
        try {
            CDAClient.builder().setEndpoint(null)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot call setEndpoint() with null.", e.getMessage())
            throw e
        }
    }
}