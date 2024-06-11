/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

/**
 * @author bne4cob
 */
public class PTType {

  private Long ptTypeId;
  private String ptTypeName;
  private String ptTypeDesc;

  /**
   * @return the ptTypeId
   */
  public Long getPtTypeId() {
    return this.ptTypeId;
  }

  /**
   * @param ptTypeId the ptTypeId to set
   */
  public void setPtTypeId(final Long ptTypeId) {
    this.ptTypeId = ptTypeId;
  }

  /**
   * @return the ptTypeName
   */
  public String getPtTypeName() {
    return this.ptTypeName;
  }

  /**
   * @param ptTypeName the ptTypeName to set
   */
  public void setPtTypeName(final String ptTypeName) {
    this.ptTypeName = ptTypeName;
  }

  /**
   * @return the ptTypeDesc
   */
  public String getPtTypeDesc() {
    return this.ptTypeDesc;
  }

  /**
   * @param ptTypeDesc the ptTypeDesc to set
   */
  public void setPtTypeDesc(final String ptTypeDesc) {
    this.ptTypeDesc = ptTypeDesc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PTType [ptTypeId=" + this.ptTypeId + ", ptTypeName=" + this.ptTypeName + ", ptTypeDesc=" + this.ptTypeDesc +
        "]";
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    PTType other = (PTType) obj;


    return getPtTypeId() == other.getPtTypeId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((getPtTypeId() == null) ? 0 : getPtTypeId().hashCode());
    return result;
  }
}
