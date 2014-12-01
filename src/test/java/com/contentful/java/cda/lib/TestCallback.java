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

package com.contentful.java.cda.lib;

import com.contentful.java.cda.CDACallback;
import java.util.concurrent.CountDownLatch;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback to be used in unit tests.
 */
public class TestCallback<T> extends CDACallback<T> {
  public T value;
  public RetrofitError error;
  private final CountDownLatch cdl;

  public TestCallback() {
    this.cdl = new CountDownLatch(1);
  }

  @Override protected void onSuccess(T t, Response response) {
    this.value = t;
    this.cdl.countDown();
  }

  @Override protected void onFailure(RetrofitError retrofitError) {
    this.error = retrofitError;
    this.cdl.countDown();
  }

  public void await() throws InterruptedException {
    this.cdl.await();
  }
}
