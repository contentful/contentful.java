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
import retrofit.RetrofitError
import com.contentful.java.cda.model.CDASyncedSpace
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import com.contentful.java.cda.model.CDAResource
import java.io.InputStreamReader

/**
 * Module Tests.
 */
class ModuleTests : BaseTest() {
    test fun testSyncThrowsRetrofitErrorOnFailure() {
        val module = object : ModuleSync(client!!.synchronization().context) {
            override fun prepare(originalSpace: CDASyncedSpace?, updatedSpace: CDASyncedSpace?,
                    iterate: Boolean): CDASyncedSpace? {
                throw UnsupportedOperationException()
            }
        }

        val methods = setOf(
                Runnable { module.performInitial() },
                Runnable { module.performWithToken("token") },
                Runnable {
                    val syncedSpace = CDASyncedSpace()
                    syncedSpace.setSyncToken("token")
                    module.performWithSpace(syncedSpace)
                })

        var count = 0

        methods.forEach {
            enqueue("space_fetch_response.json")
            enqueue("sync_initial_response.json")

            try {
                it.run()
            } catch (e: RetrofitError) {
                assertTrue(e.getCause() is UnsupportedOperationException)
                count++
            }
        }

        assertEquals(3, count)
    }

    test fun testBaseThrowsRetrofitErrorOnFailure() {
        val module = object : BaseModule<CDAResource>(client!!.synchronization().context) {
            override fun getResourcePath(): String? = ""
            override fun createCdaResource(inputStreamReader: InputStreamReader?): CDAResource? = CDAResource()
            override fun async(): BaseModule.ExtAsync? = extAsync
            override fun rx(): BaseModule.ExtRxJava? = extRxJava

            override fun <R : CDAResource?> prepare(resource: R): R {
                throw UnsupportedOperationException()
            }
        }

        val methods = setOf(
                Runnable {
                    enqueue("entry_fetch_all_response.json")
                    module.fetchAll()
                },
                Runnable {
                    enqueue("entry_fetch_one_response.json")
                    module.fetchOne("")
                })

        var count = 0

        methods.forEach {
            try {
                it.run()
            } catch (e: RetrofitError) {
                assertTrue(e.getCause() is UnsupportedOperationException)
                count++
            }
        }

        assertEquals(2, count)
    }
}
