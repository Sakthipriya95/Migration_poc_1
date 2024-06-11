/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;


/**
 * @author apl1cob
 */
public enum SSDCase {
                     /**
                      * SSD Case
                      */
                     SSD("SSD"),
                     /**
                      * MFT Case
                      */
                     MFT("MFT"),
                     /**
                      * Cal4Comp Case
                      */
                     CAL4COMP("Cal4Comp"),
                     /**
                      * Review case
                      */
                     REVIEW("Review"),
                     /**
                      * App-Chk case
                      */
                     APPCHK("App-Chk"),
                     /**
                      * No SSD Case
                      */
                     NOSSD("No-SSD,No-Check"),
                     /**
                      * CSSDUse case
                      */
                     CSSD("C-SSD"),
                     /**
                      * SSD2RV Use case
                      */
                     SSD2RV("SSD2Rv"),
                     /**
                      * QSSD Use case - ALM-526562
                      */
                     QSSD("Q-SSD");


  private final String character;

  private SSDCase(final String character) {
    this.character = character;
  }

  /**
   * Returns the DB value for these use cases
   *
   * @return db String
   */
  public String getCharacter() {
    return this.character;
  }

  /**
   * Decode the string to the SSDCase Enum
   *
   * @param literal character
   * @return enumeration
   */
  public static SSDCase getType(final String literal) {
    for (SSDCase ssdcase : SSDCase.values()) {
      if (ssdcase.character.equalsIgnoreCase(literal)) {
        return ssdcase;
      }
    }

    return null;
  }
}
