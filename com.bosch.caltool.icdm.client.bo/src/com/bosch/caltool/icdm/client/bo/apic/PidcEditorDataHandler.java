/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PidcEditorDataHandler extends AbstractClientDataHandler {

  private final PidcVersionBO pidcVersionBO;


  /**
   * PIDC Editor's main data handler
   *
   * @param pidcVersBO Pidc Version BO
   */
  public PidcEditorDataHandler(final PidcVersionBO pidcVersBO) {
    this.pidcVersionBO = pidcVersBO;
  }

  /**
   * @return Pidc
   */
  private Pidc getPidc() {
    return this.pidcVersionBO.getPidc();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerCnsCheckerForNodeAccess(MODEL_TYPE.PIDC, this::getPidc);
    registerCnsChecker(MODEL_TYPE.PROJ_ATTR, chData -> {
      Long pidcVersID = ((PidcVersionAttribute) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.VAR_ATTR, chData -> {
      Long pidcVersID = ((PidcVariantAttribute) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.SUBVAR_ATTR, chData -> {
      Long pidcVersID = ((PidcSubVariantAttribute) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.PIDC_VERSION, chData -> {
      Long pidcVersID = ((PidcVersion) CnsUtils.getModel(chData)).getId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.VARIANT, chData -> {
      Long pidcVersID = ((PidcVariant) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.SUB_VARIANT, chData -> {
      Long pidcVersID = ((PidcSubVariant) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    });
    registerCnsChecker(MODEL_TYPE.PIDC_A2L, this::isPidcA2lChanged);

    registerCnsChecker(MODEL_TYPE.UC_FAV, chData -> {
      Long pidcID = ((UsecaseFavorite) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getPidcVersion().getPidcId(), pidcID);
    });

    registerCnsChecker(MODEL_TYPE.USE_CASE_GROUP, chData -> {
      Long ucGrpId = ((UseCaseGroup) CnsUtils.getModel(chData)).getId();
      return isRefreshReqForUCItem(ucGrpId);
    });

    registerCnsChecker(MODEL_TYPE.USE_CASE, chData -> {
      Long ucId = ((UseCase) CnsUtils.getModel(chData)).getId();
      return isRefreshReqForUCItem(ucId);
    });


    registerCnsChecker(MODEL_TYPE.USE_CASE_SECT, chData -> {
      Long ucsId = ((UseCaseSection) CnsUtils.getModel(chData)).getId();
      return isRefreshReqForUCItem(ucsId);
    });

    registerCnsChecker(MODEL_TYPE.UCP_ATTR);

    registerCnsChecker(MODEL_TYPE.PIDC, chData -> {
      Pidc pidc = ((Pidc) CnsUtils.getModel(chData));
      return CommonUtils.isEqual(getPidcVersion().getPidcId(), pidc.getId());
    });

    registerCnsActionLocal(this::refreshProjAttr, MODEL_TYPE.PROJ_ATTR);
    registerCnsActionLocal(this::refreshVarAttr, MODEL_TYPE.VAR_ATTR);
    registerCnsActionLocal(this::refreshSubVarAttr, MODEL_TYPE.SUBVAR_ATTR);
    registerCnsActionLocal(this::refreshPidcVersion, MODEL_TYPE.PIDC_VERSION);
    registerCnsActionLocal(this::refreshVariantMap, MODEL_TYPE.VARIANT);
    registerCnsActionLocal(this::refreshSubVariantMap, MODEL_TYPE.SUB_VARIANT);
    registerCnsActionLocal(this::refreshPidcLocal, MODEL_TYPE.PIDC);

    registerCnsAction(this::refreshPidcA2l, MODEL_TYPE.PIDC_A2L);
    registerCnsAction(this::refreshUsecaseAttr, MODEL_TYPE.UC_FAV, MODEL_TYPE.UCP_ATTR, MODEL_TYPE.USE_CASE,
        MODEL_TYPE.USE_CASE_GROUP, MODEL_TYPE.USE_CASE_SECT);

    registerCnsAction(this::refreshWholeModel, MODEL_TYPE.PROJ_ATTR, MODEL_TYPE.VAR_ATTR, MODEL_TYPE.SUBVAR_ATTR,
        MODEL_TYPE.PIDC_VERSION, MODEL_TYPE.VARIANT, MODEL_TYPE.SUB_VARIANT);
    registerCnsAction(this::refreshPidc, MODEL_TYPE.PIDC);

    registerCns(chData -> {
      Long pidcVersID = ((PidcDetStructure) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersID);
    }, this::refreshDetStructMap, MODEL_TYPE.PIDC_DET_STRUCT);

    // changes in attribute
    registerCns(this::refreshWholeModel, MODEL_TYPE.ATTRIBUTE);
    // TODO to be optimised
    registerCns(this::refreshPidc, MODEL_TYPE.ATTRIB_VALUE);


  }


  private boolean isRefreshReqForUCItem(final Long id) {
    Set<Long> ucIdSet = this.pidcVersionBO.getPidcDataHandler().getProjectUsecaseModel().getProjectUsecaseIdSet();
    Set<Long> parentUcIdSet =
        this.pidcVersionBO.getPidcDataHandler().getProjectUsecaseModel().getParentUsecaseItemIdSet();
    return ucIdSet.contains(id) || parentUcIdSet.contains(id);
  }


  /**
   * Method to refresh uc attr map
   *
   * @param chDataInfoMap
   */
  private void refreshUsecaseAttr(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    try {
      this.pidcVersionBO.getPidcDataHandler()
          .setProjectUsecaseModel(new PidcServiceClient().getProjectUsecaseModel(this.pidcVersionBO.getPidc().getId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  private boolean isPidcA2lChanged(final ChangeData<?> chData) {
    return this.pidcVersionBO.isPidcA2lChanged(chData);
  }

  private void refreshProjAttr(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshProjAttr(chData);
  }

  private void refreshVarAttr(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshVarAttr(chData);
  }

  private void refreshSubVarAttr(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshSubVarAttr(chData);
  }

  private void refreshPidcVersion(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshPidcVersion(chData);
  }

  private void refreshVariantMap(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshVariantMap(chData);
  }

  private void refreshSubVariantMap(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshSubVariantMap(chData);
  }

  private void refreshPidcA2l(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcVersionBO.refreshPidcA2l(chDataInfoMap);
  }

  private void refreshWholeModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcVersionBO.refreshWholeModel(chDataInfoMap);
  }

  private void refreshDetStructMap(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcVersionBO.refreshDetStructMap(chDataInfoMap);
  }

  private void refreshPidc(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcVersionBO.refreshPidc(chDataInfoMap);
  }

  private void refreshPidcLocal(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshPidcLocal(chData);
  }

  /**
   * @return Pidc Version
   */
  public PidcVersion getPidcVersion() {
    return getPidcVersionBO().getPidcVersion();
  }


  /**
   * @return the pidcVersBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }


}
