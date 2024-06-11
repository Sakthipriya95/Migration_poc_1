/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

/**
 * @author elm1cob
 */
public class ExternalPidcVersionInfo extends PidcVersionInfo {

  /**
   * Pidc Hidden flag
   */
  private boolean hidden;

  /**
   * @return the hidden
   */
  public boolean isHidden() {
    return this.hidden;
  }

  /**
   * @param hidden the hidden to set
   */
  public void setHidden(final boolean hidden) {
    this.hidden = hidden;
  }

}
