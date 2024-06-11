/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author and4cob
 */
public class AttrNValueDependencyDetails {

  /**
   * key = dep value id, value = AttributeValue object
   */
  private final Map<Long, AttributeValue> attrDepValMap = new HashMap<>();
  /**
   * key = Attribute id, value = referential attribute dependency set
   */
  private final Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = new HashMap<>();
  /**
   * key = Attribute id, value = attribute dependency set
   */
  private final Map<Long, Set<AttrNValueDependency>> attrDependenciesMap = new HashMap<>();
  /**
   * Key - attribute ID. Value - Map of dependencies, with key as value ID and value as attribute dependency object
   */
  private final Map<Long, Map<Long, Set<AttrNValueDependency>>> attrDependenciesMapForAllAttr = new HashMap<>();

  /**
   * @return the depAttrVals
   */
  public Map<Long, AttributeValue> getDepAttrVals() {
    return this.attrDepValMap;
  }

  /**
   * @return the attrRefDependenciesMap
   */
  public Map<Long, Set<AttrNValueDependency>> getAttrRefDependenciesMap() {
    return this.attrRefDependenciesMap;
  }

  /**
   * @return the attrDependenciesMap
   */
  public Map<Long, Set<AttrNValueDependency>> getAttrDependenciesMap() {
    return this.attrDependenciesMap;
  }

  /**
   * @return the depMap
   */
  public Map<Long, Map<Long, Set<AttrNValueDependency>>> getAttrDependenciesMapForAllAttr() {
    return this.attrDependenciesMapForAllAttr;
  }

}
