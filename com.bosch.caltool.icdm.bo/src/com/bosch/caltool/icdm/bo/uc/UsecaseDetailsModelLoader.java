/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.uc;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;

/**
 * @author dmo5cob
 */
public class UsecaseDetailsModelLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData ServiceData
   */
  public UsecaseDetailsModelLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return UsecaseTreeGroupModel
   * @throws DataException Exception while loading Data
   */
  public UsecaseDetailsModel getUsecaseDetailsModel() throws DataException {
    UsecaseDetailsModel detailsModel = new UsecaseDetailsModel();
    UseCaseGroupLoader groupLoader = new UseCaseGroupLoader(getServiceData());
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());

    detailsModel.getUseCaseGroupMap().putAll(groupLoader.getAll());
    detailsModel.getUsecaseMap().putAll(ucLoader.getAll());
    groupLoader.fetchChildrenAndRootGroupsMap(detailsModel);

    UsecaseEditorDataLoader ucEditorDataLoader = new UsecaseEditorDataLoader(getServiceData());

    for (UseCase iterable_element : detailsModel.getUsecaseMap().values()) {
      UsecaseModel usecaseDetailsData = ucEditorDataLoader.getUsecaseDetailsData(iterable_element.getId());
      detailsModel.getUcSectionMap().putAll(usecaseDetailsData.getUcSectionMap());
      detailsModel.getUsecaseDetailsModelMap().put(iterable_element.getId(), usecaseDetailsData);
    }

    return detailsModel;
  }
}
