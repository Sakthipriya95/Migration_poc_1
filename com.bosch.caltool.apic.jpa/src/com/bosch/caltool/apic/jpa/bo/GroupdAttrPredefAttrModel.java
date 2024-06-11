/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;

/**
 * Collection of grouped attribute , predefined attribute and predefined attribute value for PIDC grouped attribute
 * dialog
 *
 * @author DJA7COB
 */
public class GroupdAttrPredefAttrModel {

  private final IPIDCAttribute groupedAttribute;

  private final Map<IPIDCAttribute, PredefinedAttrValue> predefAttrValMap;

  /**
   * @param groupedAttribute grouped attribute with changes
   * @param predefAttrValMap
   */
  public GroupdAttrPredefAttrModel(final IPIDCAttribute groupedAttribute,
      final Map<IPIDCAttribute, PredefinedAttrValue> predefAttrValMap) {
    this.groupedAttribute = groupedAttribute;
    this.predefAttrValMap = predefAttrValMap;
  }

  /**
   * @return the groupedAttribute
   */
  public IPIDCAttribute getGroupedAttribute() {
    return this.groupedAttribute;
  }

  /**
   * @return the predefAttrValMap
   */
  public Map<IPIDCAttribute, PredefinedAttrValue> getPredefAttrValMap() {
    return this.predefAttrValMap;
  }
}
