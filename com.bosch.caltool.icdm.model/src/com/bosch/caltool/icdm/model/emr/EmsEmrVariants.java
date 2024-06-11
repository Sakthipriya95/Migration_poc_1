/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.Set;

/**
 * @author dja7cob
 */
public class EmsEmrVariants {

  /**
   * Emr File ID
   */
  private Long emrFileId;

  /**
   * Emission standard ID
   */
  private Long emsId;

  /**
   * Set of variant IDs for the Emission standard
   */
  private Set<Long> variantIds;


  /**
   * @return the emrFileId
   */
  public Long getEmrFileId() {
    return this.emrFileId;
  }


  /**
   * @param emrFileId the emrFileId to set
   */
  public void setEmrFileId(final Long emrFileId) {
    this.emrFileId = emrFileId;
  }


  /**
   * @return the emsId
   */
  public Long getEmsId() {
    return this.emsId;
  }


  /**
   * @param emsId the emsId to set
   */
  public void setEmsId(final Long emsId) {
    this.emsId = emsId;
  }


  /**
   * @return the variantIds
   */
  public Set<Long> getVariantIds() {
    return this.variantIds;
  }


  /**
   * @param variantIds the variantIds to set
   */
  public void setVariantIds(final Set<Long> variantIds) {
    this.variantIds = variantIds;
  }

}
