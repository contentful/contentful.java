package com.contentful.java.cda.rich;

import java.util.ArrayList;
import java.util.List;

/**
 * A leaf element of the rich text node graph: Render a given text with the given markings.
 */
public class CDARichText extends CDARichNode {
  private final List<CDARichMark> marks = new ArrayList<>();
  private final CharSequence text;

  /**
   * Create a text with the given marks
   *
   * @param text  the text to be displayed
   * @param marks the marks to be used if any
   */
  public CDARichText(CharSequence text, List<CDARichMark> marks) {
    if (text == null) {
      text = "";
    }

    this.marks.addAll(marks);
    this.text = text;
  }

  /**
   * Create a text with the given marks
   *
   * @param text the text to be displayed
   */
  public CDARichText(String text) {
    this(text, new ArrayList<>());
  }

  /**
   * @return the text of this node.
   */
  public CharSequence getText() {
    return text;
  }

  /**
   * @return the marks of this text.
   *
   * @see CDARichMark.CDARichMarkBold
   */
  public List<CDARichMark> getMarks() {
    return marks;
  }
}
