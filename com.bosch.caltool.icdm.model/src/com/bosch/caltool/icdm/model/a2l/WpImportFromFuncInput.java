/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author hnu1cob
 */
public class WpImportFromFuncInput {

  /**
   * Working Set Wp Definition Version Id
   */
  private Long wpDefVersId;
  /**
   * deleteUnusedWPs true, if user opts to delete the WPs no longer used after Import of WP from Funcions
   */
  private boolean deleteUnusedWPs;
  /**
   * keepExistingResp true, if user opts to keep the existing responbilities
   */
  private boolean keepExistingResp;
  /**
   * @return the wpDefVersId
   */
  public Long getWpDefVersId() {
    return wpDefVersId;
  }
  /**
   * @param wpDefVersId the wpDefVersId to set
   */
  public void setWpDefVersId(Long wpDefVersId) {
    this.wpDefVersId = wpDefVersId;
  }
  /**
   * @return the deleteUnusedWPs
   */
  public boolean isDeleteUnusedWPs() {
    return deleteUnusedWPs;
  }
  /**
   * @param deleteUnusedWPs the deleteUnusedWPs to set
   */
  public void setDeleteUnusedWPs(boolean deleteUnusedWPs) {
    this.deleteUnusedWPs = deleteUnusedWPs;
  }
  /**
   * @return the keepExistingResp
   */
  public boolean isKeepExistingResp() {
    return keepExistingResp;
  }
  /**
   * @param keepExistingResp the keepExistingResp to set
   */
  public void setKeepExistingResp(boolean keepExistingResp) {
    this.keepExistingResp = keepExistingResp;
  }
}
