/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author bne4cob
 */
public class DataRefreshInput {

  /**
   * Key - model type code<br>
   * Value - Set of IDs
   */
  private Map<String, Set<Long>> input = new HashMap<>();


  /**
   * @return the inputMap
   */
  public Map<String, Set<Long>> getInput() {
    return this.input;
  }

  /**
   * @param inputMap the inputMap to set
   */
  public void setInput(final Map<String, Set<Long>> inputMap) {
    this.input = inputMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DataRefreshInput [input=" + this.input + "]";
  }


}