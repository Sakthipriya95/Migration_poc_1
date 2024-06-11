/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rgo7cob
 */
public class CompliReviewResponse {


  /**
   * list of compli prams in each hex report
   */
  private List<String> compliParams;

  /**
   * list of qssd params in hex report
   */
  private List<String> qssdParams;


  /**
   * ouput of each hex file
   */
  private Map<Long, CompliReviewOutputData> hexFileOutput;


  /**
   * unmapped Bc's
   */
  private Map<String, UnMappedBcsData> unmappedBcs;

  /**
   * Compli report name
   */
  private String reportFileName;

  /**
   * Response Time Stamp
   */
  private String timeStampUTC;

  /**
   * isNONSDOMPVER
   */
  private String isNONSDOMPVER;


  /**
   * Key - Mapped BC name, Value - {@link MappedBcsData}
   */
  private Map<String, MappedBcsData> mappedBcsMap = new HashMap<>();

  /**
   * @return the hexFileOutput
   */
  public Map<Long, CompliReviewOutputData> getHexFileOutput() {
    return this.hexFileOutput;
  }


  /**
   * @return the compliParams
   */
  public List<String> getCompliParams() {
    return this.compliParams;
  }

  /**
   * @param compliParams the compliParams to set
   */
  public void setCompliParams(final List<String> compliParams) {
    if (compliParams != null) {
      this.compliParams = new ArrayList<>(compliParams);
    }
  }


  /**
   * @param hexFileOutput the hexFileOutput to set
   */
  public void setHexFileOutput(final Map<Long, CompliReviewOutputData> hexFileOutput) {
    this.hexFileOutput = hexFileOutput;
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
   * @return the timeStampUTC
   */
  public String getTimeStampUTC() {
    return this.timeStampUTC;
  }


  /**
   * @param timeStampUTC the timeStampUTC to set
   */
  public void setTimeStampUTC(final String timeStampUTC) {
    this.timeStampUTC = timeStampUTC;
  }


  /**
   * @return the mappedBcsMap
   */
  public Map<String, MappedBcsData> getMappedBcsMap() {
    return this.mappedBcsMap;
  }


  /**
   * @param mappedBcsMap the mappedBcsMap to set
   */
  public void setMappedBcsMap(final Map<String, MappedBcsData> mappedBcsMap) {
    this.mappedBcsMap = mappedBcsMap;
  }


  /**
   * @return the unmappedBcs
   */
  public Map<String, UnMappedBcsData> getUnmappedBcs() {
    return this.unmappedBcs;
  }


  /**
   * @param unmappedBcs the unmappedBcs to set
   */
  public void setUnmappedBcs(final Map<String, UnMappedBcsData> unmappedBcs) {
    this.unmappedBcs = unmappedBcs;
  }


  /**
   * @return the qssdParams
   */
  public List<String> getQssdParams() {
    return this.qssdParams;
  }


  /**
   * @param qssdParams the qssdParams to set
   */
  public void setQssdParams(final List<String> qssdParams) {
    this.qssdParams = qssdParams;
  }

  /**
   * @return the isNONSDOMPVER
   */
  public String getIsNONSDOMPVER() {
    return this.isNONSDOMPVER;
  }


  /**
   * @param isNONSDOMPVER the isNONSDOMPVER to set
   */
  public void setIsNONSDOMPVER(final String isNONSDOMPVER) {
    this.isNONSDOMPVER = isNONSDOMPVER;
  }


}
