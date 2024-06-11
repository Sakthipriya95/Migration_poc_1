/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class CdfxDlvryWpRespUniqueRespMerge {

  private Long cdfxDelWpRespId;
  /**
   * Cdfx Delivery Id
   */
  private Long cdfxDeliveryId;
  /**
   * Work package id
   */
  private Long wpId;
  /**
   * Responsibility Id
   */
  private Long respId;

  public CdfxDlvryWpRespUniqueRespMerge() {

  }

  /**
   * @param cdfxDelWpRespId id
   * @param cdfxDeliveryId cdfx delivery id
   * @param wpId workpackage id
   * @param respId responsibility id
   */
  public CdfxDlvryWpRespUniqueRespMerge(final Long cdfxDelWpRespId, final Long cdfxDeliveryId, final Long wpId, final Long respId) {
    this.cdfxDelWpRespId = cdfxDelWpRespId;
    this.cdfxDeliveryId = cdfxDeliveryId;
    this.wpId = wpId;
    this.respId = respId;
  }

  /**
   * @return the cdfxDelWpRespId
   */
  public Long getCdfxDelWpRespId() {
    return this.cdfxDelWpRespId;
  }


  /**
   * @param cdfxDelWpRespId the cdfxDelWpRespId to set
   */
  public void setCdfxDelWpRespId(final Long cdfxDelWpRespId) {
    this.cdfxDelWpRespId = cdfxDelWpRespId;
  }

  /**
   * @return the cdfxDeliveryId
   */
  public Long getCdfxDeliveryId() {
    return this.cdfxDeliveryId;
  }


  /**
   * @param cdfxDeliveryId the cdfxDeliveryId to set
   */
  public void setCdfxDeliveryId(final Long cdfxDeliveryId) {
    this.cdfxDeliveryId = cdfxDeliveryId;
  }


  /**
   * @return the wpId
   */
  public Long getWpId() {
    return this.wpId;
  }


  /**
   * @param wpId the wpId to set
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }


  /**
   * @return the respId
   */
  public Long getRespId() {
    return this.respId;
  }


  /**
   * @param respId the respId to set
   */
  public void setRespId(final Long respId) {
    this.respId = respId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    CdfxDlvryWpRespUniqueRespMerge other = (CdfxDlvryWpRespUniqueRespMerge) obj;

    return ModelUtil.isEqual(getCdfxDeliveryId(), other.getCdfxDeliveryId()) &&
        ModelUtil.isEqual(getWpId(), other.getWpId()) && ModelUtil.isEqual(getRespId(), other.getRespId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getCdfxDeliveryId(), getWpId(), getRespId());
  }


}
