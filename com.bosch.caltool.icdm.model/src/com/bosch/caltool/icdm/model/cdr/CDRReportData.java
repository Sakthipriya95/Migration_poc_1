/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author UKT1COB
 */
public class CDRReportData {

  /*
   * PIDC Version - A2L File mapping ID
   */
  private Long pidcA2lId;
  /*
   * Workpackage ID
   */
  private Long a2lWpId;
  /*
   * Responsibility ID
   */
  private Long a2lRespId;
  /*
   * Variant ID
   */
  private Long varId;
  /*
   * maxResults - max number of reviews to be fecthed for param
   */
  private int maxResults;
  /*
   * fetchCheckVal - true if checkvalue has to be fetched
   */
  private boolean fetchCheckVal;
  /*
   * A2L File ID
   */
  private Long a2lFileId;


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the respId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }


  /**
   * @return the varId
   */
  public Long getVarId() {
    return this.varId;
  }


  /**
   * @param varId the varId to set
   */
  public void setVarId(final Long varId) {
    this.varId = varId;
  }


  /**
   * @return the maxResults
   */
  public int getMaxResults() {
    return this.maxResults;
  }


  /**
   * @param maxResults the maxResults to set
   */
  public void setMaxResults(final int maxResults) {
    this.maxResults = maxResults;
  }


  /**
   * @return the fetchCheckVal
   */
  public boolean isToFetchCheckVal() {
    return this.fetchCheckVal;
  }


  /**
   * @param fetchCheckVal the fetchCheckVal to set
   */
  public void setFetchCheckVal(final boolean fetchCheckVal) {
    this.fetchCheckVal = fetchCheckVal;
  }


  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


}
