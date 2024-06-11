/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;

/**
 * @author svj7cob
 */
public class PreCalibrationDataResponse {

  /**
   * Key : Parameter Id , Value : Statistics Output Model
   */
  private Map<Long, PreCalibrationDataOutput> paramPreCalDataDetails;

  /**
   * @return the paramPreCalDataDetails
   */
  public Map<Long, PreCalibrationDataOutput> getParamPreCalDataDetails() {
    return new HashMap<>(this.paramPreCalDataDetails);
  }


  /**
   * @param paramPreCalDataDetails the paramPreCalDataDetails to set
   */
  public void setParamPreCalDataDetails(final Map<Long, PreCalibrationDataOutput> paramPreCalDataDetails) {
    this.paramPreCalDataDetails = new HashMap<Long, PreCalibrationDataOutput>(paramPreCalDataDetails);
  }


}
