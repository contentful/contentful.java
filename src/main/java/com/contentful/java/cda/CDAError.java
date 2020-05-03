package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Map;

public class CDAError implements Serializable {

  private static final long serialVersionUID = -8417594917359895169L;

  private Map<String, Object> sys;

  @JsonProperty
  private Map<String, Object> details;

  public Map<String, Object> getSys() {
    return sys;
  }

  public Map<String, Object> getDetails() {
    return details;
  }

}
