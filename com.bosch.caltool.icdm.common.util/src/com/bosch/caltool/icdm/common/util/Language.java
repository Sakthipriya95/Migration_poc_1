/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;


/**
 * Supported languages in iCDM
 *
 * @author DMO5COB
 */
public enum Language {

                      /**
                       * English
                       */
                      ENGLISH("English"),
                      /**
                       * German
                       */
                      GERMAN("German");

  /**
   * Text represenation of the language
   */
  private String text;

  Language(final String text) {
    this.text = text;
  }

  /**
   * @return the text representation of the language
   */
  public String getText() {
    return this.text;
  }

  /**
   * Find the enum value from the input text
   *
   * @param text text represenation of the language
   * @return enum value
   */
  public static Language getLanguage(final String text) {
    for (Language l : Language.values()) {
      if (l.text.equalsIgnoreCase(text)) {
        return l;
      }
    }
    return ENGLISH;
  }

}
