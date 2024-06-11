/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.caldataimport;

/**
 * @author rgo7cob
 */
public class CalDataImportInput {

  private CalDataImportData calDataImportData;

  private String funcId;

  private String paramColType;


  /**
   * @return the calDataImportData
   */
  public CalDataImportData getCalDataImportData() {
    return this.calDataImportData;
  }


  /**
   * @param calDataImportData the calDataImportData to set
   */
  public void setCalDataImportData(final CalDataImportData calDataImportData) {
    this.calDataImportData = calDataImportData;
  }


  /**
   * @return the funcId
   */
  public String getFuncId() {
    return this.funcId;
  }


  /**
   * @param funcId the funcId to set
   */
  public void setFuncId(final String funcId) {
    this.funcId = funcId;
  }


  /**
   * @return the paramColType
   */
  public String getParamColType() {
    return this.paramColType;
  }


  /**
   * @param paramColType the paramColType to set
   */
  public void setParamColType(final String paramColType) {
    this.paramColType = paramColType;
  }


}
