/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;


import com.bosch.caltool.icdm.bo.cdr.CheckSSDOutputData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_EXCEPTION_TYPE;

/**
 * @author gge6cob
 */
public class CompliReviewSummary {


  private final CompliReviewData compliReviewData;


  private CheckSSDOutputData checkSSDOutData;


  /**
   * Review Exception Object
   */
  private Exception reviewExceptionObj;

  /**
   * Cdr Exception type
   */
  private CDR_EXCEPTION_TYPE cdrException;


  /**
   * @return the compliReviewData
   */
  public CompliReviewData getCompliReviewData() {
    return this.compliReviewData;
  }

  /**
   * Instantiates a new compli review summary.
   *
   * @param compliReviewData the compli review data
   */
  public CompliReviewSummary(final CompliReviewData compliReviewData) {
    this.compliReviewData = compliReviewData;
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
   * @return the checkSSDOutData
   */
  public CheckSSDOutputData getCheckSSDOutData() {
    return this.checkSSDOutData;
  }


  /**
   * @param checkSSDOutData the checkSSDOutData to set
   */
  public void setCheckSSDOutData(final CheckSSDOutputData checkSSDOutData) {
    this.checkSSDOutData = checkSSDOutData;
  }


}
