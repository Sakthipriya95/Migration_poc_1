/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;


/**
 * Pidc Search Condition
 *
 * @author bne4cob
 */
// ICDM-2326
public class PidcSearchCondition {

  /**
   * Attribute ID
   */
  private Long attributeId;

  /**
   * Used flag, if used flag based search
   */
  private String usedFlag;

  /**
   * Attribute Value IDs
   */
  private Set<Long> attributeValueIds;

  /**
   * Creates a new search condition for the given attribute ID
   */
  public PidcSearchCondition() {
    super();
    this.attributeValueIds = new HashSet<>();
  }


  /**
   * @return Attribute ID
   */
  public Long getAttributeId() {
    return this.attributeId;
  }


  /**
   * @param attributeId the Attribute ID to set
   */
  public void setAttributeId(final Long attributeId) {
    this.attributeId = attributeId;
  }


  /**
   * @return the used Flag
   */
  public String getUsedFlag() {
    return this.usedFlag;
  }


  /**
   * @param usedFlag the used Flag to set
   */
  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }


  /**
   * @return Set of values IDs
   */
  public Set<Long> getAttributeValueIds() {
    return this.attributeValueIds;
  }


  /**
   * @param attributeValueIds Set of values IDs
   */
  public void setAttributeValueIds(final Set<Long> attributeValueIds) {
    this.attributeValueIds = attributeValueIds == null ? null : new HashSet<>(attributeValueIds);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PidcSearchCondition [attributeId=" + this.attributeId + ", usedFlag=" + this.usedFlag +
        ", attributeValueIds=" + this.attributeValueIds + "]";
  }

}
