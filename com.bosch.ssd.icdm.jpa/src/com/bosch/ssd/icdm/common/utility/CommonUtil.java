/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.common.utility;

import java.util.Objects;

import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;

/**
 * Commonn Util Class for methods used across different service modules
 *
 * @author SSN9COB
 */
public final class CommonUtil {

  /**
   * Constant for MAP_INDIVIDUAL
   */
  private static final String MAP_INDIVIDUAL = "MAP_INDIVIDUAL";
  /**
   * Constant for MAP_GROUPED
   */
  private static final String MAP_GROUPED = "MAP_GROUPED";
  /**
   * Constant for MAP_FIXED
   */
  private static final String MAP_FIXED = "MAP_FIXED";
  /**
   * Constant for CURVE_INDIVIDUAL
   */
  private static final String CURVE_INDIVIDUAL = "CURVE_INDIVIDUAL";
  /**
   * Constant for CURVE_GROUPED
   */
  private static final String CURVE_GROUPED = "CURVE_GROUPED";
  /**
   * Constant for CURVE_FIXED
   */
  private static final String CURVE_FIXED = "CURVE_FIXED";
  /**
   * Constant for AXIS_VALUES
   */
  private static final String AXIS_VALUES = "AXIS_VALUES";
  /**
   * Constant for VALUE_BLOCK
   */
  private static final String VALUE_BLOCK = "VALUE_BLOCK";


  private CommonUtil() {
    // default private constructor
  }


  /**
   * To get value type from category
   *
   * @param category - category from ssd
   * @return - value type for icdm null- if can not be infered
   */
  public static String getValueTypeFromCategory(final String category) {
    // switch through the category to get value type
    switch (category) {
      // Value Type
      case SSDiCDMInterfaceConstants.VALUE:
        return SSDiCDMInterfaceConstants.VALUE;
      // Value Block Type
      case VALUE_BLOCK:
        return SSDiCDMInterfaceConstants.VAL_BLK;
      // Ascii Type
      case SSDiCDMInterfaceConstants.ASCII:
        return SSDiCDMInterfaceConstants.ASCII;
      // Axis Values Type
      case AXIS_VALUES:
        return SSDiCDMInterfaceConstants.AXIS_PTS;
      // CURVE_FIXED, CURVE_GROUPED, CURVE_INDIVIDUAL Type
      case CURVE_FIXED:
      case CURVE_GROUPED:
      case CURVE_INDIVIDUAL:
        return SSDiCDMInterfaceConstants.CURVE;
      // Map_Fixed, Map_grouped, Map_individual Type
      case MAP_FIXED:
      case MAP_GROUPED:
      case MAP_INDIVIDUAL:
        return SSDiCDMInterfaceConstants.MAP;
      default:
        return null;
    }
  }


  /**
   * @param value String
   * @return isEmpty
   */
  public static boolean isEmptyString(final String value) {
    return Objects.isNull(value) || value.isEmpty();
  }
}
