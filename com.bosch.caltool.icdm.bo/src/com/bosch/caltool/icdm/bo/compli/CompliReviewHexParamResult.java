/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;

/**
 * @author dmr1cob
 */
public class CompliReviewHexParamResult {

  /*
   * Key - Hex File Index, value - ( Key - Param Name Value - CompliRvwHexParam obj)
   */
  private Map<Long, Map<String, CompliRvwHexParam>> compliRvwHexParamMap = new HashMap<>();

  /**
   * @return the compliRvwHexParamMap
   */
  public Map<Long, Map<String, CompliRvwHexParam>> getCompliRvwHexParamMap() {
    return this.compliRvwHexParamMap;
  }


  /**
   * @param compliRvwHexParamMap the compliRvwHexParamMap to set
   */
  public void setCompliRvwHexParamMap(final Map<Long, Map<String, CompliRvwHexParam>> compliRvwHexParamMap) {
    this.compliRvwHexParamMap = compliRvwHexParamMap;
  }
}
