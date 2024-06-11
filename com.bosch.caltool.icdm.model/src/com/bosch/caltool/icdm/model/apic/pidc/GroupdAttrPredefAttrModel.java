/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Map;

import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;

/**
 * Collection of grouped attribute , predefined attribute and predefined attribute value for PIDC grouped attribute
 * dialog
 *
 * @author DJA7COB
 */
public class GroupdAttrPredefAttrModel {

  private final IProjectAttribute groupedAttribute;

  private final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap;

  /**
   * @param groupedAttribute grouped attribute with changes
   * @param predefAttrValMap
   */
  public GroupdAttrPredefAttrModel(final IProjectAttribute groupedAttribute,
      final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap) {
    this.groupedAttribute = groupedAttribute;
    this.predefAttrValMap = predefAttrValMap;
  }

  /**
   * @return the groupedAttribute
   */
  public IProjectAttribute getGroupedAttribute() {
    return this.groupedAttribute;
  }

  /**
   * @return the predefAttrValMap
   */
  public Map<IProjectAttribute, PredefinedAttrValue> getPredefAttrValMap() {
    return this.predefAttrValMap;
  }
}
