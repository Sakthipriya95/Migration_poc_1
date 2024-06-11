/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author apj4cob
 */
public class WPFinishRvwDet {

  Long paramId;
  Long resultId;
  String rvwType;
  String lockStatus;
  String rvwScore;
  String arcReleaseFlag;

  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }


  /**
   * @return the resultId
   */
  public Long getResultId() {
    return this.resultId;
  }


  /**
   * @param resultId the resultId to set
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }

  /**
   * @return the rvwType
   */
  public String getRvwType() {
    return this.rvwType;
  }

  /**
   * @param rvwType the rvwType to set
   */
  public void setRvwType(final String rvwType) {
    this.rvwType = rvwType;
  }

  /**
   * @return the lockStatus
   */
  public String getLockStatus() {
    return this.lockStatus;
  }

  /**
   * @param lockStatus the lockStatus to set
   */
  public void setLockStatus(final String lockStatus) {
    this.lockStatus = lockStatus;
  }

  /**
   * @return the rvwScore
   */
  public String getRvwScore() {
    return this.rvwScore;
  }

  /**
   * @param rvwScore the rvwScore to set
   */
  public void setRvwScore(final String rvwScore) {
    this.rvwScore = rvwScore;
  }


  /**
   * @return the arcReleaseFlag
   */
  public String getArcReleaseFlag() {
    return this.arcReleaseFlag;
  }


  /**
   * @param arcReleaseFlag the arcReleaseFlag to set
   */
  public void setArcReleaseFlag(final String arcReleaseFlag) {
    this.arcReleaseFlag = arcReleaseFlag;
  }

}
