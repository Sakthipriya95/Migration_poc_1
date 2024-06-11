/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author say8cob
 */
public class MonicaReviewData {

  private String label;

  private String status;

  private String comment;

  private String reviewedBy;


  /**
   * @return the label
   */
  public String getLabel() {
    return this.label;
  }


  /**
   * @param label the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }


  /**
   * @return the status
   */
  public String getStatus() {
    return this.status;
  }


  /**
   * @param status the status to set
   */
  public void setStatus(final String status) {
    this.status = status;
  }


  /**
   * @return the comment
   */
  public String getComment() {
    return this.comment;
  }


  /**
   * @param comment the comment to set
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }


  /**
   * @return the reviewedBy
   */
  public String getReviewedBy() {
    return reviewedBy;
  }


  /**
   * @param reviewedBy the reviewedBy to set
   */
  public void setReviewedBy(String reviewedBy) {
    this.reviewedBy = reviewedBy;
  }


}
