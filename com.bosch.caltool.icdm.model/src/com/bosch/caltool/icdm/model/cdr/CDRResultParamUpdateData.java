/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.Map;

/**
 * @author bru2cob
 */
public class CDRResultParamUpdateData {

  private Map<Long, CDRResultParameter> paramMap;

  private CDRReviewResult newReviewResult;

  private CDRReviewResult oldReviewResult;
  
  private RvwUserCmntHistory newRvwCmntHistory;
  
  private RvwUserCmntHistory deletedRvwCmntHistory;
  
  
  /**
   * @return the deletedRvwCmntHistory
   */
  public RvwUserCmntHistory getDeletedRvwCmntHistory() {
    return deletedRvwCmntHistory;
  }



  
  /**
   * @param deletedRvwCmntHistory the deletedRvwCmntHistory to set
   */
  public void setDeletedRvwCmntHistory(RvwUserCmntHistory deletedRvwCmntHistory) {
    this.deletedRvwCmntHistory = deletedRvwCmntHistory;
  }



  /**
   * @return the newRvwCmntHistory
   */
  public RvwUserCmntHistory getNewRvwCmntHistory() {
    return newRvwCmntHistory;
  }


  
  /**
   * @param newRvwCmntHistory the newRvwCmntHistory to set
   */
  public void setNewRvwCmntHistory(RvwUserCmntHistory newRvwCmntHistory) {
    this.newRvwCmntHistory = newRvwCmntHistory;
  }


  /**
   * @return the oldReviewResult
   */
  public CDRReviewResult getOldReviewResult() {
    return this.oldReviewResult;
  }


  /**
   * @param oldReviewResult the oldReviewResult to set
   */
  public void setOldReviewResult(final CDRReviewResult oldReviewResult) {
    this.oldReviewResult = oldReviewResult;
  }


  /**
   * @return the paramMap
   */
  public Map<Long, CDRResultParameter> getParamMap() {
    return this.paramMap;
  }


  /**
   * @param paramMap the paramMap to set
   */
  public void setParamMap(final Map<Long, CDRResultParameter> paramMap) {
    this.paramMap = paramMap;
  }


  /**
   * @return the newReviewResult
   */
  public CDRReviewResult getNewReviewResult() {
    return this.newReviewResult;
  }


  /**
   * @param newReviewResult the newReviewResult to set
   */
  public void setNewReviewResult(final CDRReviewResult newReviewResult) {
    this.newReviewResult = newReviewResult;
  }


}
