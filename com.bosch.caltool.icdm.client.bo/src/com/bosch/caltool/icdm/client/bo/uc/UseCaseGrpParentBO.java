/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.List;

import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;

/**
 * @author APJ4COB
 */
public class UseCaseGrpParentBO {

  /**
   * Method that finds all the parent usecasegroup and it in hierarichy order
   * 
   * @param useCaseGroup UseCaseGroup
   * @param useCaseItemClientBOs IUseCaseItemClientBO
   * @param useCaseTreeModel UsecaseTreeGroupModel
   */
  public void getUseCaseGroupClientBORecursive(final UseCaseGroup useCaseGroup,
      final List<IUseCaseItemClientBO> useCaseItemClientBOs, final UsecaseTreeGroupModel useCaseTreeModel) {
    UseCaseGroupClientBO parentUseCaseGroupClientBO = null;
    UseCaseGroup parentUseCaseGrp = null;
    if (null != useCaseGroup.getParentGroupId()) {
      parentUseCaseGrp = useCaseTreeModel.getUseCaseGroupMap().get(useCaseGroup.getParentGroupId());
      parentUseCaseGroupClientBO = new UseCaseGroupClientBO(parentUseCaseGrp, useCaseTreeModel, null);
      getUseCaseGroupClientBORecursive(parentUseCaseGrp, useCaseItemClientBOs, useCaseTreeModel);
    }
    useCaseItemClientBOs.add(new UseCaseGroupClientBO(useCaseGroup, useCaseTreeModel, parentUseCaseGroupClientBO));
  }

}
