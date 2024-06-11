/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author and4cob
 */
public class A2lWpImportProfileData {

  private String prefixForWp;

  private String prefixForResp;

  /**
   * @return the prefixForWp
   */
  public String getPrefixForWp() {
    return this.prefixForWp;
  }

  /**
   * @param prefixForWp the prefixForWp to set
   */
  public void setPrefixForWp(final String prefixForWp) {
    this.prefixForWp = prefixForWp;
  }

  /**
   * @return the prefixForResp
   */
  public String getPrefixForResp() {
    return this.prefixForResp;
  }

  /**
   * @param prefixForResp the prefixForResp to set
   */
  public void setPrefixForResp(final String prefixForResp) {
    this.prefixForResp = prefixForResp;
  }
}
