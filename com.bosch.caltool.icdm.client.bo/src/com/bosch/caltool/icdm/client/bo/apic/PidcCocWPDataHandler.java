/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author PDH2COB
 */
public class PidcCocWPDataHandler extends AbstractClientDataHandler {


  private final PidcCocWpBO pidcCocWpBO;

  /**
   * @param pidcCocWpBO
   */
  public PidcCocWPDataHandler(final PidcCocWpBO pidcCocWpBO) {
    super();
    this.pidcCocWpBO = pidcCocWpBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsPidcVersCocWp();
    registerCnsPidcVarCocWp();
    registerCnsPidcSubVarCocWp();
    registerCnsForPidcVersAttr();
    registerCnsForWorkPackageDivision();
  }

  private void registerCnsForPidcVersAttr() {
    registerCnsChecker(MODEL_TYPE.PROJ_ATTR,
        chData -> CommonUtils.isEqual(((PidcVersionAttribute) CnsUtils.getModel(chData)).getPidcVersId(),
            this.pidcCocWpBO.getPidcVersionBO().getPidcVersion().getId()) &&
            isQnaireConfigAttr((PidcVersionAttribute) CnsUtils.getModel(chData)));
    registerCnsAction(this::refreshWholeCocModel, MODEL_TYPE.PROJ_ATTR);
  }

  private void refreshWholeCocModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcCocWpBO.refreshPidcVersCocWpData();
  }

  private boolean isQnaireConfigAttr(final PidcVersionAttribute pidcVersionAttribute) {

    Long qnaireConfigAttrId = null;
    try {
      qnaireConfigAttrId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return CommonUtils.isEqual(pidcVersionAttribute.getAttrId(), qnaireConfigAttrId);

  }

  /**
   * Register cns for Pidc Version CocWP
   */
  private void registerCnsPidcVersCocWp() {

    registerCnsChecker(MODEL_TYPE.PIDC_VERS_COC_WP,
        chData -> CommonUtils.isEqual(((PidcVersCocWp) CnsUtils.getModel(chData)).getPidcVersId(),
            this.pidcCocWpBO.getPidcVersionBO().getPidcVersion().getId()));

    registerCnsActionLocal(
        chData -> refreshPidcVersCocWMap((PidcVersCocWp) chData.getNewData(), chData.getChangeType()),
        MODEL_TYPE.PIDC_VERS_COC_WP);

    registerCnsAction(this::refreshPidcVersCocWMapRemote, MODEL_TYPE.PIDC_VERS_COC_WP);
  }

  private void registerCnsForWorkPackageDivision() {

    registerCnsChecker(MODEL_TYPE.WORKPACKAGE_DIVISION, chData -> (this.pidcCocWpBO.getPidcVersCocWpData()
        .getWrkPkgDivMap().containsKey(CnsUtils.getModel(chData).getId())));

    registerCnsActionLocal(
        chData -> refreshWpDivMap(getDataForWPDivisionRefresh(chData.getChangeType(), chData), chData.getChangeType()),
        MODEL_TYPE.WORKPACKAGE_DIVISION);
  }

  private void refreshWpDivMap(final WorkPackageDivision workPackageDivision, final CHANGE_OPERATION changeType) {

    Map<Long, PidcVersCocWp> pidcVersCocWpMap = this.pidcCocWpBO.getPidcVersCocWpData().getPidcVersCocWpMap();
    Map<Long, WorkPackageDivision> wrkPkgDivMap = this.pidcCocWpBO.getPidcVersCocWpData().getWrkPkgDivMap();
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMap =
        this.pidcCocWpBO.getPidcVersCocWpData().getPidcVarCocWpMap();
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpMap =
        this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap();

    if (changeType == CHANGE_OPERATION.UPDATE) {

      wrkPkgDivMap.put(workPackageDivision.getId(), workPackageDivision);
      refreshWPDivInPidcVersCocWpMap(workPackageDivision, pidcVersCocWpMap);
      refreshWPDivInPidcVarCocWpMap(workPackageDivision, pidcVarCocWpMap);
      refreshWPDivInPidcSubVarCocWpMap(workPackageDivision, pidcSubVarCocWpMap);

    }
  }

  /**
   * @param workPackageDivision
   * @param pidcSubVarCocWpMap
   */
  private void refreshWPDivInPidcSubVarCocWpMap(final WorkPackageDivision workPackageDivision,
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpMap) {
    for (Map<Long, PidcSubVarCocWp> pidcSubVarCocWpMapObject : pidcSubVarCocWpMap.values()) {
      for (Map.Entry<Long, PidcSubVarCocWp> entry : pidcSubVarCocWpMapObject.entrySet()) {
        if (workPackageDivision.getId().equals(entry.getKey())) {
          entry.getValue().setDeleted(true);
        }
      }
    }
  }

  /**
   * @param workPackageDivision
   * @param pidcVarCocWpMap
   */
  private void refreshWPDivInPidcVarCocWpMap(final WorkPackageDivision workPackageDivision,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMap) {
    for (Map<Long, PidcVariantCocWp> pidcVarCocWpMapObject : pidcVarCocWpMap.values()) {
      for (Map.Entry<Long, PidcVariantCocWp> entry : pidcVarCocWpMapObject.entrySet()) {
        if (workPackageDivision.getId().equals(entry.getKey())) {
          entry.getValue().setDeleted(true);
        }
      }
    }
  }

  /**
   * @param workPackageDivision
   * @param pidcVersCocWpMap
   */
  private void refreshWPDivInPidcVersCocWpMap(final WorkPackageDivision workPackageDivision,
      final Map<Long, PidcVersCocWp> pidcVersCocWpMap) {
    for (Map.Entry<Long, PidcVersCocWp> entry : pidcVersCocWpMap.entrySet()) {
      if (workPackageDivision.getId().equals(entry.getKey())) {
        entry.getValue().setDeleted(true);
      }
    }
  }

  /**
   * Register cns for Pidc Variant CocWP
   */
  private void registerCnsPidcVarCocWp() {

    registerCnsChecker(MODEL_TYPE.PIDC_VARIANT_COC_WP,
        chData -> CommonUtils.isEqual(
            getPidcVersionFromVarMap(((PidcVariantCocWp) CnsUtils.getModel(chData)).getPidcVariantId()),
            this.pidcCocWpBO.getPidcVersionBO().getPidcVersion().getId()));

    registerCnsActionLocal(
        chData -> refreshPidcVarCocWpMap(getDataForPidcVarCocWpRefresh(chData.getChangeType(), chData),
            chData.getChangeType()),
        MODEL_TYPE.PIDC_VARIANT_COC_WP);

    registerCnsAction(this::refreshPidcVarCocWpMapRemote, MODEL_TYPE.PIDC_VARIANT_COC_WP);

  }

  /**
   * Register cns for Pidc Sub Variant CocWP
   */
  private void registerCnsPidcSubVarCocWp() {
    registerCnsChecker(MODEL_TYPE.PIDC_SUB_VAR_COC_WP,
        chData -> CommonUtils.isEqual(
            getPidcVersionFromSubVarMap(((PidcSubVarCocWp) CnsUtils.getModel(chData)).getPidcSubVarId()),
            this.pidcCocWpBO.getPidcVersionBO().getPidcVersion().getId()));
    registerCnsActionLocal(
        chData -> refreshPidcSubVarCocWpMap(getDataForPidcSubVarCocWpRefresh(chData.getChangeType(), chData),
            chData.getChangeType()),
        MODEL_TYPE.PIDC_SUB_VAR_COC_WP);

    registerCnsAction(this::refreshPidcSubVarCocWpMapRemote, MODEL_TYPE.PIDC_SUB_VAR_COC_WP);
  }


  /**
   * Cns for PidcVersCocWp from remote chnage
   *
   * @param chDataInfoMap
   */
  private void refreshPidcVersCocWMapRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        PidcVersCocWp pidcVersCocWp = this.pidcCocWpBO.gerPidcVersCocWp(data.getObjId());
        refreshPidcVersCocWMap(pidcVersCocWp, data.getChangeType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Cns for PidcVersCocWp from local chnage
   *
   * @param pidcVersCocWp as object
   * @param changeType as changedata
   */
  private void refreshPidcVersCocWMap(final PidcVersCocWp pidcVersCocWp, final CHANGE_OPERATION changeType) {
    if ((changeType == CHANGE_OPERATION.CREATE) || (changeType == CHANGE_OPERATION.UPDATE)) {
      Map<Long, PidcVersCocWp> pidcVersCocWpMap = this.pidcCocWpBO.getPidcVersCocWpData().getPidcVersCocWpMap();
      pidcVersCocWpMap.put(pidcVersCocWp.getWPDivId(), pidcVersCocWp);
    }
  }


  /**
   * Cns for pidcVarCocWp from remote chanage
   *
   * @param chDataInfoMap
   */
  private void refreshPidcVarCocWpMapRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        long pidcVarCocWpId = data.getObjId();
        if (data.getChangeType() == CHANGE_OPERATION.DELETE) {
          refreshRemoteDeleteForPidcvarCocObj(pidcVarCocWpId);
        }
        else {
          PidcVariantCocWp pidcVarsCocWp = this.pidcCocWpBO.gerPidcVarCocWp(pidcVarCocWpId);
          // for remote the old and new PidcVariantCocWp are same
          refreshPidcVarCocWpMap(pidcVarsCocWp, data.getChangeType());
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Cns for pidcVarCocWp from local chanage
   *
   * @param pidcVarCocWp as object
   * @param changeType as changedata
   */
  private void refreshPidcVarCocWpMap(final PidcVariantCocWp pidcVarCocWp, final CHANGE_OPERATION changeType) {
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMap =
        this.pidcCocWpBO.getPidcVersCocWpData().getPidcVarCocWpMap();
    if (changeType == CHANGE_OPERATION.CREATE) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpSubMap = new HashMap<>();
      if (pidcVarCocWpMap.containsKey(pidcVarCocWp.getPidcVariantId())) {
        pidcVarCocWpSubMap = pidcVarCocWpMap.get(pidcVarCocWp.getPidcVariantId());
      }
      pidcVarCocWpSubMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
      pidcVarCocWpMap.put(pidcVarCocWp.getPidcVariantId(), pidcVarCocWpSubMap);

    }
    else if ((changeType == CHANGE_OPERATION.UPDATE) && pidcVarCocWpMap.containsKey(pidcVarCocWp.getPidcVariantId())) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpSubMap = pidcVarCocWpMap.get(pidcVarCocWp.getPidcVariantId());
      pidcVarCocWpSubMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
    }
    else if ((changeType == CHANGE_OPERATION.DELETE) && pidcVarCocWpMap.containsKey(pidcVarCocWp.getPidcVariantId())) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpSubMap = pidcVarCocWpMap.get(pidcVarCocWp.getPidcVariantId());
      pidcVarCocWpSubMap.remove(pidcVarCocWp.getWPDivId());
    }
  }

  /**
   * Cns for pidcSubVarCocWp from remote chanage
   *
   * @param chDataInfoMap
   */
  private void refreshPidcSubVarCocWpMapRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        long pidcSubVarCocWPId = data.getObjId();
        if (data.getChangeType() == CHANGE_OPERATION.DELETE) {
          refreshRemoteDeleteForPidcSubvarCocObj(pidcSubVarCocWPId);
        }
        else {
          PidcSubVarCocWp pidcSubVarsCocWp = this.pidcCocWpBO.gerPidcSubVarCocWp(pidcSubVarCocWPId);
          refreshPidcSubVarCocWpMap(pidcSubVarsCocWp, data.getChangeType());
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param pidcSubVarCocWPId
   */
  private void refreshRemoteDeleteForPidcSubvarCocObj(final long pidcSubVarCocWPId) {
    PidcSubVarCocWp toBeRemovedPidcSubVarCocObj = null;
    this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap().entrySet();
    for (Entry<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpEntry : this.pidcCocWpBO.getPidcVersCocWpData()
        .getPidcSubVarCocWpMap().entrySet()) {
      for (Entry<Long, PidcSubVarCocWp> pidcSubVarCocWpSubEntry : pidcSubVarCocWpEntry.getValue().entrySet()) {
        if (pidcSubVarCocWpSubEntry.getValue().getId().equals(pidcSubVarCocWPId)) {
          toBeRemovedPidcSubVarCocObj = pidcSubVarCocWpSubEntry.getValue();
          break;
        }
      }
    }
    if ((toBeRemovedPidcSubVarCocObj != null) && this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap()
        .containsKey(toBeRemovedPidcSubVarCocObj.getPidcSubVarId())) {
      Map<Long, PidcSubVarCocWp> pidcSubVarMap = this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap()
          .get(toBeRemovedPidcSubVarCocObj.getPidcSubVarId());
      pidcSubVarMap.remove(toBeRemovedPidcSubVarCocObj.getWPDivId(), toBeRemovedPidcSubVarCocObj);
    }
  }

  /**
   * @param pidcVarCocWPId pidcVarCocWPId
   */
  private void refreshRemoteDeleteForPidcvarCocObj(final long pidcVarCocWPId) {
    PidcVariantCocWp toBeRemovedPidcVarCocObj = null;
    this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap().entrySet();
    for (Entry<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpEntry : this.pidcCocWpBO.getPidcVersCocWpData()
        .getPidcVarCocWpMap().entrySet()) {
      for (Entry<Long, PidcVariantCocWp> pidcVarCocWpSubEntry : pidcVarCocWpEntry.getValue().entrySet()) {
        if (pidcVarCocWpSubEntry.getValue().getId().equals(pidcVarCocWPId)) {
          toBeRemovedPidcVarCocObj = pidcVarCocWpSubEntry.getValue();
          break;
        }
      }
    }
    if ((toBeRemovedPidcVarCocObj != null) && this.pidcCocWpBO.getPidcVersCocWpData().getPidcVarCocWpMap()
        .containsKey(toBeRemovedPidcVarCocObj.getPidcVariantId())) {
      Map<Long, PidcVariantCocWp> pidcVariantMap =
          this.pidcCocWpBO.getPidcVersCocWpData().getPidcVarCocWpMap().get(toBeRemovedPidcVarCocObj.getPidcVariantId());
      pidcVariantMap.remove(toBeRemovedPidcVarCocObj.getWPDivId(), toBeRemovedPidcVarCocObj);
    }
  }

  /**
   * Cns for pidcSubVarCocWp from local chanage
   *
   * @param pidcSubVarCocWp as object
   * @param changeType as changedata
   */
  private void refreshPidcSubVarCocWpMap(final PidcSubVarCocWp pidcSubVarCocWp, final CHANGE_OPERATION changeType) {
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcVarCocWpMap =
        this.pidcCocWpBO.getPidcVersCocWpData().getPidcSubVarCocWpMap();
    if (changeType == CHANGE_OPERATION.CREATE) {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWpSubMap = new HashMap<>();
      if (pidcVarCocWpMap.containsKey(pidcSubVarCocWp.getPidcSubVarId())) {
        pidcSubVarCocWpSubMap = pidcVarCocWpMap.get(pidcSubVarCocWp.getPidcSubVarId());
      }
      pidcSubVarCocWpSubMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
      pidcVarCocWpMap.put(pidcSubVarCocWp.getPidcSubVarId(), pidcSubVarCocWpSubMap);
    }
    else if ((changeType == CHANGE_OPERATION.UPDATE) &&
        pidcVarCocWpMap.containsKey(pidcSubVarCocWp.getPidcSubVarId())) {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWpSubMap = pidcVarCocWpMap.get(pidcSubVarCocWp.getPidcSubVarId());
      pidcSubVarCocWpSubMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
    }
    else if ((changeType == CHANGE_OPERATION.DELETE) &&
        pidcVarCocWpMap.containsKey(pidcSubVarCocWp.getPidcSubVarId())) {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWpSubMap = pidcVarCocWpMap.get(pidcSubVarCocWp.getPidcSubVarId());
      pidcSubVarCocWpSubMap.remove(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
    }
  }

  private Long getPidcVersionFromVarMap(final Long variantId) {
    PidcVariant pidcVariant = this.pidcCocWpBO.getPidcVersionBO().getVariantsMap().get(variantId);
    return pidcVariant != null ? pidcVariant.getPidcVersionId() : 0l;
  }

  private Long getPidcVersionFromSubVarMap(final Long subVariantId) {
    PidcSubVariant pidcSubVariant = this.pidcCocWpBO.getPidcVersionBO().getSubVariantsMap().get(subVariantId);
    return pidcSubVariant != null ? pidcSubVariant.getPidcVersionId() : 0l;
  }

  private PidcVariantCocWp getDataForPidcVarCocWpRefresh(final CHANGE_OPERATION changeType,
      final ChangeData<?> chData) {
    return changeType == CHANGE_OPERATION.DELETE ? (PidcVariantCocWp) chData.getOldData()
        : (PidcVariantCocWp) chData.getNewData();
  }

  private WorkPackageDivision getDataForWPDivisionRefresh(final CHANGE_OPERATION changeType,
      final ChangeData<?> chData) {
    return changeType == CHANGE_OPERATION.DELETE ? (WorkPackageDivision) chData.getOldData()
        : (WorkPackageDivision) chData.getNewData();
  }

  private PidcSubVarCocWp getDataForPidcSubVarCocWpRefresh(final CHANGE_OPERATION changeType,
      final ChangeData<?> chData) {
    return changeType == CHANGE_OPERATION.DELETE ? (PidcSubVarCocWp) chData.getOldData()
        : (PidcSubVarCocWp) chData.getNewData();
  }

}
