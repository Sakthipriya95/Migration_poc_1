/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;

/**
 * The Class A2LDataBO.
 *
 * @author gge6cob
 */
public class A2LDataBO {

  /**
   * Gets the a 2 l system constants.
   *
   * @return the a 2 l system constants
   */
  public Map<String, A2LSystemConstant> getA2lSystemConstants() {
    return A2lDataCache.INSTANCE.getA2lSystemConstantsMap();
  }

  /**
   * Gets the system constant details.
   *
   * @param sysConstSet system constants
   * @return A2LSystemConstantValues// ICDM-205
   */
  public SortedSet<A2LSystemConstantValues> getSystemConstantDetails(final Set<SystemConstant> sysConstSet) {

    // ICDM-2627
    SortedSet<A2LSystemConstantValues> retSet = new TreeSet<>();

    String sysKonName;
    String sysKonVal;

    int newSysConst = 0;
    int newSysConstVal = 0;
    for (SystemConstant systemConstant : sysConstSet) {

      // Get system constant object
      sysKonName = systemConstant.getName();
      A2LSystemConstant a2lSysConst = A2lDataCache.INSTANCE.getA2lSystemConstantsMap().get(sysKonName);
      if (a2lSysConst == null) {
        // New system constant found
        a2lSysConst = new A2LSystemConstant();
        a2lSysConst.setSysconName(systemConstant.getName());
        A2lDataCache.INSTANCE.getA2lSystemConstantsMap().put(sysKonName, a2lSysConst);
        newSysConst++;
      }

      // Get value object
      sysKonVal = systemConstant.getValue();
      String key = a2lSysConst.getSysconName() + ":" + sysKonVal;
      A2LSystemConstantValues a2lSysConstVal = a2lSysConst.getSysConValues(key);

      if (a2lSysConstVal == null) {
        // new value found
        a2lSysConstVal = new A2LSystemConstantValues();
        a2lSysConstVal.setValue(sysKonVal);
        a2lSysConstVal.setSysconName(a2lSysConst.getSysconName());
        a2lSysConstVal.setSysconLongName(a2lSysConst.getLongName());
        a2lSysConst.getSysConValues().put(key, a2lSysConstVal);
        newSysConstVal++;
      }
      retSet.add(a2lSysConstVal);
    }
    CDMLogger.getInstance().debug(
        "Total iCDM system constants - {}; New system constants count - {}; New values count - {}", retSet.size(),
        newSysConst, newSysConstVal);
    return retSet;
  }
}
