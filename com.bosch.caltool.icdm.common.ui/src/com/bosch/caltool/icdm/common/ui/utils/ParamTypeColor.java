/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;


import java.awt.Color;


/**
 * ICDM-1657 Class for colors for Review related caldata object types. Color code parameter is not used since label
 * color is same as graph color in the table-graph component.
 * 
 * @author jvi6cob
 */
public enum ParamTypeColor {

  /**
   * Reference Value
   */
  REF_VALUE(CommonUIConstants.REF_VALUE, Color.BLUE, 0),

  /**
   * Parent Reference Value
   */
  PARENT_REF_VALUE(CommonUIConstants.TXT_PARENT_REF_VALUE, Color.RED, 1),

  /**
   * Check Value
   */
  CHECK_VALUE(CommonUIConstants.CHECK_VALUE, Color.GREEN, 2),

  /**
   * Parent Check Value
   */
  PARENT_CHECK_VALUE(CommonUIConstants.TXT_PARENT_CHECK_VALUE, Color.MAGENTA, 7);

  private String type;
  private Color color;
  private int colorCode;

  ParamTypeColor(final String type, final Color color, final int colorCode) {
    this.type = type;
    this.color = color;
    this.colorCode = colorCode;
  }


  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }


  /**
   * @return the color
   */
  public Color getColor() {
    return this.color;
  }


  /**
   * @return the colorCode
   */
  public int getColorCode() {
    return this.colorCode;
  }


}
