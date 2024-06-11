/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author mkl2cob
 */
public class UnmapA2LResponse {

  /**
   * A2L file name
   */
  private String a2lFileName;

  /**
   * pidc version name
   */
  private String pidcVersName;

  /**
   * review result count
   */
  private int rvwResCount;

  /**
   * question resp count
   */
  private int quesRespCount;


  /**
   * a2l definition versions count
   */
  private int defVersCount;

  /**
   * variant group count
   */
  private int varGrpCount;

  /**
   * wp-resp combinations count
   */
  private int wpRespCombinationsCount;

  /**
   * param mapping count
   */
  private int paramMappingCount;

  /**
   * a2l work package count
   */
  private int a2lWrkPckgCount;

  /**
   * a2l responsibilities count
   */
  private int a2lRespCount;


  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }


  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @param pidcVersName the pidcVersName to set
   */
  public void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }


  /**
   * @return the rvwResCount
   */
  public int getRvwResCount() {
    return this.rvwResCount;
  }


  /**
   * @param rvwResCount the rvwResCount to set
   */
  public void setRvwResCount(final int rvwResCount) {
    this.rvwResCount = rvwResCount;
  }


  /**
   * @return the quesRespCount
   */
  public int getQuesRespCount() {
    return this.quesRespCount;
  }


  /**
   * @param quesRespCount the quesRespCount to set
   */
  public void setQuesRespCount(final int quesRespCount) {
    this.quesRespCount = quesRespCount;
  }


  /**
   * @return the defVersCount
   */
  public int getDefVersCount() {
    return this.defVersCount;
  }


  /**
   * @param defVersCount the defVersCount to set
   */
  public void setDefVersCount(final int defVersCount) {
    this.defVersCount = defVersCount;
  }


  /**
   * @return the varGrpCount
   */
  public int getVarGrpCount() {
    return this.varGrpCount;
  }


  /**
   * @param varGrpCount the varGrpCount to set
   */
  public void setVarGrpCount(final int varGrpCount) {
    this.varGrpCount = varGrpCount;
  }


  /**
   * @return the wpRespCombinationsCount
   */
  public int getWpRespCombinationsCount() {
    return this.wpRespCombinationsCount;
  }


  /**
   * @param wpRespCombinationsCount the wpRespCombinationsCount to set
   */
  public void setWpRespCombinationsCount(final int wpRespCombinationsCount) {
    this.wpRespCombinationsCount = wpRespCombinationsCount;
  }


  /**
   * @return the paramMappingCount
   */
  public int getParamMappingCount() {
    return this.paramMappingCount;
  }


  /**
   * @param paramMappingCount the paramMappingCount to set
   */
  public void setParamMappingCount(final int paramMappingCount) {
    this.paramMappingCount = paramMappingCount;
  }


  /**
   * @return the a2lWrkPckgCount
   */
  public int getA2lWrkPckgCount() {
    return this.a2lWrkPckgCount;
  }


  /**
   * @param a2lWrkPckgCount the a2lWrkPckgCount to set
   */
  public void setA2lWrkPckgCount(final int a2lWrkPckgCount) {
    this.a2lWrkPckgCount = a2lWrkPckgCount;
  }


  /**
   * @return the a2lRespCount
   */
  public int getA2lRespCount() {
    return this.a2lRespCount;
  }


  /**
   * @param a2lRespCount the a2lRespCount to set
   */
  public void setA2lRespCount(final int a2lRespCount) {
    this.a2lRespCount = a2lRespCount;
  }


}
