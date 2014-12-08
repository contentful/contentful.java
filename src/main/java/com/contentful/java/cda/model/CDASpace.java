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
 * Class representing a single Space resource.
 */
public class CDASpace extends CDAResource {
  private String defaultLocale;
  private ArrayList<CDALocale> locales;
  private String name;

  public CDASpace(String defaultLocale, ArrayList<CDALocale> locales, String name) {
    this.defaultLocale = defaultLocale;
    this.locales = locales;
    this.name = name;
  }

  public String getDefaultLocale() {
    return defaultLocale;
  }

  public ArrayList<CDALocale> getLocales() {
    return locales;
  }

  public String getName() {
    return name;
  }
}
