/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author NIP4COB
 */
public class ParameterReviewResult {

  private long parameterId;
  private String parameterName;
  private String parameterLongName;
  private ReviewResultsType[] reviewDetails;

  /**
   * @return the parameterId
   */
  public long getParameterId() {
    return this.parameterId;
  }

  /**
   * @param parameterId the parameterId to set
   */
  public void setParameterId(final long parameterId) {
    this.parameterId = parameterId;
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return this.parameterName;
  }

  /**
   * @param parameterName the parameterName to set
   */
  public void setParameterName(final String parameterName) {
    this.parameterName = parameterName;
  }

  /**
   * @return the parameterLongName
   */
  public String getParameterLongName() {
    return this.parameterLongName;
  }

  /**
   * @param parameterLongName the parameterLongName to set
   */
  public void setParameterLongName(final String parameterLongName) {
    this.parameterLongName = parameterLongName;
  }

  /**
   * @return the reviewDetails
   */
  public ReviewResultsType[] getReviewDetails() {
    return this.reviewDetails;
  }

  /**
   * @param reviewDetails the reviewDetails to set
   */
  public void setReviewDetails(final ReviewResultsType[] reviewDetails) {
    this.reviewDetails = reviewDetails;
  }
}
