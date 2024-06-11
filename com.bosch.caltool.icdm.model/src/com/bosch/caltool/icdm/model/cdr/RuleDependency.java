/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;


/**
 * @author dmo5cob
 */
public class RuleDependency implements Comparable<RuleDependency> {

  private SortedSet<AttributeValueModel> attrValues = new TreeSet<>();

  /**
   * For mapping Attribute from row to Attribute in AttributeValueModel
   */
  private final Map<Attribute, AttributeValueModel> attrAttrValModelMap = new HashMap<>();

  private IParameter param;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleDependency arg0) {
    // TODO Auto-generated method stub
    return 1;
  }


  /**
   * @return the attrValue
   */
  public SortedSet<AttributeValueModel> getAttrValues() {
    return this.attrValues;
  }


  /**
   * @param attrValue the attrValue to set
   */
  public void setAttrValues(final SortedSet<AttributeValueModel> attrValue) {
    this.attrValues = attrValue;
  }


  /**
   * @return the param
   */
  public IParameter getParam() {
    return this.param;
  }


  /**
   * @return the attrAttrValModelMap
   */
  public Map<Attribute, AttributeValueModel> getAttrAttrValModelMap() {
    return this.attrAttrValModelMap;
  }


  /**
   * Add attribute value model
   *
   * @param attrVal AttributeValueModel
   */
  public void addAttrVal(final AttributeValueModel attrVal) {
    this.attrValues.add(attrVal);
    this.attrAttrValModelMap.put(attrVal.getAttr(), attrVal);

  }


}
