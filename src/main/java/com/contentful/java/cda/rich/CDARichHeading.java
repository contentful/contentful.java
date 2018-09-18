package com.contentful.java.cda.rich;

/**
 * Defines a headline of the text.
 * <p>
 * Can have an arbitrary level assigned, but useful probably between 1 and 6.
 */
public class CDARichHeading extends CDARichBlock {
  private final int level;

  /**
   * Create a heading block, describing a level elements deep nested heading.
   *
   * @param level a number indicating the level of this heading.
   */
  public CDARichHeading(int level) {
    this.level = level;
  }

  /**
   * @return the current nesting level of this heading.
   */
  public int getLevel() {
    return level;
  }
}
