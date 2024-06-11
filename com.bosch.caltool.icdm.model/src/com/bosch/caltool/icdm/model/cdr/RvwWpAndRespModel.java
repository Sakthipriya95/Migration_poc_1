/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author say8cob
 */
public class RvwWpAndRespModel {

  private Long a2lWpId;

  private Long a2lRespId;


  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.a2lRespId == null) ? 0 : this.a2lRespId.hashCode());
    result = (prime * result) + ((this.a2lWpId == null) ? 0 : this.a2lWpId.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RvwWpAndRespModel other = (RvwWpAndRespModel) obj;
    if (this.a2lRespId == null) {
      if (other.a2lRespId != null) {
        return false;
      }
    }
    else if (!this.a2lRespId.equals(other.a2lRespId)) {
      return false;
    }
    if (this.a2lWpId == null) {
      if (other.a2lWpId != null) {
        return false;
      }
    }
    else if (!this.a2lWpId.equals(other.a2lWpId)) {
      return false;
    }
    return true;
  }


}
