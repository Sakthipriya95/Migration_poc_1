/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.common.exception.DataException;

/**
 * @author UKT1COB
 */
public class CDRResultParamBO {

  /**
   * @param cdrResultParameter
   * @param paramId
   * @throws DataException
   */
  public Set<String> getReadOnlyParamNameSet(final A2LFileInfo a2lFileInfo) {
    return a2lFileInfo.getAllModulesLabels().values().stream().filter(Characteristic::isReadOnly)
        .map(Characteristic::getName).collect(Collectors.toSet());
  }

  /**
   * @param a2lFileInfo A2LFileInfo
   * @return Map<String, List<String>> - Key - Parameter Name, Value -List of Depends on Param
   */
  public Map<String, List<String>> getDepParamMapForA2L(final A2LFileInfo a2lFileInfo) {
    return a2lFileInfo.getAllModulesLabels().values().stream().filter(Characteristic::isDependentCharacteristic)
        .collect(Collectors.toMap(Characteristic::getName, depChar -> getDepCharNamesList(depChar)));
  }


  private List<String> getDepCharNamesList(final Characteristic characteristic) {
    // Each dependent characteristics or label in A2lfile will have 1 to n characteristics used to create formula
    return Arrays.asList(characteristic.getDependentCharacteristic().getCharacteristicName());
  }

}
