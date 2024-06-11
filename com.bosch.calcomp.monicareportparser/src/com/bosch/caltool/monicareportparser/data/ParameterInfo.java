/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser.data;


/**
 * @author rgo7cob
 */
public class ParameterInfo {

  /**
   * @author rgo7cob
   */
  public enum MONICA_REVIEW_STATUS {
                                    /**
                                     * Ok
                                     */
                                    OK("O", "OK"),
                                    /**
                                     * Not Ok
                                     */
                                    NOT_OK("N", "NOT OK"),
                                    /**
                                     * Low
                                     */
                                    LOW("L", "LOW"),
                                    /**
                                     * High
                                     */
                                    HIGH("H", "HIGH"),
                                    /**
                                     * Not reviewed
                                     */
                                    NOT_REVIEWED("", "???"),
                                    /**
                                     * Not reviewed
                                     */
                                    NONE("", "NONE"),
                                    /**
                                     * Not reviewed
                                     */
                                    FALIED("N", "FAILED");

    /**
     * db type
     */
    private final String dbType;

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * ui type
     */
    private final String uiType;

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    MONICA_REVIEW_STATUS(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

  }

  /**
   * labelName
   */
  private String labelName;

  /**
   * status
   */
  private String status;
  /**
   * comment
   */
  private String comment;
  /**
   * Reviewed By
   */
  private String reviewedBy;

  private String cellCommentForParam;

  /**
   * @return the labelName
   */
  public String getLabelName() {
    return this.labelName;
  }

  /**
   * @param labelName the labelName to set
   */
  public void setLabelName(final String labelName) {
    this.labelName = labelName;
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
   * @param value value
   * @return the enum value
   */
  public MONICA_REVIEW_STATUS getReviewStatus(final String value) {

    for (MONICA_REVIEW_STATUS type : MONICA_REVIEW_STATUS.values()) {
      if (type.getUiType().equalsIgnoreCase(value)) {
        return type;
      }
    }
    return MONICA_REVIEW_STATUS.NOT_REVIEWED;

  }


  /**
   * @return the reviewedBy
   */
  public String getReviewedBy() {
    return this.reviewedBy;
  }


  /**
   * @param reviewedBy the reviewedBy to set
   */
  public void setReviewedBy(final String reviewedBy) {
    this.reviewedBy = reviewedBy;
  }

  /**
   * @return the cellCommentForParam
   */
  public String getCellCommentForParam() {
    return this.cellCommentForParam;
  }

  /**
   * @param cellCommentForParam the cellCommentForParam to set
   */
  public void setCellCommentForParam(final String cellCommentForParam) {
    this.cellCommentForParam = cellCommentForParam;
  }

}
