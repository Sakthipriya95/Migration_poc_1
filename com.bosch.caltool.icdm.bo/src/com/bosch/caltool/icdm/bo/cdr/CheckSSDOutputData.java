/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_EXCEPTION_TYPE;

/**
 * @author rgo7cob
 */
public class CheckSSDOutputData {

  private Set<String> generatedCheckSSDFiles = new HashSet<>();

  /**
   * check SSD result map for compliance params
   */
  private Map<String, CheckSSDResultParam> checkSSDCompliParamMap = new ConcurrentHashMap<>();

  /**
   * check SSD report excel
   */
  private String checkSSDReportExcel;


  /**
   * Cdr Exception type
   */
  private CDR_EXCEPTION_TYPE cdrException;

  /**
   * Boolean indicating review having exception
   */
  private boolean reviewHasExp;

  /**
   * Review Exception Object
   */
  private Exception reviewExceptionObj;

  private boolean errorInSSD;
  /**
   * Labels in ssd which are skipped during the review
   */
  private Map<String, String> labelsInSsdMap = new HashMap<>();
  /**
   * Labels without rule
   */
  private Map<String, String> labelsWithoutRulesMap = new HashMap<>();

  private Map<String, String> qssdLabelsWithoutRulesMap = new HashMap<>();


  /**
   * @return the qssdLabelsWithoutRulesMap
   */
  public Map<String, String> getQssdLabelsWithoutRulesMap() {
    return this.qssdLabelsWithoutRulesMap;
  }


  /**
   * @param qssdLabelsWithoutRulesMap the qssdLabelsWithoutRulesMap to set
   */
  public void setQssdLabelsWithoutRulesMap(final Map<String, String> qssdLabelsWithoutRulesMap) {
    this.qssdLabelsWithoutRulesMap = qssdLabelsWithoutRulesMap;
  }

  /**
   * @return the labelsWithoutRulesMap
   */
  public Map<String, String> getLabelsWithoutRulesMap() {
    return this.labelsWithoutRulesMap;
  }


  /**
   * @param labelsWithoutRulesMap the labelsWithoutRulesMap to set
   */
  public void setLabelsWithoutRulesMap(final Map<String, String> labelsWithoutRulesMap) {
    this.labelsWithoutRulesMap = labelsWithoutRulesMap;
  }


  /**
   * @param errorInSSD the errorInSSD to set
   */
  public void setErrorInSSD(final boolean errorInSSD) {
    this.errorInSSD = errorInSSD;
  }


  /**
   * @return the generatedCheckSSDFiles
   */
  public Set<String> getGeneratedCheckSSDFiles() {
    return new HashSet<>(this.generatedCheckSSDFiles);
  }


  /**
   * @param generatedCheckSSDFiles the generatedCheckSSDFiles to set
   */
  public void setGeneratedCheckSSDFiles(final Set<String> generatedCheckSSDFiles) {
    this.generatedCheckSSDFiles = new HashSet<>(generatedCheckSSDFiles);
  }


  /**
   * @return the labelsInSsdMap
   */
  public Map<String, String> getLabelsInSsdMap() {
    return this.labelsInSsdMap;
  }


  /**
   * @param labelsInSsdMap the labelsInSsdMap to set
   */
  public void setLabelsInSsdMap(final Map<String, String> labelsInSsdMap) {
    this.labelsInSsdMap = labelsInSsdMap;
  }


  /**
   * @return the checkSSDCompliParamMap
   */
  public Map<String, CheckSSDResultParam> getCheckSSDCompliParamMap() {
    return this.checkSSDCompliParamMap;
  }


  /**
   * @param checkSSDCompliParamMap the checkSSDCompliParamMap to set
   */
  public void setCheckSSDCompliParamMap(final Map<String, CheckSSDResultParam> checkSSDCompliParamMap) {
    this.checkSSDCompliParamMap = checkSSDCompliParamMap;
  }


  /**
   * @return the cdrException
   */
  public CDR_EXCEPTION_TYPE getCdrException() {
    return this.cdrException;
  }


  /**
   * @param cdrException the cdrException to set
   */
  public void setCdrException(final CDR_EXCEPTION_TYPE cdrException) {
    this.cdrException = cdrException;
  }


  /**
   * @return the checkSSDReportExcel
   */
  public String getCheckSSDReportExcel() {
    return this.checkSSDReportExcel;
  }


  /**
   * @return the reviewHasExp
   */
  public boolean isReviewHasExp() {
    return this.reviewHasExp;
  }


  /**
   * @param checkSSDReportExcel the checkSSDReportExcel to set
   */
  public void setCheckSSDReportExcel(final String checkSSDReportExcel) {
    this.checkSSDReportExcel = checkSSDReportExcel;
  }


  /**
   * @return the reviewExceptionObj
   */
  public Exception getReviewExceptionObj() {
    return this.reviewExceptionObj;
  }


  /**
   * @param reviewExceptionObj the reviewExceptionObj to set
   */
  public void setReviewExceptionObj(final Exception reviewExceptionObj) {
    this.reviewExceptionObj = reviewExceptionObj;
  }

  /**
   * @param reviewHasExp the reviewHasExp to set
   */
  public void setExceptioninReview(final boolean reviewHasExp) {
    this.reviewHasExp = reviewHasExp;
  }


  /**
   * @return
   */
  public boolean getErrorinSSDFile() {

    return this.errorInSSD;
  }


}
