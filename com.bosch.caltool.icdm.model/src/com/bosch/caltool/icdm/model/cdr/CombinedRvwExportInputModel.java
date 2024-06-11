/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author say8cob
 *
 */
public class CombinedRvwExportInputModel {
  
  private Long rvwResultId;
  
  private boolean onlyReviewResult;
  
  private boolean onlyRvwResAndQnaireWrkSet;
  
  private boolean onlyRvwResAndQnaireLstBaseLine;
  
  private boolean loadEditorData;
  
  /**
   * @return the rvwResultId
   */
  public Long getRvwResultId() {
    return rvwResultId;
  }

  
  /**
   * @param rvwResultId the rvwResultId to set
   */
  public void setRvwResultId(Long rvwResultId) {
    this.rvwResultId = rvwResultId;
  }

  /**
   * @return the onlyReviewResult
   */
  public boolean isOnlyReviewResult() {
    return onlyReviewResult;
  }

  
  /**
   * @param onlyReviewResult the onlyReviewResult to set
   */
  public void setOnlyReviewResult(boolean onlyReviewResult) {
    this.onlyReviewResult = onlyReviewResult;
  }

  
  /**
   * @return the onlyRvwResAndQnaireWrkSet
   */
  public boolean isOnlyRvwResAndQnaireWrkSet() {
    return onlyRvwResAndQnaireWrkSet;
  }

  
  /**
   * @param onlyRvwResAndQnaireWrkSet the onlyRvwResAndQnaireWrkSet to set
   */
  public void setOnlyRvwResAndQnaireWrkSet(boolean onlyRvwResAndQnaireWrkSet) {
    this.onlyRvwResAndQnaireWrkSet = onlyRvwResAndQnaireWrkSet;
  }

  
  /**
   * @return the onlyRvwResAndQnaireLstBaseLine
   */
  public boolean isOnlyRvwResAndQnaireLstBaseLine() {
    return onlyRvwResAndQnaireLstBaseLine;
  }

  
  /**
   * @param onlyRvwResAndQnaireLstBaseLine the onlyRvwResAndQnaireLstBaseLine to set
   */
  public void setOnlyRvwResAndQnaireLstBaseLine(boolean onlyRvwResAndQnaireLstBaseLine) {
    this.onlyRvwResAndQnaireLstBaseLine = onlyRvwResAndQnaireLstBaseLine;
  }


  
  /**
   * @return the loadEditorData
   */
  public boolean isLoadEditorData() {
    return loadEditorData;
  }


  
  /**
   * @param loadEditorData the loadEditorData to set
   */
  public void setLoadEditorData(boolean loadEditorData) {
    this.loadEditorData = loadEditorData;
  }

  

}
