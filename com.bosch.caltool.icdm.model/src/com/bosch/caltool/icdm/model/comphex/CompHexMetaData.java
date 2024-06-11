/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comphex;

import com.bosch.caltool.icdm.model.cdr.review.PidcData;

/**
 * @author gge6cob
 */
public class CompHexMetaData {

  private PidcData pidcData;
  private String vcdmDstSource;
  private long vcdmDstVersId;
  private String hexFileName;
  private String srcHexFilePath;
  private boolean isHexFromVcdm;

  /**
   * @return the pidcData
   */
  public PidcData getPidcData() {
    return this.pidcData;
  }

  /**
   * @param pidcData the pidcData to set
   */
  public void setPidcData(final PidcData pidcData) {
    this.pidcData = pidcData;
  }

  /**
   * @return the vcdmDstSource
   */
  public String getVcdmDstSource() {
    return this.vcdmDstSource;
  }

  /**
   * @param vcdmDstSource the vcdmDstSource to set
   */
  public void setVcdmDstSource(final String vcdmDstSource) {
    this.vcdmDstSource = vcdmDstSource;
  }

  /**
   * @return the vcdmDstVersId
   */
  public long getVcdmDstVersId() {
    return this.vcdmDstVersId;
  }

  /**
   * @param vcdmDstVersId the vcdmDstVersId to set
   */
  public void setVcdmDstVersId(final long vcdmDstVersId) {
    this.vcdmDstVersId = vcdmDstVersId;
  }

  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }

  /**
   * @param hexFileName the hexFileName to set
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }

  /**
   * @return the srcHexFilePath
   */
  public String getSrcHexFilePath() {
    return this.srcHexFilePath;
  }

  /**
   * @param srcHexFilePath the srcHexFilePath to set
   */
  public void setSrcHexFilePath(final String srcHexFilePath) {
    this.srcHexFilePath = srcHexFilePath;
  }


  /**
   * @return the isHexFromVcdm
   */
  public boolean isHexFromVcdm() {
    return this.isHexFromVcdm;
  }


  /**
   * @param isHexFromVcdm the isHexFromVcdm to set
   */
  public void setHexFromVcdm(final boolean isHexFromVcdm) {
    this.isHexFromVcdm = isHexFromVcdm;
  }

}

