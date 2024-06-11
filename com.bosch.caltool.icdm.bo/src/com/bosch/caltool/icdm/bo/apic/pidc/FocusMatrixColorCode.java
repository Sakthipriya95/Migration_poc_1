/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;


/**
 * Supported color codes in focus matrix
 * 
 * @author DMR1COB
 */
public enum FocusMatrixColorCode {

  /**
   * Red
   */
  RED("RED", FocusMatrixColorCode.ORDER_RED, FocusMatrixColorCode.KEY_RED, "Red"),
  /**
   * Orange
   */
  ORANGE("ORG", FocusMatrixColorCode.ORDER_ORANGE, FocusMatrixColorCode.KEY_ORANGE, "Orange"),
  /**
   * Green
   */
  GREEN("GRN", FocusMatrixColorCode.ORDER_GREEN, FocusMatrixColorCode.KEY_GREEN, "Green"),
  /**
   * Yellow
   */
  YELLOW("YLW", FocusMatrixColorCode.ORDER_YELLOW, FocusMatrixColorCode.KEY_YELLOW, "Yellow"),
  /**
   * No color defined yet
   */
  NOT_DEFINED("", FocusMatrixColorCode.ORDER_NOT_DEFINED, FocusMatrixColorCode.KEY_NO_COLOR, "");

  /**
   * integer to specify order for WHITE color
   */
  private static final int ORDER_NOT_DEFINED = 0;
  /**
   * integer to specify order for GREEN color
   */
  private static final int ORDER_GREEN = 1;
  /**
   * integer to specify order for YELLOW color
   */
  private static final int ORDER_YELLOW = 2;
  /**
   * integer to specify order for RED color
   */
  private static final int ORDER_ORANGE = 3;
  /**
   * integer to specify order for RED color
   */
  private static final int ORDER_RED = 4;

  /**
   * Text represenation of the language
   */
  private String color;

  private int order;

  private int key_shortcut;
  private String displayColorTxt;

  FocusMatrixColorCode(final String color, final int order, final int key_shortcut, final String displayColorTxt) {
    this.color = color;
    this.order = order;
    this.key_shortcut = key_shortcut;
    this.displayColorTxt = displayColorTxt;
  }

  /**
   * @return the text representation of the language
   */
  public String getColor() {
    return this.color;
  }


  /**
   * @return order of the color code
   */
  public int getOrder() {
    return this.order;
  }

  /**
   * @return the displayColorTxt used in the Nat olumn Filter
   */
  public String getDisplayColorTxt() {
    return this.displayColorTxt;
  }

  /**
   * Find the enum value from the input text
   * 
   * @param color text represenation of the language
   * @return enum value
   */
  public static FocusMatrixColorCode getColor(final String color) {
    for (FocusMatrixColorCode clr : FocusMatrixColorCode.values()) {
      if (clr.color.equals(color)) {
        return clr;
      }
    }
    return NOT_DEFINED;
  }

  /**
   * @param key_shortcut char of keyboard shortcut
   * @return enum value
   */
  public static FocusMatrixColorCode getColor(final int key_shortcut) {
    for (FocusMatrixColorCode clr : FocusMatrixColorCode.values()) {
      if (clr.key_shortcut == key_shortcut) {
        return clr;
      }
    }
    return null;// null should be returned in case of invalid colors
  }

  /**
   * key to set red color code
   */
  private static final int KEY_RED = 'r';

  /**
   * key to set yellow color code
   */
  private static final int KEY_YELLOW = 'y';

  /**
   * key to set no color
   */
  private static final int KEY_NO_COLOR = 'w';

  /**
   * key to set green color code
   */
  private static final int KEY_GREEN = 'g';

  /**
   * key to set orange color code
   */
  private static final int KEY_ORANGE = 'o';


}
