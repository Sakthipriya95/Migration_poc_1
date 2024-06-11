/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.IBasicObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents the usecase root node
 *
 * @author adn1cob
 */
public class UseCaseRootNodeOld implements IBasicObject {

  /**
   * data provider
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * @param apicDataProvider dataprovider
   */
  public UseCaseRootNodeOld(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }

  /**
   * @param includeDeleted whether to include the deleted use case groups
   * @return the topLevelUCGSet
   */
  public SortedSet<UseCaseGroup> getUseCaseGroups(final boolean includeDeleted) {

    final SortedSet<UseCaseGroup> retSet =
        new TreeSet<UseCaseGroup>(this.apicDataProvider.getDataCache().getTopLevelUCGSet());

    if (includeDeleted) {
      return retSet;
    }

    // Remove the deleted groups from the set
    for (UseCaseGroup ucg : this.apicDataProvider.getTopLevelUCGSet()) {
      if (ucg.isDeleted()) {
        retSet.remove(ucg);
      }
    }
    return retSet;

  }

  /**
   * @return rootname
   */
  @Override
  public String getName() {
    return ApicConstants.USE_CASE_ROOT_NODE_NAME;
  }

  // iCDM-350
  /**
   * @return description
   */
  @Override
  public String getDescription() {
    return ApicConstants.USECASE_ROOT_DESC;
  }

  /**
   * @return tooltip of this node
   */
  @Override
  public String getToolTip() {
    return getDescription();
  }

}
