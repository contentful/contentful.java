package com.contentful.java.cda.rich;

/**
 * How to draw a given text.
 * <p>
 * Subclasses are used for further differentiation.
 */
public class CDARichMark {
  /**
   * A bold mark of a rich text.
   */
  public static class CDARichMarkBold extends CDARichMark {
  }

  /**
   * Declares the text as being displayed in italics.
   */
  public static class CDARichMarkItalic extends CDARichMark {
  }

  /**
   * Marker for making the rich text displayed as underline.
   */
  public static class CDARichMarkUnderline extends CDARichMark {
  }

  /**
   * The text marked by this marker should be represented by Code.
   */
  public static class CDARichMarkCode extends CDARichMark {
  }

  /**
   * Any custom marker for a given rich text.
   */
  public static class CDARichMarkCustom extends CDARichMark {
    private final String type;

    /**
     * Create a custom marker using the given type.
     *
     * @param type which type should this marker have?
     */
    public CDARichMarkCustom(String type) {
      this.type = type;
    }

    /**
     * @return the custom type of the marker.
     */
    public String getType() {
      return type;
    }
  }
}
