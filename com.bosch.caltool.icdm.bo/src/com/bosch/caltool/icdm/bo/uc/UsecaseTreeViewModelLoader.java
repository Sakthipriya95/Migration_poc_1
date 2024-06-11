/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.uc;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;

/**
 * @author mkl2cob
 */
public class UsecaseTreeViewModelLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData ServiceData
   */
  public UsecaseTreeViewModelLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return UsecaseTreeGroupModel
   * @throws DataException Exception while loading Data
   */
  public UsecaseTreeGroupModel getUsecaseTreeGroupModel() throws DataException {
    UsecaseTreeGroupModel treeModel = new UsecaseTreeGroupModel();
    UseCaseGroupLoader groupLoader = new UseCaseGroupLoader(getServiceData());
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    treeModel.getUseCaseGroupMap().putAll(groupLoader.getAll());
    treeModel.getUsecaseMap().putAll(ucLoader.getAll());
    groupLoader.fetchChildrenAndRootGroupsMap(treeModel);
    return treeModel;
  }
}
