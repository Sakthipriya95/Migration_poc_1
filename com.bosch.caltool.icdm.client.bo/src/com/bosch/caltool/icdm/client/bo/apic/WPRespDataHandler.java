/*
 * l * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author apj4cob
 */
public class WPRespDataHandler extends AbstractClientDataHandler {

  private final WorkPkgResponsibilityBO wpRespBO;
  private final Pidc pidc;

  /**
   * @param wpRespBO WorkPkgResponsibilityBO
   * @param pidc Pidc
   */
  public WPRespDataHandler(final WorkPkgResponsibilityBO wpRespBO, final Pidc pidc) {
    this.wpRespBO = wpRespBO;
    this.pidc = pidc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.PIDC_A2L, chData -> {
      Long pidcId = ((PidcA2l) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getWpRespBO().getPidcId(), pidcId) && isA2lRespModelRefreshNeeded(chData);
    });
    registerCnsAction(this::refreshA2lRespForPidcA2lChanges, MODEL_TYPE.PIDC_A2L);

    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBILITY, chData -> {
      Long pidcId = ((A2lResponsibility) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getWpRespBO().getPidcId(), pidcId);
    });
    registerCnsActionLocal(this::refreshA2lResp, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCnsAction(this::refreshA2lRespForRemoteChanges, MODEL_TYPE.A2L_RESPONSIBILITY);

    registerCnsChecker(MODEL_TYPE.A2L_WP_DEFN_VERSION, chData -> {
      Long pidcA2lId = ((A2lWpDefnVersion) CnsUtils.getModel(chData)).getPidcA2lId();
      return this.wpRespBO.isPidcA2lInVer(pidcA2lId);
    });
    registerCnsAction(this::refreshA2lWpDefnVersn, MODEL_TYPE.A2L_WP_DEFN_VERSION);

    registerCnsChecker(MODEL_TYPE.A2L_WORK_PACKAGE, chData -> {
      Long pidcVersId = ((A2lWorkPackage) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getWpRespBO().getPidcVersObj().getId(), pidcVersId);
    });
    // Cns checker for changes in NodeAccess Object
    registerCnsCheckerForNodeAccess(MODEL_TYPE.PIDC, this::getPidc);
    registerCnsActionLocal(this::refreshWorkPkgMap, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCnsAction(this::refreshWpForRemoteChngs, MODEL_TYPE.A2L_WORK_PACKAGE);

    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBLITY_BSHGRP_USR, chData -> {
      Long projectId = ((A2lRespBoschGroupUser) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getWpRespBO().getPidcId(), projectId);
    });

    registerCnsActionLocal(this::refreshA2lBoschGrpUser, MODEL_TYPE.A2L_RESPONSIBLITY_BSHGRP_USR);

  }

  private void refreshA2lBoschGrpUser(final ChangeData<?> chData) {
    if (CHANGE_OPERATION.CREATE.equals(chData.getChangeType())) {
      A2lRespBoschGroupUser a2lRespBoschGroupUser = (A2lRespBoschGroupUser) chData.getNewData();

      Map<Long, A2lRespBoschGroupUser> a2lBshUsrMap =
          getWpRespBO().getA2lRespModel().getA2lBoschGrpUserMap().get(a2lRespBoschGroupUser.getA2lRespId());

      if (a2lBshUsrMap == null) {
        a2lBshUsrMap = new HashMap<>();
      }

      if (!a2lBshUsrMap.containsKey(a2lRespBoschGroupUser.getId())) {
        a2lBshUsrMap.put(a2lRespBoschGroupUser.getId(), a2lRespBoschGroupUser);
      }

      getWpRespBO().getA2lRespModel().getA2lBoschGrpUserMap().put(a2lRespBoschGroupUser.getA2lRespId(), a2lBshUsrMap);


    }
    if (CHANGE_OPERATION.DELETE.equals(chData.getChangeType())) {
      A2lRespBoschGroupUser a2lRespBoschGroupUser = (A2lRespBoschGroupUser) chData.getOldData();

      if(getWpRespBO().getA2lRespModel().getA2lBoschGrpUserMap().containsKey(a2lRespBoschGroupUser.getA2lRespId())){
      getWpRespBO().getA2lRespModel().getA2lBoschGrpUserMap().get(a2lRespBoschGroupUser.getA2lRespId())
          .remove(a2lRespBoschGroupUser.getId());
      }
    }
  }

  /**
   * Resfreshes A2L responsibiity page when a new pidc a2l is assigned
   *
   * @param chDataInfoMap
   */
  private void refreshA2lRespForPidcA2lChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      getWpRespBO().loadA2lRespForPidc(true);
    }
  }

  /**
   * is A2l Responsibility model refresh needed(when a pidc a2l is assigned for first time/ after configuring qnaire
   * config attr to BEG)
   *
   * @param chData
   * @return
   */
  private boolean isA2lRespModelRefreshNeeded(final ChangeData<?> chData) {
    return ((CHANGE_OPERATION.CREATE == chData.getChangeType()) ||
        (CHANGE_OPERATION.UPDATE == chData.getChangeType())) && getWpRespBO().getPidcVersBO().isQnaireConfigBEG() &&
        !isBegRespLoaded();
  }

  /**
   * is BEG responsibility already loaded in Pidc
   *
   * @return
   */
  private boolean isBegRespLoaded() {
    return (null != getWpRespBO().getA2lRespModel()) && getWpRespBO().getA2lRespModel().getA2lResponsibilityMap()
        .values().stream().anyMatch(a2lResp -> ApicConstants.ALIAS_NAME_RB_BEG.equals(a2lResp.getAliasName()));
  }

  private void refreshA2lWpDefnVersn(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          if (CommonUtils.isNullOrEmpty(getWpRespBO().getWorkPackages())) {
            getWpRespBO().getWorkPackages()
                .putAll(new A2lWorkPackageServiceClient().getWpByPidcVers(getWpRespBO().getPidcVersObj().getId()));
          }
          if (CommonUtils.isNull(getWpRespBO().getA2lRespModel()) ||
              (CommonUtils.isNullOrEmpty(getWpRespBO().getA2lRespModel().getDefaultA2lRespMap()) &&
                  CommonUtils.isNullOrEmpty(getWpRespBO().getA2lRespModel().getA2lResponsibilityMap()))) {
            A2lResponsibilityModel a2lRespModel =
                new A2lResponsibilityServiceClient().getByPidc(this.wpRespBO.getPidcId());

            getWpRespBO().setA2lRespModel(a2lRespModel);
            getWpRespBO().getA2lRespModel().getA2lResponsibilityMap().putAll(a2lRespModel.getA2lResponsibilityMap());
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }
    }
  }

  /**
   * Method to refresh A2lWpResponsibility related changes.
   *
   * @param chData the ch data
   */
  private void refreshA2lResp(final ChangeData<?> chData) {
    if (CommonUtils.isNotNull(getWpRespBO().getA2lRespModel())) {
      if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        A2lResponsibility a2lResp = (A2lResponsibility) chData.getNewData();
        getWpRespBO().getA2lRespModel().getA2lResponsibilityMap().put(a2lResp.getId(), a2lResp);
        Long userId = this.wpRespBO.getA2lRespModel().getA2lResponsibilityMap().get(a2lResp.getId()).getUserId();
        if ((userId != null) && !getWpRespBO().getA2lRespModel().getUserMap().containsKey(userId)) {
          try {
            getWpRespBO().getA2lRespModel().getUserMap().put(userId, new UserServiceClient().getApicUserById(userId));
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      }
      else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        A2lResponsibility a2lResp = (A2lResponsibility) chData.getOldData();
        getWpRespBO().getA2lRespModel().getA2lResponsibilityMap().remove(a2lResp.getId());
      }
    }
  }

  private void refreshA2lRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          A2lResponsibility a2lResp = new A2lResponsibilityServiceClient().get(Long.valueOf(data.getObjId()));
          getWpRespBO().getA2lRespModel().getA2lResponsibilityMap().put(data.getObjId(), a2lResp);

          Long userId = this.wpRespBO.getA2lRespModel().getA2lResponsibilityMap().get(a2lResp.getId()).getUserId();
          if ((userId != null) && !getWpRespBO().getA2lRespModel().getUserMap().containsKey(userId)) {
            getWpRespBO().getA2lRespModel().getUserMap().put(userId, new UserServiceClient().getApicUserById(userId));
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        A2lResponsibility a2lResp = (A2lResponsibility) data.getRemovedData();
        getWpRespBO().getA2lRespModel().getA2lResponsibilityMap().remove(a2lResp.getId());
      }
    }
  }

  /**
   * @param chData the ch data
   */
  private void refreshWorkPkgMap(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lWorkPackage a2lWorkPkg = (A2lWorkPackage) chData.getNewData();
      getWpRespBO().getWorkPackgsMap().put(a2lWorkPkg.getId(), a2lWorkPkg);
    }
  }

  private void refreshWpForRemoteChngs(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          A2lWorkPackage a2lWp = new A2lWorkPackageServiceClient()
              .getWpByPidcVers(getWpRespBO().getPidcVersObj().getId()).get(data.getObjId());
          getWpRespBO().getWorkPackgsMap().put(data.getObjId(), a2lWp);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }

  }

  /**
   * @return the wpRespBO
   */
  public WorkPkgResponsibilityBO getWpRespBO() {
    return this.wpRespBO;
  }

  /**
   * @return Pidc
   */
  private Pidc getPidc() {
    return this.pidc;
  }
}
