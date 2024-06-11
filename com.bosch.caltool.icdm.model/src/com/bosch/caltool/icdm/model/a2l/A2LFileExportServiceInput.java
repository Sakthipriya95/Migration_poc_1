/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author say8cob
 */
public class A2LFileExportServiceInput {

  private Long wpDefVersId;

  private Long varGrpId;

  private boolean wpResp;

  private boolean wpRespFunc;

  private boolean respFunc;


  /**
   * @return the wpDefVersId
   */
  public Long getWpDefVersId() {
    return this.wpDefVersId;
  }


  /**
   * @param wpDefVersId the setWpDefVersId to set
   */
  public void setWpDefVersId(final Long wpDefVersId) {
    this.wpDefVersId = wpDefVersId;
  }


  /**
   * @return the varGrpId
   */
  public Long getVarGrpId() {
    return this.varGrpId;
  }


  /**
   * @param varGrpId the selVarGrpId to set
   */
  public void setVarGrpId(final Long varGrpId) {
    this.varGrpId = varGrpId;
  }


  /**
   * @return the wpResp
   */
  public boolean isWpResp() {
    return this.wpResp;
  }


  /**
   * @param wpResp the wpResp to set
   */
  public void setWpResp(final boolean wpResp) {
    this.wpResp = wpResp;
  }


  /**
   * @return the wpRespFunc
   */
  public boolean isWpRespFunc() {
    return this.wpRespFunc;
  }


  /**
   * @param wpRespFunc the wpRespFunc to set
   */
  public void setWpRespFunc(final boolean wpRespFunc) {
    this.wpRespFunc = wpRespFunc;
  }


  /**
   * @return the respFunc
   */
  public boolean isRespFunc() {
    return this.respFunc;
  }


  /**
   * @param respFunc the respFunc to set
   */
  public void setRespFunc(final boolean respFunc) {
    this.respFunc = respFunc;
  }


}
