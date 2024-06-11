/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comphex;


/**
 * @author gge6cob
 */
public class CompHexStatistics {

  /** Statistics for header. */
  private int statTotalParamInA2L = 0;

  /** The stat filtered param. */
  private int statFilteredParam = 0;

  /** The stat compli param in A 2 L. */
  private int statCompliParamInA2L = 0;

  /** The stat param reviewed. */
  private int statParamReviewed = 0;

  /** The stat filtered param rvwd. */
  private int statFilteredParamRvwd = 0;

  /** The stat param rvwd not equal. */
  private int statParamRvwdNotEqual = 0;

  /** The stat param rvwd not equal. */
  private int statParamRvwdEqual = 0;

  /** The stat filtered param rvwd not equal. */
  private int statFilteredParamRvwdNotEqual = 0;

  /** The stat compli cssd failed. */
  private int statCompliCssdFailed = 0;

  /** The stat compli SSD rv failed. */
  private int statCompliSSDRvFailed = 0;

  /** The stat compli no rule failed. */
  private int statCompliNoRuleFailed = 0;

  /** The stat compli param passed. */
  private int statCompliParamPassed = 0;

  /** The stat cQ_SSD param falied. */
  private int statQSSDParamFailed = 0;

  private String statParamWithBoschRespRvw;

  private int statNumParamInBoschResp = 0;

  private String statParamWithBoschRespQnaireRvw;

  private int statNumParamInBoschRespRvwed = 0;

  private int statQnaireNagativeAnswer = 0;

  /**
   * count of parameters that are equal
   */
  private long statEqualParCount;

  /**
   * @return the statTotalParamInA2L
   */
  public int getStatTotalParamInA2L() {
    return this.statTotalParamInA2L;
  }


  /**
   * @return the statFilteredParam
   */
  public int getStatFilteredParam() {
    return this.statFilteredParam;
  }


  /**
   * @return the statCompliParamInA2L
   */
  public int getStatCompliParamInA2L() {
    return this.statCompliParamInA2L;
  }


  /**
   * @return the statParamReviewed
   */
  public int getStatParamReviewed() {
    return this.statParamReviewed;
  }


  /**
   * @return the statFilteredParamRvwd
   */
  public int getStatFilteredParamRvwd() {
    return this.statFilteredParamRvwd;
  }


  /**
   * @return the statParamRvwdNotEqual
   */
  public int getStatParamRvwdNotEqual() {
    return this.statParamRvwdNotEqual;
  }


  /**
   * @return the statFilteredParamRvwdNotEqual
   */
  public int getStatFilteredParamRvwdNotEqual() {
    return this.statFilteredParamRvwdNotEqual;
  }


  /**
   * @return the statCompliCssdFailed
   */
  public int getStatCompliCssdFailed() {
    return this.statCompliCssdFailed;
  }


  /**
   * @return the statCompliSSDRvFailed
   */
  public int getStatCompliSSDRvFailed() {
    return this.statCompliSSDRvFailed;
  }


  /**
   * @return the statCompliNoRuleFailed
   */
  public int getStatCompliNoRuleFailed() {
    return this.statCompliNoRuleFailed;
  }


  /**
   * @return the statCompliParamPassed
   */
  public int getStatCompliParamPassed() {
    return this.statCompliParamPassed;
  }

  /**
   * @param statTotalParamInA2L the statTotalParamInA2L to set
   */
  public void setStatTotalParamInA2L(final int statTotalParamInA2L) {
    this.statTotalParamInA2L = statTotalParamInA2L;
  }


  /**
   * @param statFilteredParam the statFilteredParam to set
   */
  public void setStatFilteredParam(final int statFilteredParam) {
    this.statFilteredParam = statFilteredParam;
  }


  /**
   * @param statCompliParamInA2L the statCompliParamInA2L to set
   */
  public void setStatCompliParamInA2L(final int statCompliParamInA2L) {
    this.statCompliParamInA2L = statCompliParamInA2L;
  }


  /**
   * @param statParamReviewed the statParamReviewed to set
   */
  public void setStatParamReviewed(final int statParamReviewed) {
    this.statParamReviewed = statParamReviewed;
  }


  /**
   * @param statFilteredParamRvwd the statFilteredParamRvwd to set
   */
  public void setStatFilteredParamRvwd(final int statFilteredParamRvwd) {
    this.statFilteredParamRvwd = statFilteredParamRvwd;
  }


  /**
   * @param statParamRvwdNotEqual the statParamRvwdNotEqual to set
   */
  public void setStatParamRvwdNotEqual(final int statParamRvwdNotEqual) {
    this.statParamRvwdNotEqual = statParamRvwdNotEqual;
  }


  /**
   * @param statFilteredParamRvwdNotEqual the statFilteredParamRvwdNotEqual to set
   */
  public void setStatFilteredParamRvwdNotEqual(final int statFilteredParamRvwdNotEqual) {
    this.statFilteredParamRvwdNotEqual = statFilteredParamRvwdNotEqual;
  }


  /**
   * @param statCompliCssdFailed the statCompliCssdFailed to set
   */
  public void setStatCompliCssdFailed(final int statCompliCssdFailed) {
    this.statCompliCssdFailed = statCompliCssdFailed;
  }


  /**
   * @param statCompliSSDRvFailed the statCompliSSDRvFailed to set
   */
  public void setStatCompliSSDRvFailed(final int statCompliSSDRvFailed) {
    this.statCompliSSDRvFailed = statCompliSSDRvFailed;
  }


  /**
   * @param statCompliNoRuleFailed the statCompliNoRuleFailed to set
   */
  public void setStatCompliNoRuleFailed(final int statCompliNoRuleFailed) {
    this.statCompliNoRuleFailed = statCompliNoRuleFailed;
  }


  /**
   * @param statCompliParamPassed the statCompliParamPassed to set
   */
  public void setStatCompliParamPassed(final int statCompliParamPassed) {
    this.statCompliParamPassed = statCompliParamPassed;
  }


  /**
   * @return the statParamRvwdEqual
   */
  public int getStatParamRvwdEqual() {
    return this.statParamRvwdEqual;
  }


  /**
   * @param statParamRvwdEqual the statParamRvwdEqual to set
   */
  public void setStatParamRvwdEqual(final int statParamRvwdEqual) {
    this.statParamRvwdEqual = statParamRvwdEqual;
  }


  /**
   * @return the statEqualParCount
   */
  public long getStatEqualParCount() {
    return this.statEqualParCount;
  }


  /**
   * @param statEqualParCount the statEqualParCount to set
   */
  public void setStatEqualParCount(final long statEqualParCount) {
    this.statEqualParCount = statEqualParCount;
  }

  /**
   * @return the statQSSDParamFailed
   */
  public int getStatQSSDParamFailed() {
    return this.statQSSDParamFailed;
  }


  /**
   * @param statQSSDParamFailed the statQSSDParamFailed to set
   */
  public void setStatQSSDParamFailed(final int statQSSDParamFailed) {
    this.statQSSDParamFailed = statQSSDParamFailed;
  }


  /**
   * @return the statParamWithBoschRespRvw
   */
  public String getStatParamWithBoschRespRvw() {
    return this.statParamWithBoschRespRvw;
  }


  /**
   * @param string the statParamWithBoschRespRvw to set
   */
  public void setStatParamWithBoschRespRvw(final String string) {
    this.statParamWithBoschRespRvw = string;
  }

  /**
   * @return the statNumParamInBoschResp
   */
  public int getstatNumParamInBoschResp() {
    return this.statNumParamInBoschResp;
  }

  /**
   * @param statNumParamInBoschResp the statNumParamInBoschResp to set
   */
  public void setStatNumParamInBoschResp(final int statNumParamInBoschResp) {
    this.statNumParamInBoschResp = statNumParamInBoschResp;
  }

  /**
   * @return the statParamWithBoschRespQnaireRvw
   */
  public String getStatParamWithBoschRespQnaireRvw() {
    return this.statParamWithBoschRespQnaireRvw;
  }


  /**
   * @param statParamWithBoschRespQnaireRvw the statParamWithBoschRespQnaireRvw to set
   */
  public void setStatParamWithBoschRespQnaireRvw(final String statParamWithBoschRespQnaireRvw) {
    this.statParamWithBoschRespQnaireRvw = statParamWithBoschRespQnaireRvw;
  }


  /**
   * @return the statNumParamInBoschRespRvwed
   */
  public int getstatNumParamInBoschRespRvwed() {
    return this.statNumParamInBoschRespRvwed;
  }


  /**
   * @param statNumParamInBoschRespRvwed the statNumParamInBoschRespRvwed to set
   */
  public void setStatNumParamInBoschRespRvwed(final int statNumParamInBoschRespRvwed) {
    this.statNumParamInBoschRespRvwed = statNumParamInBoschRespRvwed;
  }

  /**
   * @return the statQnaireNagativeAnswer
   */
  public int getStatQnaireNagativeAnswer() {
    return this.statQnaireNagativeAnswer;
  }


  /**
   * @param statQnaireNagativeAnswer the statQnaireNagativeAnswer to set
   */
  public void setStatQnaireNagativeAnswer(final int statQnaireNagativeAnswer) {
    this.statQnaireNagativeAnswer = statQnaireNagativeAnswer;
  }


}
