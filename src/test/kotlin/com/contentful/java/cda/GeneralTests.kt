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
import com.contentful.java.cda.model.ArrayResource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.lang.reflect.InvocationTargetException
import java.io.UnsupportedEncodingException
import kotlin.test.assertNull
import org.mockito.Mockito
import java.io.Closeable
import java.io.IOException
import kotlin.test.assertFalse

/**
 * General Tests.
 */
class GeneralTests : BaseTest() {
    test(expected = javaClass<IllegalArgumentException>())
    fun testAssertNotNull() {
        val param = "param"

        try {
            Utils.assertNotNull(null, param)
        } catch(e: IllegalArgumentException) {
            assertEquals("${param} may not be null.", e.getMessage())
            throw e
        }
    }
    test(expected = javaClass<RuntimeException>())
    fun testQueryParamThrowsRuntimeException() {
        val message = "message"

        try {
            Utils.getQueryParamFromUrl("http://a.com/b?c=d", "c", {
                throw UnsupportedEncodingException(message)
            })
        } catch (e: RuntimeException) {
            assertTrue(e.getCause() is UnsupportedEncodingException)
            assertEquals("java.io.UnsupportedEncodingException: ${message}", e.getMessage())
            throw e
        }
    }

    test fun testQueryParam() {
        assertNull(Utils.getQueryParamFromUrl("http://a.com/b?c=d=e=f", "c"))
        assertNull(Utils.getQueryParamFromUrl("http://a.com/b?", "c"))
        assertNull(Utils.getQueryParamFromUrl("http://a.com/b", "c"))
    }

    test(expected = javaClass<IllegalArgumentException>())
    fun testArrayParserThrowsOnInvalidInput() {
        class InvalidInput : ArrayResource() { }

        try {
            ArrayParser(InvalidInput(), null).call()
        } catch(e: IllegalArgumentException) {
            assertEquals("Invalid input.", e.getMessage())
            throw e
        }
    }

    test fun testConstantsThrowsUnsupportedException() {
        assertPrivateConstructor(javaClass<Constants>())
    }

    test fun testRxExtensionsThrowsUnsupportedException() {
        assertPrivateConstructor(javaClass<RxExtensions>())
    }

    test fun testUtilsThrowsUnsupportedException() {
        assertPrivateConstructor(javaClass<Utils>())
    }

    test fun testResourceUtilsThrowsUnsupportedException() {
        assertPrivateConstructor(javaClass<ResourceUtils>())
    }

    test fun testCloseStream() {
        val closable = Mockito.mock(javaClass<Closeable>())
        Mockito.`when`(closable.close()).thenThrow(javaClass<IOException>())
        assertFalse(ResourceUtils.closeStream(closable))
    }

    fun assertPrivateConstructor(clazz: Class<out Any>) {
        var ctor = clazz.getDeclaredConstructor()
        ctor.setAccessible(true)
        var exception = try {
            ctor.newInstance()
        } catch(e: Exception) {
            e
        }

        assertNotNull(exception)
        assertTrue(exception is InvocationTargetException)
        assertTrue((exception as InvocationTargetException).getCause() is
                UnsupportedOperationException)
    }
}
