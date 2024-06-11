/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author bru2cob
 */
public class PIDCVersionDummy extends PidcVersion {

  /**
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;
  /**
   * Instance of pid card
   */
  private final Pidc pidCard;

  /**
   * @param apicDataProvider Data Provider
   * @param pidCard
   */
  public PIDCVersionDummy(final Pidc pidCard) {
    super();
    this.pidCard = pidCard;
  }

  @Override
  public int compareTo(final PidcVersion pidcVersion) {
    return -1;
  }

  /**
   * @return the proRevId
   */
  @Override
  public Long getProRevId() {
    return 0L;
  }

  /**
   * @return PIDC name
   */
  @Override
  public String getVersionName() {
    return "";
  }

  /**
   * @return Description
   */
  @Override
  public String getDescription() {
    return "";
  }

  /**
   * @return PIDC of this PIDC Version
   */
  public Pidc getPidc() {
    return this.pidCard;
  }
}
