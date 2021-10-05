package com.contentful.java.cda;

import com.contentful.java.cda.image.ImageOption;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single asset.
 */
public class CDAAsset extends LocalizedResource {

  private static final long serialVersionUID = -4645571481643616657L;

  /**
   * @return title of this asset.
   */
  public String title() {
    return getField("title");
  }

  /**
   * @return url to the file of this asset.
   */
  public String url() {
    return fileField("url");
  }

  /**
   * Returns a url with the given image manipulation.
   * <p>
   * If the asset is not pointing to an image (as identified by its mimetype) the {@link #url()} is
   * returned. Same happens if the options are empty or non existing, then the url gets returned.
   * <p>
   * In an error case (for instance, using the same option twice), the last option with the same
   * operation will be used.
   *
   * @param options to manipulate the image the returned url will be pointing to.
   * @return an url reflecting all the options given.
   * @throws IllegalArgumentException if no options are given.
   * @throws IllegalArgumentException if no mimetype was set on asset.
   * @throws IllegalArgumentException if mimetype was not an image.
   * @see ImageOption
   * @see #url()
   */
  public String urlForImageWith(ImageOption... options) {
    if (options == null || options.length == 0) {
      throw new IllegalArgumentException("Do not use empty options argument. "
          + "If you want to manipulate the url by hand, please use `CDAAsset.url()` instead.");
    }

    final String mimeType = mimeType();
    if (mimeType == null || !mimeType.startsWith("image")) {
      throw new IllegalStateException("Asset does not have an image mime type.");
    }

    final Map<String, ImageOption> mappedOptions
        = new LinkedHashMap<>(options.length);

    for (final ImageOption option : options) {
      mappedOptions.put(option.getOperation(), option);
    }

    String url = url();
    for (final ImageOption option : mappedOptions.values()) {
      url = option.apply(url);
    }

    return url;
  }

  /**
   * @return mime-type of this asset.
   */
  public String mimeType() {
    return fileField("contentType");
  }

  /**
   * Helper method to extract a field from the {@code file} map.
   *
   * @param key the key who's value to be returned.
   * @param <T> the type of this field.
   * @return field of this file.
   */
  @SuppressWarnings("unchecked")
  public <T> T fileField(String key) {
    T result = null;
    Map<String, Object> file = getField("file");
    if (file != null) {
      result = (T) file.get(key);
    }
    return result;
  }

  /**
   * Return a string, showing the id and title.
   *
   * @return a human readable string
   */
  @Override public String toString() {
    return "CDAAsset{"
        + "id='" + id() + '\''
        + ", title='" + title() + '\''
        + '}';
  }
}
