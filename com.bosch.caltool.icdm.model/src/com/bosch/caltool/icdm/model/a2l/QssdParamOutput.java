/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;

/**
 * model for QssdParams
 *
 * @author hnu1cob
 */
public class QssdParamOutput {

  /**
   * qssdParamMap- Map of qssd parameters.key - param name,value-ptype
   */
  private Map<String, String> qssdParamMap = new HashMap<>();


  /**
   * @return the qssdParamMap
   */
  public Map<String, String> getQssdParamMap() {
    return this.qssdParamMap;
  }

  /**
   * @param qssdParamMap the qssdParamMap to set
   */
  public void setQssdParamMap(final Map<String, String> qssdParamMap) {
    this.qssdParamMap = qssdParamMap;
  }
}
