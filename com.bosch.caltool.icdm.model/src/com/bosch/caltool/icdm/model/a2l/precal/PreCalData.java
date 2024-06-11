/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l.precal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bne4cob
 */
public class PreCalData {

  /**
   * Key- parameter name, Value - cal data object, serialized and zipped
   */
  private Map<String, byte[]> preCalDataMap = new HashMap<>();

  /**
   * @return the preCalDataMap
   */
  public Map<String, byte[]> getPreCalDataMap() {
    return this.preCalDataMap;
  }

  /**
   * @param preCalDataMap the preCalDataMap to set
   */
  public void setPreCalDataMap(final Map<String, byte[]> preCalDataMap) {
    this.preCalDataMap = preCalDataMap;
  }

}
