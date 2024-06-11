/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import com.bosch.caltool.icdm.common.exception.DataRuntimeException;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * Maturity level of the review rule, as required in iCDM
 * 
 * @author dmo5cob
 */
public enum RuleMaturityLevel {
  /**
   * None
   */
  NONE("none (0%)", null, new String[] { "changed", "" }),
  /**
   * Start
   */
  START("prelim-calibrated (25%)", "START", new String[] { "prelimCalibrated" }),
  /**
   * Standard
   */
  STANDARD("calibrated (50%)", "STANDARD", new String[] { "calibrated" }),
  /**
   * Fixed
   */
  FIXED("checked (75%)", "FIXED", new String[] { "checked", "completed" });


  /**
   * Text in iCDM
   */
  private final String icdmMaturityLevel;
  /**
   * Text in SSD
   */
  private final String ssdMaturityLevel;
  /**
   * Text in data files(e.g. CDFx)
   */
  private final String[] importText;

  /**
   * Constructor
   * 
   * @param icdmText iCDM text
   * @param ssdText ssd text
   * @param importText file texts
   */
  RuleMaturityLevel(final String icdmText, final String ssdText, final String[] importText) {
    this.icdmMaturityLevel = icdmText;
    this.ssdMaturityLevel = ssdText;
    this.importText = importText.clone();
  }

  /**
   * Get enum constant from SSD text
   * 
   * @param ssdMaturityLevel SSD Maturity Level
   * @return ICDM Maturity Level
   */
  public static RuleMaturityLevel getIcdmMaturityLvlEnumForSsdText(final String ssdMaturityLevel) {
    String inputSsdMaturity = CommonUtils.checkNull(ssdMaturityLevel);
    String enumSsdMaturity;
    for (RuleMaturityLevel level : RuleMaturityLevel.values()) {
      enumSsdMaturity = CommonUtils.checkNull(level.getSSDMaturityLevel());
      if (CommonUtils.isEqualIgnoreCase(inputSsdMaturity, enumSsdMaturity)) {
        return level;
      }
    }
    throw new DataRuntimeException("Invalid SSD maturity level " + ssdMaturityLevel);
  }

  // ICDM-1300
  /**
   * Get enum constant from ICDM text
   * 
   * @param icdmMaturityLevel ICDM Maturity Level text
   * @return ICDM Maturity Level
   */
  public static RuleMaturityLevel getType(final String icdmMaturityLevel) {
    for (RuleMaturityLevel level : RuleMaturityLevel.values()) {
      if (CommonUtils.isEqualIgnoreCase(level.icdmMaturityLevel, icdmMaturityLevel)) {
        return level;
      }
    }
    throw new DataRuntimeException("Invalid ICDM maturity level text " + icdmMaturityLevel);
  }

  /**
   * Get iCDM text from SSD text
   * 
   * @param ssdMaturityLevel SSD Maturity Level
   * @return ICDM Maturity Level text
   */
  public static String getIcdmMaturityLevelText(final String ssdMaturityLevel) {
    return getIcdmMaturityLvlEnumForSsdText(ssdMaturityLevel).getICDMMaturityLevel();
  }

  /**
   * Get the SSD text from iCDM text
   * 
   * @param icdmMaturity ICDM Maturity Level
   * @return SSD Maturity Level
   */
  public static String getSsdMaturityLevelText(final String icdmMaturity) {
    for (RuleMaturityLevel level : RuleMaturityLevel.values()) {
      if (CommonUtils.isEqualIgnoreCase(icdmMaturity, level.getICDMMaturityLevel())) {
        return level.getSSDMaturityLevel();
      }
    }
    throw new DataRuntimeException("Invalid ICDM maturity level " + icdmMaturity);
  }

  /**
   * Get iCDM text from import file text
   * 
   * @param text icdm maturity level ( This text is used in .cdfx files)
   * @return String
   */
  public static String getIcdmMaturityLvlFromImportFileTxt(final String text) {
    for (RuleMaturityLevel level : RuleMaturityLevel.values()) {
      for (String str : level.importText) {
        if (str.equals(text)) {
          return level.getICDMMaturityLevel();
        }
        else if ((null == text) || text.equals(level.ssdMaturityLevel)) {
          return level.importText[0];
        }
      }
    }
    return "";
  }

  /**
   * Get iCDM maturity level enum from import file text
   * 
   * @param text icdm maturity level ( This text is used in .cdfx files)
   * @return String
   */
  public static RuleMaturityLevel getICDMMaturityLevelEnum(final String text) {
    for (RuleMaturityLevel level : RuleMaturityLevel.values()) {
      for (String str : level.importText) {
        if (str.equals(text)) {
          return level;
        }
      }
    }
    return NONE;
  }

  /**
   * @return ICDM Maturity Level text
   */
  public String getICDMMaturityLevel() {
    return this.icdmMaturityLevel;
  }

  /**
   * @return SSD Maturity Level text
   */
  public String getSSDMaturityLevel() {
    return this.ssdMaturityLevel;
  }

}