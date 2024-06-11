/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author rgo7cob
 */
public abstract class AbstractParameter implements IParameter {

  private String longNameGer;
  private String longNameEng;

  private String paramHint;

  private String type;

  private String longName;

  private String ssdClass;

  private String codeWord;

  private String isBitWise;

  private boolean isBlackList;

  private boolean qssdFlag;


  /**
   * @return the longNameGer
   */
  @Override
  public String getLongNameGer() {
    return this.longNameGer;
  }


  /**
   * @param longNameGer the longNameGer to set
   */
  @Override
  public void setLongNameGer(final String longNameGer) {
    this.longNameGer = longNameGer;
  }


  /**
   * @return the longNameEng
   */
  @Override
  public String getLongNameEng() {
    return this.longNameEng;
  }


  /**
   * @param longNameEng the longNameEng to set
   */
  public void setLongNameEng(final String longNameEng) {
    this.longNameEng = longNameEng;
  }


  /**
   * @return the paramHint
   */
  @Override
  public String getParamHint() {
    return this.paramHint;
  }


  /**
   * @param paramHint the paramHint to set
   */
  @Override
  public void setParamHint(final String paramHint) {
    this.paramHint = paramHint;
  }


  /**
   * @return the type
   */
  @Override
  public String getType() {
    return this.type;
  }


  /**
   * @param type the type to set
   */
  @Override
  public void setType(final String type) {
    this.type = type;
  }


  /**
   * @return the longName
   */
  @Override
  public String getLongName() {
    return this.longName;
  }


  /**
   * @param longName the longName to set
   */
  @Override
  public void setLongName(final String longName) {
    this.longName = longName;
  }


  /**
   * @return the ssdClass
   */
  @Override
  public String getSsdClass() {
    return this.ssdClass;
  }


  /**
   * @param ssdClass the ssdClass to set
   */
  @Override
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
  }


  /**
   * @return the codeWord
   */
  @Override
  public String getCodeWord() {
    return this.codeWord;
  }


  /**
   * @param codeWord the codeWord to set
   */
  @Override
  public void setCodeWord(final String codeWord) {
    this.codeWord = codeWord;
  }

  /**
   * @return the isBitWise
   */
  @Override
  public String getIsBitWise() {
    return this.isBitWise;
  }


  /**
   * @param isBitWise the isBitWise to set
   */
  public void setIsBitWise(final String isBitWise) {
    this.isBitWise = isBitWise;
  }


  /**
   * @return the isBlackList
   */
  @Override
  public boolean isBlackList() {
    return this.isBlackList;
  }


  /**
   * @param isBlackList the isBlackList to set
   */
  @Override
  public void setBlackList(final boolean isBlackList) {
    this.isBlackList = isBlackList;
  }


  /**
   * @return the qssdFlag
   */
  @Override
  public boolean isQssdFlag() {
    return this.qssdFlag;
  }


  /**
   * @param qssdFlag the qssdFlag to set
   */
  @Override
  public void setQssdFlag(final boolean qssdFlag) {
    this.qssdFlag = qssdFlag;
  }


}
