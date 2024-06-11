/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;

/**
 * Class for the pre calibration data input
 *
 * @author svj7cob
 */
// Task 243510
public class PreCalibrationDataInput {

  /**
   * Pidc Search condition set
   */
  private final Set<PidcSearchCondition> pidcSearchConditionSet = new HashSet<>();

  /**
   * Given parameter set
   */
  private final Set<Long> parameterIdSet = new HashSet<>();


  /**
   * @return the pidcSearchConditionSet
   */
  public Set<PidcSearchCondition> getPidcSearchConditionSet() {
    return new HashSet<>(this.pidcSearchConditionSet);
  }


  /**
   * @param pidcSearchConditionSet the pidcSearchConditionSet to set
   */
  public void setPidcSearchConditionSet(final Set<PidcSearchCondition> pidcSearchConditionSet) {
    if (null != pidcSearchConditionSet) {
      this.pidcSearchConditionSet.clear();
      this.pidcSearchConditionSet.addAll(pidcSearchConditionSet);
    }
  }


  /**
   * @return the parameterIdSet
   */
  public Set<Long> getParameterIdSet() {
    return new HashSet<>(this.parameterIdSet);
  }


  /**
   * @param parameterIdSet the parameterIdSet to set
   */
  public void setParameterIdSet(final Set<Long> parameterIdSet) {
    if (null != parameterIdSet) {
      this.parameterIdSet.clear();
      this.parameterIdSet.addAll(parameterIdSet);
    }
  }


}
