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

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a CDA resource having a map of fields.
 */
public class ResourceWithMap extends CDAResource {
  // Map of fields in their raw form as retrieved from the CDA.
  private Map<String, Object> rawFields;

  // Custom map prepared out of the original fields sectioned by different locales.
  private HashMap<String, Map> localizedFieldsMap = new HashMap<String, Map>();

  /**
   * Sets the raw fields map of this resource.
   *
   * @param rawFields Map of fields as retrieved from the CDA.
   */
  public void setRawFields(Map<String, Object> rawFields) {
    this.rawFields = rawFields;
  }

  /**
   * Gets the raw fields map of this resource.
   *
   * @return Map of fields as retrieved from the CDA.
   */
  public Map<String, Object> getRawFields() {
    return rawFields;
  }

  /**
   * Gets a localized map of fields.
   *
   * @return A custom map prepared out of the original fields sectioned by different locales.
   */
  public HashMap<String, Map> getLocalizedFieldsMap() {
    return localizedFieldsMap;
  }

  /**
   * Convenience method to get a Map of fields using the default / defined locale.
   * If no locale was set, the fields map will be retrieved with the default Space locale.
   *
   * @return Map of fields.
   */
  public Map getFields() {
    return localizedFieldsMap.get(this.locale);
  }
}
