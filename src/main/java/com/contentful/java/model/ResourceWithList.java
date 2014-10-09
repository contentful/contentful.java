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

package com.contentful.java.model;

import java.util.List;

/**
 * Class representing a CDA resource having an array of fields.
 */
public class ResourceWithList<T> extends CDAResource {
  // List of fields
  private List<T> fields;

  /**
   * Gets the fields list for this resource.
   *
   * @return List of fields.
   */
  public List<T> getFields() {
    return fields;
  }

  /**
   * Sets the fields list for this resource.
   *
   * @param fields List of fields.
   */
  public void setFields(List<T> fields) {
    this.fields = fields;
  }
}
