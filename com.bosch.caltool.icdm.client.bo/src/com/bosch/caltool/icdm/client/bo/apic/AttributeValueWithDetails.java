/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author nip4cob
 */
public class AttributeValueWithDetails {

  /**
   * set of attribute values
   */
  private Set<AttributeValue> attrValset;
  /**
   * Map of valid values
   */
  private Map<Long, Boolean> validValMap = new HashMap<>();


  /**
   * @return the attrValset
   */
  public Set<AttributeValue> getAttrValset() {
    return this.attrValset;
  }


  /**
   * @param attrValset the attrValset to set
   */
  public void setAttrValset(final Set<AttributeValue> attrValset) {
    this.attrValset = attrValset;
  }


  /**
   * @return the validValMap
   */
  public Map<Long, Boolean> getValidValMap() {
    return this.validValMap;
  }


  /**
   * @param validValMap the validValMap to set
   */
  public void setValidValMap(final Map<Long, Boolean> validValMap) {
    this.validValMap = validValMap;
  }
}
