/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author hnu1cob
 */
public class UnmapA2LDeletionResponse {

  private PidcA2l pidcA2l;

  /**
   * @return the pidcA2lId
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2l(final PidcA2l pidcA2lId) {
    this.pidcA2l = pidcA2lId;
  }

}
