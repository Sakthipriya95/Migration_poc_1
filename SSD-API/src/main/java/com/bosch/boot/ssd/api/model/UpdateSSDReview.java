/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ICP1COB
 */
public class UpdateSSDReview implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 7087889295299744045L;

  private BigDecimal ssdReviewId;
  private String reviewLink;
  private String reviewId;

  /**
   * @return the ssdReviewId
   */
  public BigDecimal getSsdReviewId() {
    return ssdReviewId;
  }

  /**
   * @param ssdReviewId the ssdReviewId to set
   */
  public void setSsdReviewId(BigDecimal ssdReviewId) {
    this.ssdReviewId = ssdReviewId;
  }

  /**
   * @return the reviewLink
   */
  public String getReviewLink() {
    return reviewLink;
  }

  /**
   * @param reviewLink the reviewLink to set
   */
  public void setReviewLink(String reviewLink) {
    this.reviewLink = reviewLink;
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
}
