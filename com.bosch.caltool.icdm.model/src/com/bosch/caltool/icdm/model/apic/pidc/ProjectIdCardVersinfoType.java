/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author NIP4COB
 */
public class ProjectIdCardVersinfoType extends ProjectIdCardInfoType {


  private PidcVersionType pidcVersionType;


  /**
   * @return the pidcVersionType
   */
  public PidcVersionType getPidcVersionType() {
    return this.pidcVersionType;
  }

  /**
   * @param pidcVersionType the pidcVersionType to set
   */
  public void setPidcVersionType(final PidcVersionType pidcVersionType) {
    this.pidcVersionType = pidcVersionType;
  }
}
