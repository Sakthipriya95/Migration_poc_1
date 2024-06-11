/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;

/**
 * @author mkl2cob
 */
public class UseCaseDetailsDataHandler extends AbstractClientDataHandler {

  private UsecaseClientBO useCase;

  private UsecaseEditorModel ucEditorModel;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(chData -> {
      Long useCaseId = ((UseCase) CnsUtils.getModel(chData)).getId();
      return CommonUtils.isEqual(getUseCase().getId(), useCaseId);
    }, this::refreshUseCase, MODEL_TYPE.USE_CASE);
    registerCns(chData -> {
      Long useCaseId = ((UseCaseSection) CnsUtils.getModel(chData)).getUseCaseId();
      return CommonUtils.isEqual(getUseCase().getId(), useCaseId);
    }, this::refreshEditorModel, MODEL_TYPE.USE_CASE_SECT);
    // refresh needed when the selection is usecase section
    registerCns(chData -> {
      Long useCaseId = ((UcpAttr) CnsUtils.getModel(chData)).getUseCaseId();
      return CommonUtils.isEqual(getUseCase().getId(), useCaseId);
    }, this::refreshEditorModel, MODEL_TYPE.UCP_ATTR);
    registerCnsCheckerForNodeAccess(MODEL_TYPE.USE_CASE, this::getUseCase);
    registerCns(this::refreshEditorModel, MODEL_TYPE.ATTRIBUTE, MODEL_TYPE.ATTRIB_VALUE);
  }

  /**
   * load use case again
   */
  private void refreshUseCase(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    try {
      UseCase newUsecase = ucServiceClient.getById(getUseCase().getId());
      this.useCase.setUseCase(newUsecase);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * load use case again
   */
  private void refreshEditorModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    try {
      UsecaseEditorModel editorModel = ucServiceClient.getUseCaseEditorData(getUseCase().getId());
      this.useCase.setUsecaseEditorModel(editorModel);
      this.useCase.clearChildMap();

      UseCase newUsecase = ucServiceClient.getById(getUseCase().getId());
      this.useCase.setUseCase(newUsecase);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the ucEditorModel
   */
  public UsecaseEditorModel getUcEditorModel() {
    return this.ucEditorModel;
  }

  /**
   * @param ucEditorModel the ucEditorModel to set
   */
  public void setUcEditorModel(final UsecaseEditorModel ucEditorModel) {
    this.ucEditorModel = ucEditorModel;
  }

  /**
   * @return the useCase
   */
  public UsecaseClientBO getUseCase() {
    return this.useCase;
  }

  /**
   * @param useCase the useCase to set
   */
  public void setUseCase(final UsecaseClientBO useCase) {
    this.useCase = useCase;
  }

}
