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

package com.contentful.java.cda;

import com.contentful.java.cda.model.CDAResource;
import com.contentful.java.cda.model.CDASpace;
import com.contentful.java.cda.model.CDASyncedSpace;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import retrofit.client.Response;

import static com.contentful.java.cda.lib.Constants.CDAResourceType;

/**
 * Custom Callable used internally to prepare array sync result objects.
 *
 * This attempts to merge two {@code CDASyncedSpace} objects together while taking into account
 * created, deleted and updated resources.
 *
 * In case no {@code updatedSpace} is provided, merge operation will be skipped.
 *
 * This will also create and execute an {@code ArrayParser} before returning the result to resolve
 * any links after the merge operation.
 */
class SpaceMerger implements Callable<CDASyncedSpace> {
  private final CDASyncedSpace originalSpace;
  private final CDASyncedSpace updatedSpace;
  private final CDACallback<CDASyncedSpace> callback;
  private final Response response;
  private final CDASpace space;

  public SpaceMerger(CDASyncedSpace originalSpace, CDASyncedSpace updatedSpace,
      CDACallback<CDASyncedSpace> callback, Response response, CDASpace space) {

    this.originalSpace = originalSpace;
    this.updatedSpace = updatedSpace;
    this.callback = callback;
    this.response = response;
    this.space = space;
  }

  @SuppressWarnings("unchecked")
  @Override public CDASyncedSpace call() throws Exception {
    if (originalSpace != null) {
      ArrayList<CDAResource> originalItems = new ArrayList<CDAResource>(originalSpace.getItems());
      ArrayList<CDAResource> updatedItems = updatedSpace.getItems();

      for (int i = updatedItems.size() - 1; i >= 0; i--) {
        CDAResource item = updatedItems.get(i);
        CDAResourceType resourceType = CDAResourceType.valueOf((String) item.getSys().get("type"));

        if (CDAResourceType.DeletedAsset.equals(resourceType)) {
          item.getSys().put("type", CDAResourceType.Asset.toString());
          originalItems.remove(item);
        } else if (CDAResourceType.DeletedEntry.equals(resourceType)) {
          item.getSys().put("type", CDAResourceType.Entry.toString());
          originalItems.remove(item);
        } else if (CDAResourceType.Asset.equals(resourceType) || CDAResourceType.Entry.equals(
            resourceType)) {

          originalItems.remove(item);
          originalItems.add(0, item);
        }
      }

      updatedItems.clear();
      updatedItems.addAll(originalItems);
    }

    CDASyncedSpace result = null;

    try {
      result = new ArrayParser<CDASyncedSpace>(updatedSpace, space).call();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (callback != null && !callback.isCancelled()) {
      callback.success(result, response);
    }

    return result;
  }
}
