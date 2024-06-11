/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.services;

import java.util.Map;

import com.bosch.calmodel.caldata.CalData;

/**
 * @author bru2cob
 */
public interface IA2lParamTable {

  /**
   * Adds the caldata objects to table
   * @param calDataMap
   */
  void addPreCalibDataToA2l(final Map<String, CalData> calDataMap);
}
