/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;

/**
 * @author ICP1COB
 */
public class InputReviewDetails implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3978513808073047570L;

  private String userId;
  private String reviewId;
  private String reviewName;
  private String reviewRemarks;
  private String[] reviewers;

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return the reviewId
   */
  public String getReviewId() {
    return reviewId;
  }

  /**
   * @param reviewId the reviewId to set
   */
  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  /**
   * @return the reviewName
   */
  public String getReviewName() {
    return reviewName;
  }

  /**
   * @param reviewName the reviewName to set
   */
  public void setReviewName(String reviewName) {
    this.reviewName = reviewName;
  }

  /**
   * @return the reviewRemarks
   */
  public String getReviewRemarks() {
    return reviewRemarks;
  }

  /**
   * @param reviewRemarks the reviewRemarks to set
   */
  public void setReviewRemarks(String reviewRemarks) {
    this.reviewRemarks = reviewRemarks;
  }

  /**
   * @return the reviewers
   */
  public String[] getReviewers() {
    return reviewers;
  }

  /**
   * @param reviewers the reviewers to set
   */
  public void setReviewers(String[] reviewers) {
    this.reviewers = reviewers;
  }
}
