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

package com.contentful.java.cda.model;

import java.util.ArrayList;

/**
 * A class to represent the result of a Space synchronization.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDASyncedSpace extends ArrayResource {
  private ArrayList<CDAResource> items;
  private String nextSyncUrl;
  private String nextPageUrl;
  private String syncToken;

  public ArrayList<CDAResource> getItems() {
    return items;
  }

  /**
   * Returns the next sync URL.
   */
  public String getNextSyncUrl() {
    return nextSyncUrl;
  }

  /**
   * Returns the next page URL.
   */
  public String getNextPageUrl() {
    return nextPageUrl;
  }

  /**
   * Returns the sync token from this Space's {@code nextSyncUrl} value.
   */
  public String getSyncToken() {
    return syncToken;
  }

  /**
   * Sets the sync token for this synced Space.
   *
   * @param syncToken String representing the sync token
   */
  public void setSyncToken(String syncToken) {
    this.syncToken = syncToken;
  }
}
