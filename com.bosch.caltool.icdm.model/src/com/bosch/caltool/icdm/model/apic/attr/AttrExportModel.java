/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ELM1COB
 */
public class AttrExportModel {

  /**
   * Map<Long - AttributeId, Map<Long - AttributevalueId, AttributeValue>>
   */
  private Map<Long, Map<Long, AttributeValue>> allAttrValuesMap = new HashMap<>();

  /**
   * AttributeGroupModel object
   */
  private AttrGroupModel attrGroup;

  /**
   * @return the allAttrValuesMap
   */
  public Map<Long, Map<Long, AttributeValue>> getAllAttrValuesMap() {
    return this.allAttrValuesMap;
  }


  /**
   * @param allAttrValuesMap the allAttrValuesMap to set
   */
  public void setAllAttrValuesMap(final Map<Long, Map<Long, AttributeValue>> allAttrValuesMap) {
    this.allAttrValuesMap = allAttrValuesMap;
  }


  /**
   * @return the attrGroup
   */
  public AttrGroupModel getAttrGroup() {
    return this.attrGroup;
  }


  /**
   * @param attrGroup the attrGroup to set
   */
  public void setAttrGroup(final AttrGroupModel attrGroup) {
    this.attrGroup = attrGroup;
  }


}
