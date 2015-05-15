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

import org.robolectric.Robolectric
import android.content.Intent
import kotlin.test.assertEquals
import android.app.Activity
import android.os.Looper
import android.os.Bundle
import retrofit.RestAdapter
import com.contentful.java.cda.model.CDAArray
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import org.junit.Test as test
import retrofit.RetrofitError

/**
 * Android Tests.
 */
[RunWith(javaClass<RobolectricTestRunner>())]
[Config(manifest=Config.NONE)]
class AndroidTests : BaseTest() {
    test fun testCallbackExecutesOnMainThread() {
        enqueue("space_fetch_response.json")
        enqueue("asset_fetch_all_response.json")

        val activity = Robolectric.buildActivity(javaClass<TestActivity>())
                .withIntent(Intent().putExtra("EXTRA_URL", getServerUrl()))
                .create()
                .get()

        while (activity.callbackLooper == null) {
            Thread.sleep(1000)
        }

        assertEquals(activity.mainThreadLooper, activity.callbackLooper)
    }

    class TestActivity : Activity() {
        val mainThreadLooper = Looper.getMainLooper()
        var callbackLooper: Looper? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super<Activity>.onCreate(savedInstanceState)
            val cb = object : CDACallback<CDAArray>() {
                override fun onSuccess(result: CDAArray?) {
                    callbackLooper = Looper.myLooper()
                }

                override fun onFailure(retrofitError: RetrofitError?) {
                    super<CDACallback>.onFailure(retrofitError)
                    retrofitError!!.printStackTrace()
                }
            }

            val androidClient = CDAClient.builder()
                    .setSpaceKey("space")
                    .setAccessToken("token")
                    .setEndpoint(getIntent().getStringExtra("EXTRA_URL"))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .noSSL()
                    .build()

            androidClient.assets().async().fetchAll(cb)
        }
    }
}
