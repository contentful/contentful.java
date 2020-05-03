package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Map;

import static com.contentful.java.cda.Constants.LOCALE;

/**
 * This class represents a basic resource, serving as a base class
 * for CDAContentType, CDASpace and, indirectly, for CDAEntry.
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "sys.type",
    defaultImpl = CDAArray.class
    )
@JsonTypeResolver(NestedTypeResolver.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CDAContentType.class, name = "ContentType"),
    @JsonSubTypes.Type(value = CDAArray.class, name = "Error"),
    @JsonSubTypes.Type(value = CDASpace.class, name = "Space"),
    @JsonSubTypes.Type(value = CDAEntry.class, name = "Entry"),
    @JsonSubTypes.Type(value = CDAAsset.class, name = "Asset"),
    @JsonSubTypes.Type(value = CDALocale.class, name = "Locale"),
    @JsonSubTypes.Type(value = CDAArray.class, name = "Array"),
    //@JsonSubTypes.Type(value = SynchronizedSpace.class, name = "Sync"),
    @JsonSubTypes.Type(value = DeletedResource.class, name = "DeletedAsset"),
    @JsonSubTypes.Type(value = DeletedResource.class, name = "DeletedEntry")
})
public abstract class CDAResource implements Serializable {
  private static final long serialVersionUID = -160701290783423915L;
  @SerializedName("sys")
  @JsonProperty("sys")
  Map<String, Object> attrs;

  /**
   * @return a string representing this object's id.
   */
  public String id() {
    return getAttribute("id");
  }

  /**
   * @return the type of this resource.
   */
  public CDAType type() {
    String type = getAttribute("type");
    return CDAType.valueOf(type.toUpperCase(LOCALE));
  }

  /**
   * @return all of the attributes, this object holds.
   */
  public Map<String, Object> attrs() {
    return attrs;
  }

  /**
   * Retrieve a specific attribute of this resource.
   *
   * @param key a string key associated with the value to be retrieved.
   * @param <T> the type of the attribute to be retrieved.
   * @return the actual value of the attribute, or null, if there the key was not found.
   */
  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <T> T getAttribute(String key) {
    return (T) attrs.get(key);
  }


  /**
   * @return a human readable string, representing the object.
   */
  @Override public String toString() {
    return "CDAResource { "
        + "attrs = " + attrs() + ", "
        + "id = " + id() + ", "
        + "type = " + type() + " "
        + "}";
  }
}
