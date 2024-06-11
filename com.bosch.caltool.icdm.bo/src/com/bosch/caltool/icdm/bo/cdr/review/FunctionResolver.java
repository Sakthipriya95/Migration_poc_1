/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author bru2cob
 */
public class FunctionResolver implements IReviewParamResolver {

  ConcurrentMap<String, String> labelFunMap;

  SortedSet<Function> reviewFuncsList;

  /**
   * @param labelFunMap
   * @param reviewFuncsList
   */
  public FunctionResolver(final ConcurrentMap<String, String> labelFunMap, final SortedSet<Function> reviewFuncsList) {
    this.labelFunMap = labelFunMap;
    this.reviewFuncsList = reviewFuncsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getParameters() throws IcdmException {
    List<String> paramsList = new ArrayList<String>();
    for (Function function : this.reviewFuncsList) {
      List<DefCharacteristic> paramList = function.getDefCharRefList();
      if ((paramList != null) && (!paramList.isEmpty())) {
        for (DefCharacteristic defCharacteristic : paramList) {
          this.labelFunMap.put(defCharacteristic.getName(), function.getName());
        }
      }

    }
    Set<String> keySet = new HashSet<>(this.labelFunMap.keySet());
    paramsList.addAll(keySet);
    return paramsList;
  }

}
