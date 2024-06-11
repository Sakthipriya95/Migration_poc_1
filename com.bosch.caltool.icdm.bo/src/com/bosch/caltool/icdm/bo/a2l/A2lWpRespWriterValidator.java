/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.bosch.ice.ara2l.a2lparserapi.aom.Group;
import com.bosch.ice.ara2l.a2lparserapi.aom.Module;

/**
 * @author rgo7cob
 */
public class A2lWpRespWriterValidator {

  /**
   * @param a2lModule
   * @return
   */
  public boolean isRootGrpResporWp(final Module a2lModule, final String rootGrpName) {
    Collection<Group> groups = a2lModule.getGroups();
    for (Group group : groups) {
      if (group.isRoot() && group.getName().equals(rootGrpName)) {
        return true;
      }

    }
    return false;
  }


  /**
   * @param workPackageLabelMap
   * @param groupList
   * @param respLabelMap
   * @return
   */
  public boolean isWpRespAvailable(final Map<String, Set<String>> workPackageLabelMap,
      final Collection<Group> groupList, final Map<String, Set<String>> respLabelMap) {

    for (Group group : groupList) {
      if (workPackageLabelMap.containsKey(group.getName())) {
        return true;
      }
      if (respLabelMap.containsKey(group.getName())) {
        return true;
      }
    }
    return false;
  }


}
