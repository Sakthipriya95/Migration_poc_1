/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

/**
 * @author bru2cob
 */
public class CopyPar2WpFromA2lInput {


  private Long descPidcA2lId;

  private boolean overWriteAssigments;

  private boolean overOnlyDefaultAssigments;

  private Long sourceWpDefVersId;

  /** isCopyToWS - is copy version to workingset flag **/
  private boolean isCopyToWS;

  /**
   * destWpDefVersId - dest wp def vers id - set , in case of copy mappings from one version to woking set
   **/
  private Long destWpDefVersId;

  private boolean derivateFromFunc;

  /**
   * *@return the sourceWpDefVersId
   */

  public Long getSourceWpDefVersId() {
    return this.sourceWpDefVersId;
  }


  /**
   * @param sourceWpDefVersId the sourceWpDefVersId to set
   */
  public void setSourceWpDefVersId(final Long sourceWpDefVersId) {
    this.sourceWpDefVersId = sourceWpDefVersId;
  }


  /**
   * @return the overWriteAssigments
   */
  public boolean isOverWriteAssigments() {
    return this.overWriteAssigments;
  }


  /**
   * @param overWriteAssigments the overWriteAssigments to set
   */
  public void setOverWriteAssigments(final boolean overWriteAssigments) {
    this.overWriteAssigments = overWriteAssigments;
  }


  /**
   * @return the overOnlyDefaultAssigments
   */
  public boolean isOverOnlyDefaultAssigments() {
    return this.overOnlyDefaultAssigments;
  }


  /**
   * @param overOnlyDefaultAssigments the overOnlyDefaultAssigments to set
   */
  public void setOverOnlyDefaultAssigments(final boolean overOnlyDefaultAssigments) {
    this.overOnlyDefaultAssigments = overOnlyDefaultAssigments;
  }


  /**
   * @return the descPidcA2lId
   */
  public Long getDescPidcA2lId() {
    return this.descPidcA2lId;
  }


  /**
   * @param descPidcA2lId the descPidcA2lId to set
   */
  public void setDescPidcA2lId(final Long descPidcA2lId) {
    this.descPidcA2lId = descPidcA2lId;
  }


  /**
   * @return the isCopyToWS
   */
  public boolean isCopyToWS() {
    return this.isCopyToWS;
  }


  /**
   * @param isCopyToWS the isCopyToWS to set
   */
  public void setCopyToWS(final boolean isCopyToWS) {
    this.isCopyToWS = isCopyToWS;
  }


  /**
   * @return the destWpDefVersId
   */
  public Long getDestWpDefVersId() {
    return this.destWpDefVersId;
  }


  /**
   * @param destWpDefVersId the destWpDefVersId to set
   */
  public void setDestWpDefVersId(final Long destWpDefVersId) {
    this.destWpDefVersId = destWpDefVersId;
  }


  /**
   * @return the derivateFromFunc
   */
  public boolean isDerivateFromFunc() {
    return derivateFromFunc;
  }


  /**
   * @param derivateFromFunc the derivateFromFunc to set
   */
  public void setDerivateFromFunc(boolean derivateFromFunc) {
    this.derivateFromFunc = derivateFromFunc;
  }


}
