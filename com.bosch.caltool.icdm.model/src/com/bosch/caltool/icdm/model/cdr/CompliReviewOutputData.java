/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author rgo7cob
 */
public class CompliReviewOutputData implements Serializable {


  /**
   *
   */
  private static final long serialVersionUID = 4688106715380566978L;

  /**
   * Key- param name and value is Result(would be derived from enum in cdr constant)
   */
  private Map<String, String> compliResult = new TreeMap<>();

  /**
   * Key- param name and value is Result(would be derived from enum in cdr constant)
   */
  private Map<String, String> qssdResult = new TreeMap<>();

  /**
   * check ssd result files
   */
  private List<String> checkSSDfileNames = new ArrayList<>();

  /**
   * ssd file ouptut
   */
  private String ssdFileName;

  private String hexfileName;
  /**
   * Release Id
   */
  private Long releaseId;
  /**
   * Hex file checksum value calculated using CRC32
   */
  private String hexChecksum;

  /**
   * compli count
   */
  private int compliCount;

  /**
   * compli pass count
   */
  private int compliPassCount;

  /**
   * total fail count
   */
  private int totalFailCount;
  /**
   * no rule count
   */
  private int noRuleCount;
  /**
   * cssd fail cint
   */
  private int cssdFailCount;
  /**
   * ssd2RvFail Count
   */
  private int ssd2RvFailCount;

  /**
   * total QSSD param count
   */
  private int qssdCount;

  /**
   * QSSD pass count
   */
  private int qssdPassCount;

  /**
   * QSSD params with no rule
   */
  private int qssdNoRuleCount;

  /**
   * QSSD total failure count - qssdFail + qssdNoRule count
   */
  private int qssdTotalFailCount;

  /**
   * QSSD fail count
   */
  private int qssdFailCount;


  /**
   * @return the checkSSDfileNames
   */
  public List<String> getCheckSSDfileNames() {
    return this.checkSSDfileNames;
  }


  /**
   * @param checkSSDfileNames the checkSSDfileNames to set
   */
  public void setCheckSSDfileNames(final List<String> checkSSDfileNames) {
    if ((checkSSDfileNames != null) && !checkSSDfileNames.isEmpty()) {
      this.checkSSDfileNames = new ArrayList<>(checkSSDfileNames);
    }
  }


  /**
   * @return the ssdFileName
   */
  public String getSsdFileName() {
    return this.ssdFileName;
  }


  /**
   * @param ssdFileName the ssdFileName to set
   */
  public void setSsdFileName(final String ssdFileName) {
    this.ssdFileName = ssdFileName;
  }


  /**
   * @return the hexfileName
   */
  public String getHexfileName() {
    return this.hexfileName;
  }


  /**
   * @param hexfileName the hexfileName to set
   */
  public void setHexfileName(final String hexfileName) {
    this.hexfileName = hexfileName;
  }


  /**
   * @return the compliResult
   */
  public Map<String, String> getCompliResult() {
    return this.compliResult;
  }


  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final Map<String, String> compliResult) {
    this.compliResult = compliResult;
  }


  /**
   * @return the compliCount
   */
  public int getCompliCount() {
    return this.compliCount;
  }


  /**
   * @param compliCount the compliCount to set
   */
  public void setCompliCount(final int compliCount) {
    this.compliCount = compliCount;
  }


  /**
   * @return the compliPassCount
   */
  public int getCompliPassCount() {
    return this.compliPassCount;
  }


  /**
   * @param compliPassCount the compliPassCount to set
   */
  public void setCompliPassCount(final int compliPassCount) {
    this.compliPassCount = compliPassCount;
  }


  /**
   * @return the totalFailCount
   */
  public int getTotalFailCount() {
    return this.totalFailCount;
  }


  /**
   * @param totalFailCount the totalFailCount to set
   */
  public void setTotalFailCount(final int totalFailCount) {
    this.totalFailCount = totalFailCount;
  }


  /**
   * @return the noRuleCount
   */
  public int getNoRuleCount() {
    return this.noRuleCount;
  }


  /**
   * @param noRuleCount the noRuleCount to set
   */
  public void setNoRuleCount(final int noRuleCount) {
    this.noRuleCount = noRuleCount;
  }


  /**
   * @return the cssdFailCount
   */
  public int getCssdFailCount() {
    return this.cssdFailCount;
  }


  /**
   * @param cssdFailCount the cssdFailCount to set
   */
  public void setCssdFailCount(final int cssdFailCount) {
    this.cssdFailCount = cssdFailCount;
  }


  /**
   * @return the ssd2RvFailCount
   */
  public int getSsd2RvFailCount() {
    return this.ssd2RvFailCount;
  }


  /**
   * @param ssd2RvFailCount the ssd2RvFailCount to set
   */
  public void setSsd2RvFailCount(final int ssd2RvFailCount) {
    this.ssd2RvFailCount = ssd2RvFailCount;
  }


  /**
   * @return the releaseId
   */
  public Long getReleaseId() {
    return this.releaseId;
  }


  /**
   * @param releaseId the releaseId to set
   */
  public void setReleaseId(final Long releaseId) {
    this.releaseId = releaseId;
  }


  /**
   * @return the checkSum
   */
  public String getHexChecksum() {
    return this.hexChecksum;
  }


  /**
   * @param checkSum the checkSum to set
   */
  public void setHexChecksum(final String checkSum) {
    this.hexChecksum = checkSum;
  }


  /**
   * @return the qssdResult
   */
  public Map<String, String> getQssdResult() {
    return this.qssdResult;
  }


  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final Map<String, String> qssdResult) {
    this.qssdResult = qssdResult;
  }


  /**
   * @return the qssdCount
   */
  public int getQssdCount() {
    return this.qssdCount;
  }


  /**
   * @param qssdCount the qssdCount to set
   */
  public void setQssdCount(final int qssdCount) {
    this.qssdCount = qssdCount;
  }


  /**
   * @return the qssdPassCount
   */
  public int getQssdPassCount() {
    return this.qssdPassCount;
  }


  /**
   * @param qssdPassCount the qssdPassCount to set
   */
  public void setQssdPassCount(final int qssdPassCount) {
    this.qssdPassCount = qssdPassCount;
  }


  /**
   * @return the qssdNoRuleCount
   */
  public int getQssdNoRuleCount() {
    return this.qssdNoRuleCount;
  }


  /**
   * @param qssdNoRuleCount the qssdNoRuleCount to set
   */
  public void setQssdNoRuleCount(final int qssdNoRuleCount) {
    this.qssdNoRuleCount = qssdNoRuleCount;
  }


  /**
   * @return the qssdTotalFailCount
   */
  public int getQssdTotalFailCount() {
    return this.qssdTotalFailCount;
  }


  /**
   * @param qssdTotalFailCount the qssdTotalFailCount to set
   */
  public void setQssdTotalFailCount(final int qssdTotalFailCount) {
    this.qssdTotalFailCount = qssdTotalFailCount;
  }


  /**
   * @return the qssdFailCount
   */
  public int getQssdFailCount() {
    return this.qssdFailCount;
  }


  /**
   * @param qssdFailCount the qssdFailCount to set
   */
  public void setQssdFailCount(final int qssdFailCount) {
    this.qssdFailCount = qssdFailCount;
  }


}
