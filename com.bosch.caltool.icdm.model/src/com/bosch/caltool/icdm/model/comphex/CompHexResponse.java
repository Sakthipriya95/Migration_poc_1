/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comphex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * The Class CompHexResponse.
 *
 * @author gge6cob
 */
public class CompHexResponse {

  private String hexFileName;

  private String srcHexFilePath;

  private String vcdmDst;

  /** The cdr report. */
  private CdrReport cdrReport;

  /** Holds folder name where results are stored */
  private String referenceId = "";

  /** The comp hex statistics. */
  private CompHexStatistics compHexStatistics;

  /** list of CompHEXWtihCDFParam. */
  private SortedSet<CompHexWithCDFParam> compHexResult;

  /** The is compli check failed. */
  private boolean isCompliCheckFailed = false;

  /** The is QSSD check failed. */
  private boolean isQSSDCheckFailed;


  /** The error msg set. */
  private Set<String> errorMsgSet = new HashSet<>();

  /**
   * SSD rules for compliance params
   */
  private Map<String, List<ReviewRule>> ssdRulesForCompliance = new HashMap<>();


  /**
   * SSD rules for QSSD params
   */
  private Map<String, List<ReviewRule>> ssdRulesForQssd = new HashMap<>();


  private boolean processCompleted;

  /**
   * @param isCompliCheckFailed the isCompliCheckFailed to set
   */
  public void setCompliCheckFailed(final boolean isCompliCheckFailed) {
    this.isCompliCheckFailed = isCompliCheckFailed;
  }


  /**
   * Gets the comp hex statistics.
   *
   * @return the compHexStatistics
   */
  public CompHexStatistics getCompHexStatistics() {
    return this.compHexStatistics;
  }


  /**
   * Sets the comp hex statistics.
   *
   * @param compHexStatistics the compHexStatistics to set
   */
  public void setCompHexStatistics(final CompHexStatistics compHexStatistics) {
    this.compHexStatistics = compHexStatistics;
  }


  /**
   * Gets the cdr report.
   *
   * @return the cdrReport
   */
  public CdrReport getCdrReport() {
    return this.cdrReport;
  }


  /**
   * Sets the cdr report.
   *
   * @param cdrReport the cdrReport to set
   */
  public void setCdrReport(final CdrReport cdrReport) {
    this.cdrReport = cdrReport;
  }


  /**
   * @return the compHexResult
   */
  public SortedSet<CompHexWithCDFParam> getCompHexResult() {
    return this.compHexResult;
  }


  /**
   * @param compHexResult the compHexResult to set
   */
  public void setCompHexResult(final SortedSet<CompHexWithCDFParam> compHexResult) {
    this.compHexResult = compHexResult == null ? null : new TreeSet<>(compHexResult);
  }


  /**
   * @return the referenceId
   */
  public String getReferenceId() {
    return this.referenceId;
  }


  /**
   * @param referenceId the referenceId to set
   */
  public void setReferenceId(final String referenceId) {
    this.referenceId = referenceId;
  }


  /**
   * @return the isCompliCheckFailed
   */
  public boolean isCompliCheckFailed() {
    return this.isCompliCheckFailed;
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
   * @return the vcdmDst
   */
  public String getVcdmDst() {
    return this.vcdmDst;
  }


  /**
   * @param vcdmDst the vcdmDst to set
   */
  public void setVcdmDst(final String vcdmDst) {
    this.vcdmDst = vcdmDst;
  }


  /**
   * @return the errorMsgSet
   */
  public Set<String> getErrorMsgSet() {
    return this.errorMsgSet;
  }


  /**
   * @param errorMsgSet the errorMsgSet to set
   */
  public void setErrorMsgSet(final Set<String> errorMsgSet) {
    this.errorMsgSet = errorMsgSet == null ? null : new HashSet<>(errorMsgSet);
  }


  /**
   * @return the ssdRulesForCompliance
   */
  public Map<String, List<ReviewRule>> getSsdRulesForCompliance() {
    return this.ssdRulesForCompliance;
  }


  /**
   * @param ssdRulesForCompliance the ssdRulesForCompliance to set
   */
  public void setSsdRulesForCompliance(final Map<String, List<ReviewRule>> ssdRulesForCompliance) {
    this.ssdRulesForCompliance = ssdRulesForCompliance;
  }


  /**
   * @return the processCompleted
   */
  public boolean isProcessCompleted() {
    return this.processCompleted;
  }


  /**
   * @param processCompleted the processCompleted to set
   */
  public void setProcessCompleted(final boolean processCompleted) {
    this.processCompleted = processCompleted;
  }

  /**
   * @return the isQSSDCheckFailed
   */
  public boolean isQSSDCheckFailed() {
    return this.isQSSDCheckFailed;
  }


  /**
   * @param isQSSDCheckFailed the isQSSDCheckFailed to set
   */
  public void setQSSDCheckFailed(final boolean isQSSDCheckFailed) {
    this.isQSSDCheckFailed = isQSSDCheckFailed;
  }
  
  /**
   * @return the ssdRulesForQssd
   */
  public Map<String, List<ReviewRule>> getSsdRulesForQssd() {
    return this.ssdRulesForQssd;
  }


  /**
   * @param ssdRulesForQssd the ssdRulesForQssd to set
   */
  public void setSsdRulesForQssd(final Map<String, List<ReviewRule>> ssdRulesForQssd) {
    this.ssdRulesForQssd = ssdRulesForQssd;
  }
}
