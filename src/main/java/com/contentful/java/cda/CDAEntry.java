package com.contentful.java.cda;

public class CDAEntry extends LocalizedResource {
  private CDAContentType contentType;

  public CDAContentType contentType() {
    return contentType;
  }

  void setContentType(CDAContentType contentType) {
    this.contentType = contentType;
  }

  @Override public String toString() {
    return "CDAEntry{" +
        "id='" + id() + '\'' +
        '}';
  }
}
