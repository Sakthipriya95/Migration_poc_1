/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Status of PIDC Version
 *
 * @author bne4cob
 */
public enum PidcVersionStatus {
                               /**
                                * In Work
                                */
                               IN_WORK("I", ApicConstants.INWORK),
                               /**
                                * Locked
                                */
                               LOCKED("L", ApicConstants.LOCKED);

  /**
   * Status code in database
   */
  private String dbStatus;

  /**
   * String representation of status code, to display in UI
   */
  private String uiStatus;

  /**
   * Constructor
   *
   * @param dbStatus Status code in database
   * @param statusStr String representation of status code, to display in UI
   */
  PidcVersionStatus(final String dbStatus, final String statusStr) {
    this.dbStatus = dbStatus;
    this.uiStatus = statusStr;
  }

  /**
   * Find the Enum value corresponding to the DB status
   *
   * @param dbStatus DB status
   * @return PidcVersionStatus enum value, if DB status is valid, else <code>null</code>
   */
  public static PidcVersionStatus getStatus(final String dbStatus) {
    PidcVersionStatus ret = null;
    for (PidcVersionStatus verStatus : PidcVersionStatus.values()) {
      if (verStatus.dbStatus.equals(dbStatus)) {
        ret = verStatus;
        break;
      }
    }
    return ret;
  }

  /**
   * @return the Status code in database
   */
  public String getDbStatus() {
    return this.dbStatus;
  }

  /**
   * @return the String representation of status code, to display in UI
   */
  public String getUiStatus() {
    return this.uiStatus;
  }

}
