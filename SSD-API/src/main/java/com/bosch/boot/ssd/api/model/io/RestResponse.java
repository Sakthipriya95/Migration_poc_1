/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.io;


/**
 * @author GDH9COB
 *
 */
public class RestResponse {
  
  private boolean success = true;
  private Object result;
  private RestError error;
  
  public RestResponse(boolean success) {
    this.success = success;
  }
  
  public RestResponse(boolean success, Object result) {
    this.success = success;
    this.result = result;
  }
  
  public RestResponse(Object body) {
    this.result = body;
  }

  
  /**
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  
  /**
   * @param success the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  
  /**
   * @return the result
   */
  public Object getResult() {
    return result;
  }

  
  /**
   * @param result the result to set
   */
  public void setResult(Object result) {
    this.result = result;
  }

  
  /**
   * @return the error
   */
  public RestError getError() {
    return error;
  }

  
  /**
   * @param error the error to set
   */
  public void setError(RestError error) {
    this.error = error;
  }
  
  

}
