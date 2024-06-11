/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;


import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * This class represents the UI model for the predefined attribute values validity . Validity is defined based on an
 * attribute with one/multiple values
 *
 * @author pdh2cob
 */
public class PredefinedAttrValuesValidityModel {

  private final Attribute validityAttr;

  // This map will have the validity value as value and the PredefinedAttrValuesValidity object as the key
  private final Map<PredfndAttrValsValidityClientBO, AttributeValue> validityValuesMap;

  /**
   * constructor
   *
   * @param attribute validity attr
   * @param attrValues values of validity attr
   */
  public PredefinedAttrValuesValidityModel(final Attribute attribute,
      final Map<PredfndAttrValsValidityClientBO, AttributeValue> attrValues) {

    this.validityAttr = attribute;
    this.validityValuesMap = attrValues;
  }


  /**
   * Get the validity Attribute
   *
   * @return Attribute object
   */
  public Attribute getValidityAttribute() {

    return this.validityAttr;
  }

  /**
   * Get the predefined Attribute Value
   *
   * @return AttributeValue object
   */
  public Map<PredfndAttrValsValidityClientBO, AttributeValue> getValidityAttributeValues() {
    return this.validityValuesMap;
  }


  /**
   * @return the validityValues
   */
  public SortedSet<AttributeValue> getValidityValues() {
    return new TreeSet<AttributeValue>(getValidityAttributeValues().values());
  }


}