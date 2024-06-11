/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author rgo7cob
 */
public class CompliParamOutput {


  private final Map<String, String> compliParamMap = new TreeMap<>();


  /**
   * @return the compliParamMap
   */
  public Map<String, String> getCompliParamMap() {
    return this.compliParamMap;
  }


}
