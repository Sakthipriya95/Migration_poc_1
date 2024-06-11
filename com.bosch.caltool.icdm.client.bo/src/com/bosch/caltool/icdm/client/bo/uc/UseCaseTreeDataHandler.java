/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;

/**
 * @author mkl2cob
 */
public class UseCaseTreeDataHandler extends AbstractClientDataHandler {

  private UsecaseTreeGroupModel ucTreeGrpModel;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(this::refreshModelMap, MODEL_TYPE.USE_CASE_GROUP);
    registerCns(this::refreshModelMap, MODEL_TYPE.USE_CASE);
  }


  private void refreshModelMap(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    loadTreeModel();
  }

  /**
   *
   */
  private void loadTreeModel() {
    UseCaseGroupServiceClient ucgServiceClient = new UseCaseGroupServiceClient();
    try {
      this.ucTreeGrpModel = ucgServiceClient.getUseCaseTreeDataModel();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the ucTreeGrpModel
   */
  public UsecaseTreeGroupModel getUcTreeGrpModel() {
    if (null == this.ucTreeGrpModel) {
      loadTreeModel();
    }
    return this.ucTreeGrpModel;
  }

  /**
   * @param ucTreeGrpModel the ucTreeGrpModel to set
   */
  public void setUcTreeGrpModel(final UsecaseTreeGroupModel ucTreeGrpModel) {
    this.ucTreeGrpModel = ucTreeGrpModel;
  }
}
