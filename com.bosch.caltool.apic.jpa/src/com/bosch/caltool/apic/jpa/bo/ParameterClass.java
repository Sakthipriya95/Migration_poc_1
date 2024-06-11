/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

/**
 * 
 */
public enum ParameterClass {
  /**
   * Screw
   */
  SCREW("SCREW", "S"),
  /**
   * Rivet
   */
  RIVET("RIVET", "R"),
  /**
   * Nail
   */
  NAIL("NAIL", "N"),
  /**
   * Icdm-916 new statistical Rivet Statisticak Rivet
   */
  STATRIVET("S-RIVET", "T");


  private final String text;
  private final String shortText;


  ParameterClass(final String text, final String shortText) {
    this.text = text;
    this.shortText = shortText;
  }

  /**
   * @return String
   */
  public String getText() {
    return this.text;
  }

  /**
   * @return String
   */
  public String getShortText() {
    return this.shortText;
  }


  /**
   * Get the object for the given short text
   * 
   * @param dbText DB text
   * @return Parameter class object
   */
  public static ParameterClass getParamClass(final String dbText) {
    for (ParameterClass pClass : ParameterClass.values()) {
      if (pClass.getShortText().equals(dbText)) {
        return pClass;
      }
    }
    return null;
  }

  /**
   * Get the object for the given text
   * 
   * @param text text
   * @return Parameter class object
   */
  public static ParameterClass getParamClassT(final String text) {
    for (ParameterClass pClass : ParameterClass.values()) {
      if (pClass.getText().equals(text)) {
        return pClass;
      }
    }
    return null;
  }


}