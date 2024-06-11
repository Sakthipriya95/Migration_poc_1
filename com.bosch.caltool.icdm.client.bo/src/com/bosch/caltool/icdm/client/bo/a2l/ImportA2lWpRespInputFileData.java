/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;

/**
 * @author dmr1cob
 */
public class ImportA2lWpRespInputFileData {

  private String paramName;

  private String funcName;

  private String wpName;

  private String varGrpName;

  private String respName;

  private String respTypeName;

  private Long a2lVarGrpId;

  private boolean isInputFileVarGrp;

  private ImportA2lWpRespData importA2lWpRespData;


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
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the varGrpName
   */
  public String getVarGrpName() {
    return this.varGrpName;
  }


  /**
   * @param varGrpName the varGrpName to set
   */
  public void setVarGrpName(final String varGrpName) {
    this.varGrpName = varGrpName;
  }


  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }


  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
  }


  /**
   * @return the respTypeName
   */
  public String getRespTypeName() {
    return this.respTypeName;
  }


  /**
   * @param respTypeName the respTypeName to set
   */
  public void setRespTypeName(final String respTypeName) {
    this.respTypeName = respTypeName;
  }


  /**
   * @return the a2lVarGrpId
   */
  public Long getA2lVarGrpId() {
    return this.a2lVarGrpId;
  }


  /**
   * @param a2lVarGrpId the a2lVarGrpId to set
   */
  public void setA2lVarGrpId(final Long a2lVarGrpId) {
    this.a2lVarGrpId = a2lVarGrpId;
  }


  /**
   * @return the isInputFileVarGrp
   */
  public boolean isInputFileVarGrp() {
    return this.isInputFileVarGrp;
  }


  /**
   * @param isInputFileVarGrp the isInputFileVarGrp to set
   */
  public void setInputFileVarGrp(final boolean isInputFileVarGrp) {
    this.isInputFileVarGrp = isInputFileVarGrp;
  }


  /**
   * @return the importA2lWpRespData
   */
  public ImportA2lWpRespData getImportA2lWpRespData() {
    return this.importA2lWpRespData;
  }


  /**
   * @param importA2lWpRespData the importA2lWpRespData to set
   */
  public void setImportA2lWpRespData(final ImportA2lWpRespData importA2lWpRespData) {
    this.importA2lWpRespData = importA2lWpRespData;
  }
}
