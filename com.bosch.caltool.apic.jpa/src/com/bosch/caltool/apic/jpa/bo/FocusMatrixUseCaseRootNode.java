/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents the usecase root node in focus matrix tree view ICDM-1557
 * 
 * @author dmo5cob
 */
public class FocusMatrixUseCaseRootNode {

  private final ApicDataProvider apicDataProvider;

  /**
   * @param apicDataProvider dataprovider
   */
  public FocusMatrixUseCaseRootNode(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }

  /**
   * @return the topLevelUCGSet
   */
  public SortedSet<UseCaseGroup> getUseCaseGroups() {

    SortedSet<UseCaseGroup> resultSet = new TreeSet<UseCaseGroup>();
    for (UseCaseGroup ucg : this.apicDataProvider.getTopLevelUCGSet()) {
      if (!ucg.getChildFocusMatrixItems().isEmpty() && !ucg.isDeleted()) {
        addParentGroups(ucg, resultSet);
      }
    }
    return resultSet;
  }

  /**
   * @param useCaseGrp
   * @param sectionSet
   */
  private SortedSet<UseCaseGroup> addParentGroups(final UseCaseGroup useCaseGrp, final SortedSet<UseCaseGroup> resultSet) {
    if ((useCaseGrp.getParent() instanceof UseCaseGroup) &&
        !((UseCaseGroup) useCaseGrp.getParent()).getChildGroupSet(false).isEmpty()) {
      addParentGroups((UseCaseGroup) useCaseGrp.getParent(), resultSet);
    }
    else {
      resultSet.add(useCaseGrp);
    }
    return resultSet;
  }


  /**
   * @return rootname
   */
  public String getName() {
    return ApicConstants.USE_CASE_ROOT_NODE_NAME;
  }

  // iCDM-350
  /**
   * @return description
   */
  public String getDescription() {
    return ApicConstants.USECASE_ROOT_DESC;
  }

}
