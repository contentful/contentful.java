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

package com.contentful.java.api;

import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * RxJava Extensions.
 */
final class RxExtensions {
  private RxExtensions() {
    throw new UnsupportedOperationException();
  }

  /**
   * Base Action.
   */
  abstract static class AbsAction<T> implements Action1<T> {
    final CDACallback<T> callback;

    public AbsAction(CDACallback<T> callback) {
      this.callback = callback;
    }
  }

  /**
   * Success Action.
   */
  static class ActionSuccess<T> extends AbsAction<T> {
    public ActionSuccess(CDACallback<T> callback) {
      super(callback);
    }

    @Override public void call(T t) {
      if (!callback.isCancelled()) {
        callback.onSuccess(t, null);
      }
    }
  }

  /**
   * Error Action.
   */
  static class ActionError extends AbsAction<Throwable> {
    @SuppressWarnings("unchecked")
    public ActionError(CDACallback callback) {
      super(callback);
    }

    @Override public void call(Throwable t) {
      if (!callback.isCancelled()) {
        if (t instanceof RetrofitError) {
          callback.onFailure((RetrofitError) t);
        } else {
          callback.onFailure(RetrofitError.unexpectedError(null, t));
        }
      }
    }
  }

  /**
   * DefFunc.
   */
  abstract static class DefFunc<T> implements Func0<Observable<T>> {
    @Override public final Observable<T> call() {
      return Observable.just(method());
    }

    abstract T method();
  }

  /**
   * Creates an Observable with the given {@code func} function and subscribes to it
   * with a set of pre-defined actions. The provided {@code callback} will be passed to these
   * actions in order to populate the events.
   */
  static <R> CDACallback<R> defer(DefFunc<R> func, CDACallback<R> callback) {
    if (callback == null) {
      throw new IllegalArgumentException("callback may not be null.");
    }

    Observable.defer(func)
        .observeOn(Schedulers.io())
        .subscribe(
            new ActionSuccess<R>(callback),
            new ActionError(callback));
    return callback;
  }
}
