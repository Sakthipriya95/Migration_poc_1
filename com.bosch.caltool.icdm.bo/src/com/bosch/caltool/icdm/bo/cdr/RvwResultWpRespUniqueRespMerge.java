/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class RvwResultWpRespUniqueRespMerge {

  private Long rvwResultWpRespId;

  private Long resultId;

  private Long wpId;

  private Long respId;

  public RvwResultWpRespUniqueRespMerge() {

  }


  /**
   * @param rvwResultWpRespId rvwResultWpRespId
   * @param resultId rvw result id
   * @param wpId work package id
   * @param respId resp id
   */
  public RvwResultWpRespUniqueRespMerge(final Long rvwResultWpRespId, final Long resultId, final Long wpId,
      final Long respId) {
    this.rvwResultWpRespId = rvwResultWpRespId;
    this.resultId = resultId;
    this.wpId = wpId;
    this.respId = respId;
  }


  /**
   * @return the rvwResultWpRespId
   */
  public Long getRvwResultWpRespId() {
    return this.rvwResultWpRespId;
  }


  /**
   * @param rvwResultWpRespId the rvwResultWpRespId to set
   */
  public void setRvwResultWpRespId(final Long rvwResultWpRespId) {
    this.rvwResultWpRespId = rvwResultWpRespId;
  }


  /**
   * @return the resultId
   */
  public Long getResultId() {
    return this.resultId;
  }


  /**
   * @param resultId the resultId to set
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
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
    RvwResultWpRespUniqueRespMerge other = (RvwResultWpRespUniqueRespMerge) obj;

    return ModelUtil.isEqual(getResultId(), other.getResultId()) && ModelUtil.isEqual(getWpId(), other.getWpId()) &&
        ModelUtil.isEqual(getRespId(), other.getRespId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getResultId(), getWpId(), getRespId());
  }
}
