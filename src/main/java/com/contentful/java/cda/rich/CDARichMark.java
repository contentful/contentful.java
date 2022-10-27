package com.contentful.java.cda.rich;

/**
 * How to draw a given text.
 * <p>
 * Subclasses are used for further differentiation.
 */
public class CDARichMark {

  public CDARichMark(String type) {
    this.type = type;
  }

  /**
   * @return the type of the marker.
   */
  public String getType() {
    return type;
  }

  protected final String type;

  /**
   * A bold mark of a rich text.
   */
  public static class CDARichMarkBold extends CDARichMark {

    public CDARichMarkBold() {
      super("bold");
    }

  }

  /**
   * Declares the text as being displayed in italics.
   */
  public static class CDARichMarkItalic extends CDARichMark {

    public CDARichMarkItalic() {
      super("italic");
    }

  }

  /**
   * Marker for making the rich text displayed as underline.
   */
  public static class CDARichMarkUnderline extends CDARichMark {

    public CDARichMarkUnderline() {
      super("underline");
    }

  }

  /**
   * The text marked by this marker should be represented by Code.
   */
  public static class CDARichMarkCode extends CDARichMark {

    public CDARichMarkCode() {
      super("code");
    }

  }

  /**
   * Any custom marker for a given rich text.
   */
  public static class CDARichMarkCustom extends CDARichMark {
    /**
     * Create a custom marker using the given type.
     *
     * @param type which type should this marker have?
     */
    public CDARichMarkCustom(String type) {
      super(type);
    }
  }
}
