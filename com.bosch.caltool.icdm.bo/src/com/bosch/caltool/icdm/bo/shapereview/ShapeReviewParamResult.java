/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;


/**
 * @author bne4cob
 */
public class ShapeReviewParamResult {

  /**
   * Name of the parameter
   */
  private String paramName;

  /**
   * Result
   */
  // TODO create a new enum
  private String result;

  /**
   * Error details
   */
  private String errorDetails;

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
   * @return the result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @param result the result to set
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * @return the errorDetails
   */
  public String getErrorDetails() {
    return this.errorDetails;
  }

  /**
   * @param errorDetails the errorDetails to set
   */
  public void setErrorDetails(final String errorDetails) {
    this.errorDetails = errorDetails;
  }


}
