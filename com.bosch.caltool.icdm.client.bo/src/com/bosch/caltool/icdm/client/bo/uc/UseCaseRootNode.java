/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;


/**
 * This class represents the usecase root node
 *
 * @author adn1cob
 */
public class UseCaseRootNode {

  /**
   * set of uc group client bo's
   */
  private SortedSet<UseCaseGroupClientBO> rootGroupsSet;
  private final UseCaseDataHandler ucDataHandler;
  private UsecaseTreeGroupModel ucTreeModel;


  /**
   * Constructor
   *
   * @param ucDataHandler
   */
  public UseCaseRootNode(final UseCaseDataHandler ucDataHandler) {
    this.ucDataHandler = ucDataHandler;
  }

  /**
   * @param ucTreeModel
   */
  public UseCaseRootNode(final UsecaseTreeGroupModel ucTreeModel) {
    this.ucTreeModel = ucTreeModel;
    this.ucDataHandler = null;
  }

  /**
   * @param includeDeleted whether to include the deleted use case groups
   * @return the topLevelUCGSet
   */
  public SortedSet<UseCaseGroupClientBO> getUseCaseGroups(final boolean includeDeleted) {

    if (null == this.rootGroupsSet) { // fill the root group set for the first time
      this.rootGroupsSet = new TreeSet<>();
      if (null != this.ucTreeModel) {
        for (Long rootUCGId : this.ucTreeModel.getRootUCGSet()) {
          UseCaseGroupClientBO ucgBo =
              new UseCaseGroupClientBO(this.ucTreeModel.getUseCaseGroupMap().get(rootUCGId), this.ucTreeModel, null);
          this.rootGroupsSet.add(ucgBo);
        }
      }
      if (null != this.ucDataHandler) {
        for (Long rootUCGId : this.ucDataHandler.getUseCaseDetailsModel().getRootUCGSet()) {
          UseCaseGroupClientBO ucgBo =
              new UseCaseGroupClientBO(this.ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(rootUCGId),
                  this.ucDataHandler.getUseCaseDetailsModel(), null);
          this.rootGroupsSet.add(ucgBo);
        }
      }
    }

    if (includeDeleted) {
      // return all the top level groups
      return this.rootGroupsSet;
    }

    SortedSet<UseCaseGroupClientBO> retSet = new TreeSet<>();
    retSet.addAll(this.rootGroupsSet);
    // Remove the deleted groups from the set
    for (UseCaseGroupClientBO ucg : this.rootGroupsSet) {
      if (ucg.isDeleted()) {
        retSet.remove(ucg);
      }
    }
    return retSet;

  }

  /**
   * @param includeDeleted
   * @return
   */
  public SortedSet<IUseCaseItem> getUseCaseGroupsSorted(final boolean includeDeleted) {

    SortedSet<IUseCaseItem> rootGroupSet = new TreeSet<>();
    if (null != this.ucDataHandler) {
      for (Long rootUCGId : this.ucDataHandler.getUseCaseDetailsModel().getRootUCGSet()) {

        rootGroupSet.add(this.ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(rootUCGId));
      }
    }
    if (includeDeleted) {
      // return all the top level groups
      return rootGroupSet;
    }

    SortedSet<IUseCaseItem> retSet = new TreeSet<>();
    retSet.addAll(rootGroupSet);
    // Remove the deleted groups from the set
    for (IUseCaseItem ucg : rootGroupSet) {
      if (ucg.isDeleted()) {
        retSet.remove(ucg);
      }
    }
    return retSet;

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

  /**
   * @return tooltip of this node
   */
  public String getToolTip() {
    return getDescription();
  }

  /**
   * Refresh based on the change
   */
  public void refresh() {
    if (null != this.ucDataHandler) {
      UseCaseGroupServiceClient ucgServiceClient = new UseCaseGroupServiceClient();
      try {
        UsecaseDetailsModel useCaseDetailsModel = ucgServiceClient.getUseCaseDetailsModel();
        this.ucDataHandler.setUseCaseDetailsModel(useCaseDetailsModel);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp);
      }
      this.rootGroupsSet = null;
      getUseCaseGroupsSorted(false);

    }
  }


}
