/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class A2lWPRespModel implements Comparable<A2lWPRespModel> {

  private Long wpRespId;

  private Long a2lRespId;

  private Long a2lWpId;

  private boolean inheritedFlag;
  
  /**
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }


  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWPRespModel o) {
    int value = ModelUtil.compare(getA2lWpId(), o.getA2lWpId());
    if (value == 0) {
      return ModelUtil.compare(getA2lRespId(), o.getA2lRespId());
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) 
        && ModelUtil.isEqual(getWpRespId(), ((A2lWPRespModel) obj).getWpRespId()) 
        && ModelUtil.isEqual(getA2lRespId(), ((A2lWPRespModel) obj).getA2lRespId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getA2lRespId());
  }


  
  /**
   * @return the inheritedFlag
   */
  public boolean isInheritedFlag() {
    return inheritedFlag;
  }


  
  /**
   * @param inheritedFlag the inheritedFlag to set
   */
  public void setInheritedFlag(boolean inheritedFlag) {
    this.inheritedFlag = inheritedFlag;
  }

}
