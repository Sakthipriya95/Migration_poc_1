/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.Map;

/**
 * @author pdh2cob
 */
public class PredefinedAttrValueAndValidtyModel {

  private Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap;

  private Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap;


  /**
   * @return the predefinedAttrValueMap
   */
  public Map<Long, Map<Long, PredefinedAttrValue>> getPredefinedAttrValueMap() {
    return this.predefinedAttrValueMap;
  }


  /**
   * @param predefinedAttrValueMap the predefinedAttrValueMap to set
   */
  public void setPredefinedAttrValueMap(final Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap) {
    this.predefinedAttrValueMap = predefinedAttrValueMap;
  }


  /**
   * @return the predefinedValidityMap
   */
  public Map<Long, Map<Long, PredefinedValidity>> getPredefinedValidityMap() {
    return this.predefinedValidityMap;
  }


  /**
   * @param predefinedValidityMap the predefinedValidityMap to set
   */
  public void setPredefinedValidityMap(final Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap) {
    this.predefinedValidityMap = predefinedValidityMap;
  }


}
