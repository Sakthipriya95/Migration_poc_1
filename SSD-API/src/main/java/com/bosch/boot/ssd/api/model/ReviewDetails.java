/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;

/**
 * @author ICP1COB
 */
public class ReviewDetails implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = -2707093263361287539L;
  
  private String reviewName;
  private String reviewPlanStrtDt;
  private String isChklstCnfReq;
  private String reviewRemarks;
  
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
   * @return the reviewPlanStrtDt
   */
  public String getReviewPlanStrtDt() {
    return reviewPlanStrtDt;
  }
  
  /**
   * @param reviewPlanStrtDt the reviewPlanStrtDt to set
   */
  public void setReviewPlanStrtDt(String reviewPlanStrtDt) {
    this.reviewPlanStrtDt = reviewPlanStrtDt;
  }
  
  /**
   * @return the isChklstCnfReq
   */
  public String getIsChklstCnfReq() {
    return isChklstCnfReq;
  }
  
  /**
   * @param isChklstCnfReq the isChklstCnfReq to set
   */
  public void setIsChklstCnfReq(String isChklstCnfReq) {
    this.isChklstCnfReq = isChklstCnfReq;
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

 
}
