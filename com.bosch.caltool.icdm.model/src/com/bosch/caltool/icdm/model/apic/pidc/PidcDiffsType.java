/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class PidcDiffsType {

  /**
   * Pidc Id
   */
  private Long pidcId;

  /**
   * The PIDC version were to start reporting changes. Together with newPidcChangeNumber this attribute determines the
   * bandwith in which changes are reported. + 1 is added because the chance in the last (old) change nummer should not
   * be considered, just the change that happens after the passed old chance ID.
   */
  private Long oldPidcChangeNumber;

  /**
   * The PIDC version were to end reporting changes. Together with oldPidcChangeNumber this attribute determines the
   * bandwith in which changes are reported.
   */
  private Long newPidcChangeNumber;


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
   * @return the oldPidcChangeNumber
   */
  public Long getOldPidcChangeNumber() {
    return this.oldPidcChangeNumber;
  }


  /**
   * @param oldPidcChangeNumber the oldPidcChangeNumber to set
   */
  public void setOldPidcChangeNumber(final Long oldPidcChangeNumber) {
    this.oldPidcChangeNumber = oldPidcChangeNumber;
  }


  /**
   * @return the newPidcChangeNumber
   */
  public Long getNewPidcChangeNumber() {
    return this.newPidcChangeNumber;
  }


  /**
   * @param newPidcChangeNumber the newPidcChangeNumber to set
   */
  public void setNewPidcChangeNumber(final Long newPidcChangeNumber) {
    this.newPidcChangeNumber = newPidcChangeNumber;
  }


}
