/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


import java.util.ArrayList;
import java.util.List;

/**
 * @author rgo7cob
 */
public class ReviewParamResponse {

  private Long paramId;

  private String paramName;

  private String longName;

  private List<ReviewResultType> reviewResultType;


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
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
  }


  /**
   * @param longName the longName to set
   */
  public void setLongName(final String longName) {
    this.longName = longName;
  }


  /**
   * @return the reviewResultType
   */
  public List<ReviewResultType> getReviewResultType() {
    return this.reviewResultType;
  }


  /**
   * @param reviewResultType the reviewResultType to set
   */
  public void setReviewResultType(final List<ReviewResultType> reviewResultType) {
    this.reviewResultType = reviewResultType == null ? null : new ArrayList<>(reviewResultType);
  }


}
