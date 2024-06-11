/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;


/**
 * @author bru2cob
 */
public class ResultData {

  /**
   * parent result id
   */
  private Long parentResultId;
  /**
   * cancelled result id
   */
  private Long canceledResultId;
  /**
   * is official review
   */
  private boolean offReviewType;
  /**
   * is start review
   */
  private boolean startReviewType;
  /**
   * is only locked official review
   */
  private boolean onlyLockedOffReview;
  /**
   * is only locked start review
   */
  private boolean onlyLockedStartResults;

  /**
   * @return the parentResultId
   */
  public Long getParentResultId() {
    return this.parentResultId;
  }

  /**
   * @param parentResultId the parentResultId to set
   */
  public void setParentResultId(final Long parentResultId) {
    this.parentResultId = parentResultId;
  }

  /**
   * @return the canceledResultId
   */
  public Long getCanceledResultId() {
    return this.canceledResultId;
  }

  /**
   * @param canceledResultId the canceledResultId to set
   */
  public void setCanceledResultId(final Long canceledResultId) {
    this.canceledResultId = canceledResultId;
  }

  /**
   * @return the offReviewType
   */
  public boolean isOffReviewType() {
    return this.offReviewType;
  }

  /**
   * @param offReviewType the offReviewType to set
   */
  public void setOffReviewType(final boolean offReviewType) {
    this.offReviewType = offReviewType;
  }

  /**
   * @return the startReviewType
   */
  public boolean isStartReviewType() {
    return this.startReviewType;
  }

  /**
   * @param startReviewType the startReviewType to set
   */
  public void setStartReviewType(final boolean startReviewType) {
    this.startReviewType = startReviewType;
  }

  /**
   * @return the onlyLockedOffReview
   */
  public boolean isOnlyLockedOffReview() {
    return this.onlyLockedOffReview;
  }

  /**
   * @param onlyLockedOffReview the onlyLockedOffReview to set
   */
  public void setOnlyLockedOffReview(final boolean onlyLockedOffReview) {
    this.onlyLockedOffReview = onlyLockedOffReview;
  }

  /**
   * @return the onlyLockedStartResults
   */
  public boolean isOnlyLockedStartResults() {
    return this.onlyLockedStartResults;
  }

  /**
   * @param onlyLockedStartResults the onlyLockedStartResults to set
   */
  public void setOnlyLockedStartResults(final boolean onlyLockedStartResults) {
    this.onlyLockedStartResults = onlyLockedStartResults;
  }


}
