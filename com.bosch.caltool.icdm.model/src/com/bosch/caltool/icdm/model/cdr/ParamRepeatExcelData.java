/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * This class is to provide the row data in Parameters repeated excel report
 * 
 * @author mkl2cob
 */
public class ParamRepeatExcelData {

  // Function name of the parameter
  private String funcName;

  // Parameter name which is repeated
  private String paramName;

  // File name where the parameter is repeated
  private String fileName;


  public ParamRepeatExcelData(final String funcName, final String paramName, final String fileName) {
    this.funcName = funcName;
    this.paramName = paramName;
    this.fileName = fileName;
  }

  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }

  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
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
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


}
