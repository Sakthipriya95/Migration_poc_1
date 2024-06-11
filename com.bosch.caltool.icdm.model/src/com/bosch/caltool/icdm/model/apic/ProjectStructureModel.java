/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;


/**
 * @author hnu1cob
 */
public class ProjectStructureModel {

  /**
   * Id of Project ID Card
   */
  private Long pidcId;

  /**
   * Id of PIDC Version
   */
  private Long pidcVersId;

  /**
   * Id of PIDC Variant
   */
  private Long varId;

  /**
   * Id of PIDC A2l
   */
  private Long pidcA2lId;

  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return the varId
   */
  public Long getVarId() {
    return this.varId;
  }

  /**
   * @param varId the varId to set
   */
  public void setVarId(final Long varId) {
    this.varId = varId;
  }

  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

}
