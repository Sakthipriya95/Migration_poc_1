/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.TreeMap;

/**
 * Response class to fetch compliance parameter in an a2l file
 *
 * @author svj7cob
 */
// Task 263282
public class A2lCompliParameterServiceResponse {

  /**
   * A2L file name
   */
  private String fileName;

  /**
   * A2L File size
   */
  private long fileSize;

  /**
   * A2L file's last modified date
   */
  private String lastModifiedDate;

  /**
   * Key : Parameter Name , Value : Compliance class type
   */
  private Map<String, String> a2lCompliParamMap = new TreeMap<>();

  /**
   * Key : Parameter Name , Value : any class type but Q-SSD flag is Y
   */
  private Map<String, String> a2lQSSDParamMap = new TreeMap<>();

  /**
   * total parameter in a2l file
   */
  private int totalParamSize;

  /**
   * total compliance parameter in a2l file
   */
  private int compliParamSize;

  /**
   * total QSSD parameter in a2l file
   */
  private int qssdParamSize;

  /**
   * Compli report name
   */
  private String reportFileName;

  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the fileSize
   */
  public long getFileSize() {
    return this.fileSize;
  }


  /**
   * @param fileSize the fileSize to set
   */
  public void setFileSize(final long fileSize) {
    this.fileSize = fileSize;
  }


  /**
   * @return the lastModifiedDate
   */
  public String getLastModifiedDate() {
    return this.lastModifiedDate;
  }


  /**
   * @param lastModifiedDate the lastModifiedDate to set
   */
  public void setLastModifiedDate(final String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }


  /**
   * @return the a2lCompliParamMap
   */
  public Map<String, String> getA2lCompliParamMap() {
    return this.a2lCompliParamMap;
  }


  /**
   * @param a2lCompliParamMap the a2lCompliParamMap to set
   */
  public void setA2lCompliParamMap(final Map<String, String> a2lCompliParamMap) {
    this.a2lCompliParamMap = a2lCompliParamMap;
  }

  /**
   * @return the totalParamSize
   */
  public int getTotalParamSize() {
    return this.totalParamSize;
  }


  /**
   * @param totalParamSize the totalParamSize to set
   */
  public void setTotalParamSize(final int totalParamSize) {
    this.totalParamSize = totalParamSize;
  }


  /**
   * @return the compliParamSize
   */
  public int getCompliParamSize() {
    return this.compliParamSize;
  }

  /**
   * @param compliParamSize the compliParamSize to set
   */
  public void setCompliParamSize(final int compliParamSize) {
    this.compliParamSize = compliParamSize;
  }


  /**
   * @return the reportFileName
   */
  public String getReportFileName() {
    return this.reportFileName;
  }

  /**
   * @param reportFileName the reportFileName to set
   */
  public void setReportFileName(final String reportFileName) {
    this.reportFileName = reportFileName;
  }


  /**
   * @return the a2lQSSDParamMap
   */
  public Map<String, String> getA2lQSSDParamMap() {
    return this.a2lQSSDParamMap;
  }


  /**
   * @param a2lQSSDParamMap the a2lQSSDParamMap to set
   */
  public void setA2lQSSDParamMap(final Map<String, String> a2lQSSDParamMap) {
    this.a2lQSSDParamMap = a2lQSSDParamMap;
  }


  /**
   * @return the qssdParamSize
   */
  public int getQssdParamSize() {
    return this.qssdParamSize;
  }


  /**
   * @param qssdParamSize the qssdParamSize to set
   */
  public void setQssdParamSize(final int qssdParamSize) {
    this.qssdParamSize = qssdParamSize;
  }
}
