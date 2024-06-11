/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dja7cob
 */
public class PidcCreationUcFav {

  private Long ucId;

  private Long ucGrpId;

  private Long ucSecId;


  /**
   * @return the ucId
   */
  public Long getUcId() {
    return this.ucId;
  }


  /**
   * @param ucId the ucId to set
   */
  public void setUcId(final Long ucId) {
    this.ucId = ucId;
  }


  /**
   * @return the ucGrpId
   */
  public Long getUcGrpId() {
    return this.ucGrpId;
  }


  /**
   * @param ucGrpId the ucGrpId to set
   */
  public void setUcGrpId(final Long ucGrpId) {
    this.ucGrpId = ucGrpId;
  }


  /**
   * @return the ucSecId
   */
  public Long getUcSecId() {
    return this.ucSecId;
  }


  /**
   * @param ucSecId the ucSecId to set
   */
  public void setUcSecId(final Long ucSecId) {
    this.ucSecId = ucSecId;
  }

}
