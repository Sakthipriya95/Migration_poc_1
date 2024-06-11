/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author svj7cob
 */
public class HexFileSelectionRowProvider {

  private int index;

  private final String hexFilePath;

  private final String hexFileName;

  private final String pidcVersName;

  private final Long pidcVersId;

  private final String pidcVariantName;

  private final Long pidcVariantId;

  /**
   * @param hexFilePath
   * @param hexFileName
   * @param pidcVersName
   * @param pidcVersId
   * @param pidcVariantName
   * @param pidcVariantId
   */
  public HexFileSelectionRowProvider(final String hexFilePath, final String hexFileName, final String pidcVersName,
      final Long pidcVersId, final String pidcVariantName, final Long pidcVariantId) {
    super();
    this.hexFilePath = hexFilePath;
    this.hexFileName = hexFileName;
    this.pidcVersName = pidcVersName;
    this.pidcVersId = pidcVersId;
    this.pidcVariantName = pidcVariantName;
    this.pidcVariantId = pidcVariantId;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return this.index;
  }


  /**
   * @return the hexFilePath
   */
  public String getHexFilePath() {
    return this.hexFilePath;
  }


  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }


  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param index the index to set
   */
  public void setIndex(final int index) {
    this.index = index;
  }

  /**
   * @return custom display
   */
  public String getPidcElementDisplay() {
    if (0 == getIndex()) {
      return getPidcVersName();
    }
    if (CommonUtils.isNotEmptyString(getPidcVariantName())) {
      return getPidcVariantName() + " [ PIDC Version : " + getPidcVersName() + " ]";
    }
    if (CommonUtils.isNotEmptyString(getPidcVersName())) {
      return getPidcVersName();
    }
    return "";
  }

  /**
   * Key for validation
   * 
   * @return
   */
  public String getPidcElementKey() {
    String pidcVersIdKey = null == getPidcVersId() ? "" : String.valueOf(getPidcVersId());
    String pidcVarIdKey = null == getPidcVariantId() ? "" : String.valueOf(getPidcVariantId());
    return pidcVersIdKey + pidcVarIdKey + getHexFileName();
  }
}
