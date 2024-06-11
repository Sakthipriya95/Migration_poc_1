package com.bosch.caltool.icdm.common.util;

/**
 * This represents the mode in which the application window should be opened on launch.
 */
public enum WindowOpenMode {
                            /**
                             * Minimised Mode
                             */
                            MIN("MIN"),
                            /**
                             * Minimised Mode
                             */
                            MAX("MAX");

  /**
   * Text represenation of the mode
   */
  private String text;

  WindowOpenMode(final String text) {
    this.text = text;
  }

  /**
   * @return the text representation of the language
   */
  public String getText() {
    return this.text;
  }
}