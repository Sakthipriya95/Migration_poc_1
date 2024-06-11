/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author NDV4KOR
 */
public class CompliCheckSSDInputModel implements Serializable {

  private ExcelReportTypeEnum datafileoption;

  private boolean predecessorCheck;

  private String hexFileName;

  private Map<Long, Long> hexFilePidcElement = new TreeMap<>();


  /**
   * @return the hexFilePidcElement
   */
  public Map<Long, Long> getHexFilePidcElement() {
    return this.hexFilePidcElement;
  }


  /**
   * @param hexFilePidcElement the hexFilePidcElement to set
   */
  public void setHexFilePidcElement(final Map<Long, Long> hexFilePidcElement) {
    this.hexFilePidcElement = hexFilePidcElement;
  }


  private Long pidcA2l;

  private Long variantId;


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the pidcA2l
   */
  public Long getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final Long pidcA2l) {
    this.pidcA2l = pidcA2l;
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
   * @return the predecessorCheck
   */
  public boolean isPredecessorCheck() {
    return this.predecessorCheck;
  }


  /**
   * @param predecessorCheck the predecessorCheck to set
   */
  public void setPredecessorCheck(final boolean predecessorCheck) {
    this.predecessorCheck = predecessorCheck;
  }


  /**
   * @return the datafileoption
   */
  public ExcelReportTypeEnum getDatafileoption() {
    return this.datafileoption;
  }


  /**
   * @param datafileoption the datafileoption to set
   */
  public void setDatafileoption(final ExcelReportTypeEnum datafileoption) {
    this.datafileoption = datafileoption;
  }
}
