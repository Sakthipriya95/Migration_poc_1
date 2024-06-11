/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.testdata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author bne4cob
 */
public class InputData {

  /**
   * SB_START_LENGTH
   */
  private static final int SB_START_LENGTH = 50;


  /**
   * Reference class
   */
  private final String refClass;

  /**
   * Map of parameters
   */
  private final Map<String, String> params = new ConcurrentHashMap<>();

  /**
   * @param refClass referring bean class
   */
  public InputData(final String refClass) {
    this.refClass = refClass;
  }

  /**
   * @return the refClass
   */
  public String getRefClass() {
    return this.refClass;
  }


  /**
   * @return the params
   */
  public Map<String, String> getParams() {
    return this.params;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(SB_START_LENGTH);
    builder.append("TestData [refClass=").append(this.refClass).append(", params=").append(this.params).append(']');
    return builder.toString();
  }

}
