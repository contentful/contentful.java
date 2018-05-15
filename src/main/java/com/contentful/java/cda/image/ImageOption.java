package com.contentful.java.cda.image;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Locale.getDefault;

/**
 * Options for images to be used when an asset returns a url.
 *
 * @see com.contentful.java.cda.CDAAsset#urlForImageWith(ImageOption...)
 */
public class ImageOption {

  /**
   * Defines the possible formats of an image.
   */
  public enum Format {
    /**
     * Image should be a jpg, useful for {@link #jpegQualityOf(int)}, but might produce artifacts.
     */
    jpg,

    /**
     * Use lossless, but bigger image format: PNG.
     */
    png,

    /**
     * Use lossy, 256 coloured PNG image format.
     */
    png8("png&fl=png8"),

    /**
     * Use googles lossy and lossless format.
     */
    webp;

    final String override;

    /**
     * use default url parameter.
     */
    Format() {
      this.override = null;
    }

    /**
     * Override name, if special values are required.
     */
    Format(String override) {
      this.override = override;
    }

    /**
     * @return the url parameter used for this format.
     */
    public String toUrlParameter() {
      return override == null ? super.name() : override;
    }
  }

  /**
   * Defines the type of resizing possible.
   */
  public enum Resize {
    /**
     * Adds padding so that the generated image has the specified dimensions.
     */
    pad,

    /**
     * Crop a part of the original image to match the specified size.
     */
    crop,

    /**
     * Crop the image to the specified dimensions, upscaling if source image is smaller.
     */
    fill,

    /**
     * Creates a thumbnail from the image based on a focus area, if {@link #focusOn(Focus)}.
     */
    thumb,

    /**
     * Scale the image regardless of the original aspect ratio.
     */
    scale
  }

  /**
   * Defines the area of focus to crop to.
   */
  public enum Focus {
    /**
     * Directional focus areas.
     */
    top,

    /**
     * @see #top
     */
    left,
    /**
     * @see #top
     */
    right,
    /**
     * @see #top
     */
    bottom,

    /**
     * @see #top
     */
    top_left,
    /**
     * @see #top
     */
    top_right,
    /**
     * @see #top
     */
    bottom_left,
    /**
     * @see #top
     */
    bottom_right,

    /**
     * Focus the resizing/cropping on one face detected area.
     */
    face,

    /**
     * Focus the resizing/cropping on multiple face detected areas.
     */
    faces
  }

  /**
   * Retrieve image of specific image format.
   *
   * @param format an {@link Format} to be used for returning the image.
   * @return an image option.
   * @see Format
   */
  public static ImageOption formatOf(Format format) {
    return new ImageOption("fm", format.toUrlParameter());
  }

  /**
   * Define the quality of the jpg image to be returned.
   *
   * @param quality an positive integer between 1 and 100.
   * @return an image option for updating the url.
   * @throws IllegalArgumentException if quality is not between 1 and 100.
   */
  public static ImageOption jpegQualityOf(int quality) {
    if (quality < 1 || quality > 100) {
      throw new IllegalArgumentException("Quality has to be in the range from 1 to 100.");
    }

    return new ImageOption("q", Integer.toString(quality));
  }

  /**
   * Define the width of the image to be represented.
   *
   * @param width width in pixel of the image
   * @return an image option to be used for manipulating an image url.
   * @throws IllegalArgumentException if width is not positive.
   */
  public static ImageOption widthOf(int width) {
    if (width <= 0) {
      throw new IllegalArgumentException("Width has to be positive.");
    }
    return new ImageOption("w", Integer.toString(width));
  }

  /**
   * Set the height of the output image.
   *
   * @param height size of image in pixel.
   * @return an ImageOption changing the height of an output image.
   * @throws IllegalArgumentException if height is not positive.
   */
  public static ImageOption heightOf(int height) {
    if (height <= 0) {
      throw new IllegalArgumentException("Height has to be positive.");
    }
    return new ImageOption("h", Integer.toString(height));
  }

  /**
   * Change the behaviour of resizing.
   * <p>
   * By default the aspect ratio of the image is preserved when defining a size, or a width and
   * height. Using this method, you can define a different resize behaviour.
   *
   * @param resize the behaviour of resizing
   * @return an image option for manipulating a given url.
   */
  public static ImageOption fitOf(Resize resize) {
    return new ImageOption("fit", resize.name());
  }

  /**
   * Changes the focus area when using the fit type of {@link Resize#thumb}.
   *
   * @param focus the area of focus.
   * @return an image option for manipulating a given url.
   */
  public static ImageOption focusOn(Focus focus) {
    return new ImageOption("f", focus.name());
  }

  /**
   * Cut corners round.
   *
   * @param radius of the inner circle in pixel
   * @return an image option for manipulating a given url.
   * @throws IllegalArgumentException if the radius is negative.
   */
  public static ImageOption roundedCornerRadiusOf(float radius) {
    if (radius < 0) {
      throw new IllegalArgumentException("Radius is negative.");
    }

    return new ImageOption("r", Float.toString(radius));
  }

  /**
   * Define a background color.
   * <p>
   * The color value must be a hexadecimal value, i.e. 0xFF0000 means red.
   *
   * @param color the color in hex to be used.
   * @return an image option for manipulating a given url.
   * @throws IllegalArgumentException if the color is less then zero or greater then 0xFFFFFF.
   */
  public static ImageOption backgroundColorOf(int color) {
    if (color < 0 || color > 0xFFFFFF) {
      throw new IllegalArgumentException("Color must be in rgb hex range of 0x0 to 0xFFFFFF.");
    }
    return new ImageOption("bg", "rgb:" + format(Locale.getDefault(), "%06X", color));
  }

  /**
   * Define a background color by its components.
   * <p>
   * Each color component must be a hexadecimal in the range from 0 to 255 inclusively.
   *
   * @param r the red color component in hex to be used.
   * @param g the green color component in hex to be used.
   * @param b the blue color component in hex to be used.
   * @return an image option for manipulating a given url.
   * @throws IllegalArgumentException if a component is less then zero or greater then 255.
   */
  public static ImageOption backgroundColorOf(int r, int g, int b) {
    if (r > 255 || r < 0) {
      throw new IllegalArgumentException("Red component out of range: " + r);
    }
    if (g > 255 || g < 0) {
      throw new IllegalArgumentException("Green component out of range: " + g);
    }
    if (b > 255 || b < 0) {
      throw new IllegalArgumentException("Blue component out of range: " + b);
    }

    return new ImageOption("bg", "rgb:" + format(Locale.getDefault(), "%02X%02X%02X", r, g, b));
  }

  /**
   * Define a background color as black.
   *
   * @return an image option for manipulating a given url.
   */
  public static ImageOption blackBackgroundColor() {
    return backgroundColorOf(0);
  }

  /**
   * Prefix image urls with http.
   * <p>
   * If a protocol (either http or https) was already specified, this one will be taken and
   * this option will not have any effect.
   *
   * @return an image option changing a given url to use insecure http transport.
   */
  public static ImageOption http() {
    return new ImageOption("http", "") {
      @Override public String apply(String url) {
        if (url.startsWith("//")) {
          return "http:" + url;
        } else {
          return url;
        }
      }
    };
  }

  /**
   * Prefix image urls with secure http.
   * <p>
   * If a protocol (either http or https) was already specified, this one will be taken and
   * this option will not have any effect.
   *
   * @return an image option changing a given url to use secure http transport.
   * @throws IllegalArgumentException if {@link #http()} or this method was called before.
   */
  public static ImageOption https() {
    return new ImageOption("https", "") {
      @Override public String apply(String url) {
        if (url.startsWith("//")) {
          return "https:" + url;
        } else {
          return url;
        }
      }
    };
  }

  private final String operation;
  private final String argument;

  private ImageOption(String operation, String argument) {
    this.operation = operation;
    this.argument = argument;
  }

  /**
   * Apply this image option to a url, replacing and updating this url.
   *
   * @param url a url to be handled
   * @return the changed url.
   */
  public String apply(String url) {
    return format(
        getDefault(),
        "%s%s%s=%s",
        url,
        concatenationOperator(url),
        operation,
        argument);
  }

  /**
   * Return the operation of this option
   *
   * @return a string representing this option.
   */
  public String getOperation() {
    return operation;
  }

  private String concatenationOperator(String url) {
    return url.contains("?") ? "&" : "?";
  }
}
