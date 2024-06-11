/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class VariantMapClientModel implements Comparable<VariantMapClientModel> {

  // variant name
  private String variantName;
  // variant description
  private String variantDesc;

  private boolean isMapped;

  private String otherVarGroupName;

  private Long variantId;


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }


  /**
   * @return the variantDesc
   */
  public String getVariantDesc() {
    return this.variantDesc;
  }


  /**
   * @param variantDesc the variantDesc to set
   */
  public void setVariantDesc(final String variantDesc) {
    this.variantDesc = variantDesc;
  }


  /**
   * @return the isMapped
   */
  public boolean isMapped() {
    return this.isMapped;
  }


  /**
   * @param isMapped the isMapped to set
   */
  public void setMapped(final boolean isMapped) {
    this.isMapped = isMapped;
  }


  /**
   * @return the otherVarGroupName
   */
  public String getOtherVarGroupName() {
    return this.otherVarGroupName;
  }


  /**
   * @param otherVarGroupName the otherVarGroupName to set
   */
  public void setOtherVarGroupName(final String otherVarGroupName) {
    this.otherVarGroupName = otherVarGroupName;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getVariantId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return ModelUtil.isEqual(getVariantId(), ((VariantMapClientModel) obj).getVariantId());
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VariantMapClientModel obj) {
    // if mapped , compare name for second level sorting
    if (obj.isMapped() == isMapped()) {
      return ApicUtil.compare(getVariantName(), obj.getVariantName());
    }
    return ApicUtil.compare(obj.isMapped(), isMapped());
  }

}
