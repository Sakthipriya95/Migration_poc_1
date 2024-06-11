/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.sql.Timestamp;

/**
 * @author bne4cob
 */


public class ParameterReviewDetails {

  /**
   * Review ID
   */
  private long rvwID;

  /**
   * Review Method - M/A
   */
  private String rvwMethod;

  /**
   * Hint
   */
  private String hint;

  /**
   * Exact match flag setting - Y/N
   */
  private String matchRefFlag;

  /**
   * Review result of the parameter - OK/Not OK/Low/High/???
   */
  private String reviewResult;
  // ICDM-1839
  /**
   * Review file id in which the param was present
   */
  private long rvwFileID;
  /**
   * Id of function in which the param was present during the review
   */
  private long funcID;
  /**
   * Byte array to store the checked value
   */
  private byte[] checkedVal;


  private String reviewScore;

  private String rvwComment;

  private String srResult;

  private Timestamp srAcceptedDate;


  private String srAcceptedFlag;


  private String srAcceptedUser;


  private String srErrorDetails;

  private long rvwParameterId;


  /**
   * @return the checkedVal
   */
  public byte[] getCheckedVal() {
    return this.checkedVal;
  }


  /**
   * @param checkedVal the checkedVal to set
   */
  public void setCheckedVal(final byte[] checkedVal) {
    this.checkedVal = checkedVal;
  }


  /**
   * @return the funcID
   */
  public long getFuncID() {
    return this.funcID;
  }


  /**
   * @param funcID the funcID to set
   */
  public void setFuncID(final long funcID) {
    this.funcID = funcID;
  }


  /**
   * @return the rvwFileID
   */
  public long getRvwFileID() {
    return this.rvwFileID;
  }


  /**
   * @param rvwFileID the rvwFileID to set
   */
  public void setRvwFileID(final long rvwFileID) {
    this.rvwFileID = rvwFileID;
  }

  /**
   * @return the rvwID
   */
  public long getRvwID() {
    return this.rvwID;
  }

  /**
   * @param rvwID the rvwID to set
   */
  public void setRvwID(final long rvwID) {
    this.rvwID = rvwID;
  }

  /**
   * @return the rvwMethod
   */
  public String getRvwMethod() {
    return this.rvwMethod;
  }


  /**
   * @param rvwMethod the rvwMethod to set
   */
  public void setRvwMethod(final String rvwMethod) {
    this.rvwMethod = rvwMethod;
  }


  /**
   * @return the hint
   */
  public String getHint() {
    return this.hint;
  }


  /**
   * @param hint the hint to set
   */
  public void setHint(final String hint) {
    this.hint = hint;
  }


  /**
   * @return the matchRefFlag
   */
  public String getMatchRefFlag() {
    return this.matchRefFlag;
  }


  /**
   * @param matchRefFlag the matchRefFlag to set
   */
  public void setMatchRefFlag(final String matchRefFlag) {
    this.matchRefFlag = matchRefFlag;
  }

  /**
   * @return the reviewResult
   */
  public String getReviewResult() {
    return this.reviewResult;
  }

  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final String reviewResult) {
    this.reviewResult = reviewResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [rvwID=" + this.rvwID + ", rvwMethod=" + this.rvwMethod + ", hint=" + this.hint +
        ", matchRefFlag=" + this.matchRefFlag + ", reviewResult=" + this.reviewResult + ", reviewFileID=" +
        this.rvwFileID + ", latestFuncID=" + this.funcID + "]";
  }


  /**
   * @param reviewScore reviewScore
   */
  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;

  }


  /**
   * @return the reviewScore
   */
  public String getReviewScore() {
    return this.reviewScore;
  }

  /**
   * @param rvwComment the rvwComment to set
   */
  public void setRvwComment(final String rvwComment) {
    this.rvwComment = rvwComment;
  }


  /**
   * @return the rvwComment
   */
  public String getRvwComment() {
    return this.rvwComment;
  }


  /**
   * @return the srResult
   */
  public String getSrResult() {
    return this.srResult;
  }


  /**
   * @param srResult the srResult to set
   */
  public void setSrResult(final String srResult) {
    this.srResult = srResult;
  }


  /**
   * @return the srAcceptedDate
   */
  public Timestamp getSrAcceptedDate() {
    return this.srAcceptedDate;
  }


  /**
   * @param srAcceptedDate the srAcceptedDate to set
   */
  public void setSrAcceptedDate(final Timestamp srAcceptedDate) {
    this.srAcceptedDate = srAcceptedDate;
  }


  /**
   * @return the srAcceptedFlag
   */
  public String getSrAcceptedFlag() {
    return this.srAcceptedFlag;
  }


  /**
   * @param srAcceptedFlag the srAcceptedFlag to set
   */
  public void setSrAcceptedFlag(final String srAcceptedFlag) {
    this.srAcceptedFlag = srAcceptedFlag;
  }


  /**
   * @return the srAcceptedUser
   */
  public String getSrAcceptedUser() {
    return this.srAcceptedUser;
  }


  /**
   * @param srAcceptedUser the srAcceptedUser to set
   */
  public void setSrAcceptedUser(final String srAcceptedUser) {
    this.srAcceptedUser = srAcceptedUser;
  }


  /**
   * @return the srErrorDetails
   */
  public String getSrErrorDetails() {
    return this.srErrorDetails;
  }


  /**
   * @param srErrorDetails the srErrorDetails to set
   */
  public void setSrErrorDetails(final String srErrorDetails) {
    this.srErrorDetails = srErrorDetails;
  }


  /**
   * @return the rvwParameterId
   */
  public Long getRvwParameterId() {
    return this.rvwParameterId;
  }


  /**
   * @param rvwParameterId the rvwParameterId to set
   */
  public void setRvwParameterId(final Long rvwParameterId) {
    this.rvwParameterId = rvwParameterId;
  }


}
