package com.contentful.java.cda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.contentful.java.cda.CDAType.ASSET;
import static com.contentful.java.cda.CDAType.ENTRY;

final class Localization {
  private Localization() {
    throw new AssertionError();
  }

  static void localizeResources(List<CDAResource> resources, CDASpace space) {
    for (CDAResource resource : resources) {
      CDAType type = resource.type();
      if (ASSET.equals(type) || ENTRY.equals(type)) {
        localize((LocalizedResource) resource, space);
      }
    }
  }

  private static void localize(LocalizedResource resource, CDASpace space) {
    resource.localized = new HashMap<String, Map<String, ? super Object>>();
    String localeCode = resource.getAttribute("locale");
    if (localeCode == null) {
      localizeFromSync(resource, space);
    } else {
      localizeWithDefault(resource, localeCode);
    }
  }

  private static void localizeFromSync(LocalizedResource resource, CDASpace space) {
    for (CDALocale locale : space.locales()) {
      Map<String, ? super Object> fieldsMap = new HashMap<String, Object>();
      for (Map.Entry<String, ? super Object> rawField : resource.rawFields.entrySet()) {
        Map<?, ?> map = (Map) rawField.getValue();
        Object value = map.get(locale.code());
        if (value != null) {
          fieldsMap.put(rawField.getKey(), value);
        }
      }
      resource.localized.put(locale.code(), fieldsMap);
    }
    resource.locale = space.defaultLocale().code();
    resource.activeFields = resource.localized.get(space.defaultLocale().code());
  }

  private static void localizeWithDefault(LocalizedResource resource, String localeCode) {
    resource.locale = localeCode;
    resource.activeFields = resource.rawFields;
    resource.localized = new HashMap<String, Map<String, ? super Object>>();
    resource.localized.put(localeCode, resource.activeFields);
  }
}
