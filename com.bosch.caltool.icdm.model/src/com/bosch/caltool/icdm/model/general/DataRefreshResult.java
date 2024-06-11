/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author bne4cob
 */
public class DataRefreshResult {

  /**
   * Key : model type code<br>
   * Value : Key - ID, value - refresh status
   */
  private Map<String, Map<Long, String>> refreshStatus = new TreeMap<>();

  /**
   * @return the refreshStatus
   */
  public Map<String, Map<Long, String>> getRefreshStatus() {
    return this.refreshStatus;
  }

  /**
   * @param refreshStatus the refreshStatus to set
   */
  public void setRefreshStatus(final Map<String, Map<Long, String>> refreshStatus) {
    this.refreshStatus = refreshStatus;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DataRefreshResult [refreshStatus=" + this.refreshStatus + "]";
  }

}
