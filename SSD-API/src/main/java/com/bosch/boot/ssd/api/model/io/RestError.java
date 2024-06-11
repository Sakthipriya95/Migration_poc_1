/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.io;


/**
 * @author GDH9COB
 *
 */
public class RestError {
  
  private int code;
  
  private String txt;

  /**
   * @param code
   * @param txt
   */
  public RestError(int code, String txt) {
    super();
    this.code = code;
    this.txt = txt;
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  
  /**
   * @return the txt
   */
  public String getTxt() {
    return txt;
  }

  
  /**
   * @param txt the txt to set
   */
  public void setTxt(String txt) {
    this.txt = txt;
  }
  
  

}
