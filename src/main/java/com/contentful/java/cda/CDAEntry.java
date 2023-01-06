package com.contentful.java.cda;

/**
 * The class represents a basic entry in the space.
 */
public class CDAEntry extends LocalizedResource {
  private static final long serialVersionUID = 5902790363045498307L;
  protected CDAContentType contentType;
  private CDAMetadata metadata;

  /**
   * @return the metadata set.
   */
  public CDAMetadata metadata() {
    return metadata;
  }

  /**
   * @return the contentType set.
   */
  public CDAContentType contentType() {
    return contentType;
  }

  /**
   * Set the contentType of this entry.
   * @param contentType the type to be set.
   */
  protected void setContentType(CDAContentType contentType) {
    this.contentType = contentType;
  }

  /**
   * Create a human readable string of this object.
   * @return a string, containing the id of this content type.
   */
  @Override public String toString() {
    return "CDAEntry{"
        + "id='" + id() + '\''
        + "metadata='" + metadata() + '\''
        + '}';
  }
}
