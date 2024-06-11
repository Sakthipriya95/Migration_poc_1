/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l.precal;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author bne4cob
 */
public class PreCalAttrValResponse {

  /**
   * Key - Attribute ID, Value - attribute
   */
  private Map<Long, Attribute> attrMap = new HashMap<>();
  /**
   * Key - Attribute ID, Value - pidc attribute
   */
  private Map<Long, PidcVersionAttribute> pidcAttrMap = new HashMap<>();
  /**
   * Key - Attribute ID, Value - variant attribute
   */
  private Map<Long, PidcVariantAttribute> pidcVarAttrMap = new HashMap<>();

  /**
   * Key - value ID, value - attribute value
   */
  private Map<Long, AttributeValue> attrValMap = new HashMap<>();

  /**
   * @return the attrMap
   */
  public Map<Long, Attribute> getAttrMap() {
    return this.attrMap;
  }


  /**
   * @param attrMap the attrMap to set
   */
  public void setAttrMap(final Map<Long, Attribute> attrMap) {
    this.attrMap = attrMap;
  }


  /**
   * @return the pidcAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrMap() {
    return this.pidcAttrMap;
  }


  /**
   * @param pidcAttrMap the pidcAttrMap to set
   */
  public void setPidcAttrMap(final Map<Long, PidcVersionAttribute> pidcAttrMap) {
    this.pidcAttrMap = pidcAttrMap;
  }


  /**
   * @return the pidcVarAttrMap
   */
  public Map<Long, PidcVariantAttribute> getPidcVarAttrMap() {
    return this.pidcVarAttrMap;
  }


  /**
   * @param pidcVarAttrMap the pidcVarAttrMap to set
   */
  public void setPidcVarAttrMap(final Map<Long, PidcVariantAttribute> pidcVarAttrMap) {
    this.pidcVarAttrMap = pidcVarAttrMap;
  }


  /**
   * @return the attrValMap
   */
  public Map<Long, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }


  /**
   * @param attrValMap the attrValMap to set
   */
  public void setAttrValMap(final Map<Long, AttributeValue> attrValMap) {
    this.attrValMap = attrValMap;
  }

}
