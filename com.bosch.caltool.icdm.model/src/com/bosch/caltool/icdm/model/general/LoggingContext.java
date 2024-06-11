/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;


/**
 * LoggingContext Model class
 *
 * @author GBJ1COB
 */
public class LoggingContext {

  private String requestId;

  private String method;

  /**
   * @return the requestId
   */
  public String getRequestId() {
    return this.requestId;
  }


  /**
   * @param requestId the requestId to set
   */
  public void setRequestId(final String requestId) {
    this.requestId = requestId;
  }


  /**
   * @return the method
   */
  public String getMethod() {
    return this.method;
  }


  /**
   * @param method the method to set
   */
  public void setMethod(final String method) {
    this.method = method;
  }

}
