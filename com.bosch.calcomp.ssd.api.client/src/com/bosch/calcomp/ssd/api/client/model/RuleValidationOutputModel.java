/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.client.model;

/**
 * @author TAB1JA
 *
 */
public class RuleValidationOutputModel {

  private Integer lineNo;
  
  private String message;
  
  private String type;


  /**
   * @return the lineNo
   */
  public Integer getLineNo() {
    return lineNo;
  }

  
  /**
   * @param lineNo the lineNo to set
   */
  public void setLineNo(Integer lineNo) {
    this.lineNo = lineNo;
  }

  
  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  
  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  
  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  
  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }
  
  
}
