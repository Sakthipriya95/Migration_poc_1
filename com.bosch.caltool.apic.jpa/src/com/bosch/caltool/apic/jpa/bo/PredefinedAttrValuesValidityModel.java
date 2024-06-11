/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * This class represents the UI model for the predefined attribute values validity . Validity is defined based on an
 * attribute with one/multiple values
 *
 * @author dmo5cob
 */
@Deprecated
public class PredefinedAttrValuesValidityModel {

  private final Attribute validityAttr;

  // This map will have the validity value as value and the PredefinedAttrValuesValidity object as the key
  private final Map<PredefinedAttrValuesValidity, AttributeValue> validityValuesMap;

  /**
   * constructor
   *
   * @param attribute validity attr
   * @param attrValues values of validity attr
   */
  public PredefinedAttrValuesValidityModel(final Attribute attribute,
      final Map<PredefinedAttrValuesValidity, AttributeValue> attrValues) {

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
  public Map<PredefinedAttrValuesValidity, AttributeValue> getValidityAttributeValues() {
    return this.validityValuesMap;
  }


  /**
   * @return the validityValues
   */
  public SortedSet<AttributeValue> getValidityValues() {
    return new TreeSet<AttributeValue>(getValidityAttributeValues().values());
  }


}