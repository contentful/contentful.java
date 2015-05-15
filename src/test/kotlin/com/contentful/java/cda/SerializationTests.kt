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
import java.io.File
import com.contentful.java.cda.model.CDAArray
import com.contentful.java.cda.model.CDAEntry
import com.contentful.java.cda.lib.TestUtils
import com.contentful.java.cda.model.CDAResource
import com.google.gson.JsonParseException
import kotlin.test.assertTrue

/**
 * Serialization Tests.
 */
class SerializationTests : BaseTest() {
    test fun testSerialization() {
        enqueue("entry_fetch_all_response.json")
        val original = client!!.entries().fetchAll()
        val file = File("asset.dat");
        file.createNewFile()
        file.deleteOnExit()

        ResourceUtils.saveResourceToFile(original, file)
        val result = ResourceUtils.readResourceFromFile(file) as CDAArray

        setOf(original, result).forEach { EntryTests.verifyFetchAll(it, client!!) }
    }

    test(expected = javaClass<JsonParseException>())
    fun testTypeAdapterInstantiationException() {
        val cli = CDAClient.builder()
                .setSpaceKey("space")
                .setAccessToken("token")
                .setCustomClasses(hashMapOf(
                        Pair("dog", javaClass<BadClass1>())
                )).build()


        try {
            cli!!.gson.fromJson(TestUtils.fileToString("entry_fetch_one_response.json"),
                    javaClass<CDAEntry>())
        } catch(e: JsonParseException) {
            assertTrue(e.getCause() is InstantiationException)
            throw e
        }
    }

    test(expected = javaClass<JsonParseException>())
    fun testTypeAdapterIllegalAccessException() {
        val cli = CDAClient.builder()
                .setSpaceKey("space")
                .setAccessToken("token")
                .setCustomClasses(hashMapOf(
                        Pair("dog", javaClass<BadClass2>())
                )).build()


        try {
            cli!!.gson.fromJson(TestUtils.fileToString("entry_fetch_one_response.json"),
                    javaClass<CDAEntry>())
        } catch(e: JsonParseException) {
            assertTrue(e.getCause() is IllegalAccessException)
            throw e
        }
    }

    class BadClass1(val whatever: String) : CDAResource()
    class BadClass2() : CDAEntry() {
        init {
            throw IllegalAccessException()
        }
    }
}
