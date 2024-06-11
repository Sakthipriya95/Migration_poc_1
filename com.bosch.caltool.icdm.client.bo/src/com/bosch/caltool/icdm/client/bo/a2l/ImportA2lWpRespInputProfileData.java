/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;


/**
 * @author dmr1cob
 */
public class ImportA2lWpRespInputProfileData {

  private boolean isFuncCol;

  private boolean isLabelCol;

  private boolean wpDefImp;

  private Integer funcOrLabelCol;

  private Integer wpColNum;

  private Integer respColNum;

  private Integer respTypeColNum;

  private boolean isInputFileVarGrp;

  private Integer varGrpColNum;

  private Long a2lVarGrpId;


  /**
   * @return the isFuncCol
   */
  public boolean isFuncCol() {
    return this.isFuncCol;
  }


  /**
   * @param isFuncCol the isFuncCol to set
   */
  public void setFuncCol(final boolean isFuncCol) {
    this.isFuncCol = isFuncCol;
  }


  /**
   * @return the isLabelCol
   */
  public boolean isLabelCol() {
    return this.isLabelCol;
  }


  /**
   * @param isLabelCol the isLabelCol to set
   */
  public void setLabelCol(final boolean isLabelCol) {
    this.isLabelCol = isLabelCol;
  }


  /**
   * @return the wpDefImp
   */
  public boolean isWpDefImp() {
    return this.wpDefImp;
  }


  /**
   * @param wpDefImp the wpDefImp to set
   */
  public void setWpDefImp(final boolean wpDefImp) {
    this.wpDefImp = wpDefImp;
  }


  /**
   * @return the funcOrLabelCol
   */
  public Integer getFuncOrLabelCol() {
    return this.funcOrLabelCol;
  }


  /**
   * @param funcOrLabelCol the funcOrLabelCol to set
   */
  public void setFuncOrLabelCol(final Integer funcOrLabelCol) {
    this.funcOrLabelCol = funcOrLabelCol;
  }


  /**
   * @return the wpColNum
   */
  public Integer getWpColNum() {
    return this.wpColNum;
  }


  /**
   * @param wpColNum the wpColNum to set
   */
  public void setWpColNum(final Integer wpColNum) {
    this.wpColNum = wpColNum;
  }


  /**
   * @return the respColNum
   */
  public Integer getRespColNum() {
    return this.respColNum;
  }


  /**
   * @param respColNum the respColNum to set
   */
  public void setRespColNum(final Integer respColNum) {
    this.respColNum = respColNum;
  }


  /**
   * @return the respTypeColNum
   */
  public Integer getRespTypeColNum() {
    return this.respTypeColNum;
  }


  /**
   * @param respTypeColNum the respTypeColNum to set
   */
  public void setRespTypeColNum(final Integer respTypeColNum) {
    this.respTypeColNum = respTypeColNum;
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
   * @return the varGrpColNum
   */
  public Integer getVarGrpColNum() {
    return this.varGrpColNum;
  }


  /**
   * @param varGrpColNum the varGrpColNum to set
   */
  public void setVarGrpColNum(final Integer varGrpColNum) {
    this.varGrpColNum = varGrpColNum;
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

}
