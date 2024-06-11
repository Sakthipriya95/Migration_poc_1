/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author bne4cob
 */
public class PidcVcdmTransferInput {

  private Pidc pidc;

  private String encPwd;


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }

  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }


  /**
   * @return the encPwd
   */
  public String getEncPwd() {
    return this.encPwd;
  }


  /**
   * @param encPwd the encPwd to set
   */
  public void setEncPwd(final String encPwd) {
    this.encPwd = encPwd;
  }


}
