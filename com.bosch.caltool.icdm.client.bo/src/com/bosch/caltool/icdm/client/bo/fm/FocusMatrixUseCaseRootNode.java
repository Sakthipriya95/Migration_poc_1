/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents the usecase root node in focus matrix tree view ICDM-1557
 *
 * @author dmo5cob
 */
public class FocusMatrixUseCaseRootNode {


  /**
   * UseCaseDataHandler
   */
  private final SortedSet<FavUseCaseItemNode> favUCGroups;

  /**
   * @param projectFavNodes UseCaseDataHandler
   */
  public FocusMatrixUseCaseRootNode(final SortedSet<FavUseCaseItemNode> projectFavNodes) {
    this.favUCGroups = projectFavNodes;

  }

  /**
   * @return the topLevelUCGSet
   */
  public SortedSet<FavUseCaseItemNode> getUseCaseGroups() {
    return this.favUCGroups;
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
