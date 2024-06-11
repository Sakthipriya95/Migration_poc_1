/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpParamMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author bru2cob
 */
public class A2LCompareHandler extends AbstractClientDataHandler {

  Map<Long, A2LEditorDataProvider> a2lEditorDpMap = new HashMap<Long, A2LEditorDataProvider>();

  /**
   * Key is a2l wp def vers id value is a2l info
   */
  private Map<Long, A2LWPInfoBO> a2lWpInfoMap = new HashMap<>();
  /**
   * Input map is a map of a2l wp def version and set of variant group chosen for the version Input map value is null if
   * no var grp is selected/available
   */
  private Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpDefVarGrpMap = new HashMap<>();

  /**
   * @param a2lEditorDpMap a2l wp def ver id and a2l editor data provider map
   * @param a2lWpInfoMap a2l wp def ver id and a2l wp info bo map
   * @param a2lWpDefVarGrpMap contains set of variant group selected for same/different workpackage definition version
   */
  public A2LCompareHandler(final Map<Long, A2LEditorDataProvider> a2lEditorDpMap,
      final Map<Long, A2LWPInfoBO> a2lWpInfoMap, final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpDefVarGrpMap) {
    this.a2lEditorDpMap = a2lEditorDpMap;
    this.a2lWpInfoMap = a2lWpInfoMap;
    this.a2lWpDefVarGrpMap = a2lWpDefVarGrpMap;
    initializeHandler();
  }

  private void initializeHandler() {
    for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
      // check if data is already loaded
      if (!this.a2lWpInfoMap.keySet().contains(entrySet.getKey())) {
        loadData(entrySet.getKey(), entrySet.getValue());
      }
    }
  }

  /**
   * Load selected wp def version data for each a2l file
   *
   * @param a2lwpDefVersid
   * @param a2lDataProvider
   */
  private void loadData(final Long a2lwpDefVersid, final A2LEditorDataProvider a2lDataProvider) {
    // load the param data
    if (CommonUtils.isNotEmpty(a2lDataProvider.getA2lFileInfoBO().getParamProps())) {
      a2lDataProvider.getA2lWpInfoBO().initialiseA2LWParamInfo(
          a2lDataProvider.getA2lFileInfoBO().getA2lParamMap(a2lDataProvider.getA2lFileInfoBO().getParamProps()));
    }
    // load wp def data
    A2lWpDefnVersion selWpDefVers = a2lDataProvider.getA2lWpInfoBO().getA2lWpDefnVersMap().get(a2lwpDefVersid);
    if (null != selWpDefVers) {
      a2lDataProvider.getA2lWpInfoBO().loadWpMappedToPidcVers();
      a2lDataProvider.getA2lWpInfoBO().initializeModelBasedOnWpDefVers(selWpDefVers.getId(), false, true);
      a2lDataProvider.getA2lWpInfoBO().formVirtualRecords();
      A2lVariantGroupServiceClient varGrpServiceClient = new A2lVariantGroupServiceClient();
      try {
        a2lDataProvider.getA2lWpInfoBO()
            .setDetailsStrucModel(varGrpServiceClient.getA2lDetailsStructureData(selWpDefVers.getId()));
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    this.a2lWpInfoMap.put(a2lwpDefVersid, a2lDataProvider.getA2lWpInfoBO());
  }


  /**
   * @return the a2lWpInfoMap
   */
  public Map<Long, A2LWPInfoBO> getA2lWpInfoMap() {
    return this.a2lWpInfoMap;
  }

  /**
   * @return the a2lWpDefVarGrpMap
   */
  public final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> getA2lWpDefVarGrpMap() {
    return this.a2lWpDefVarGrpMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBILITY, chData -> {
      Long pidcId = ((A2lResponsibility) CnsUtils.getModel(chData)).getProjectId();
      for (A2LWPInfoBO a2lWpInfoBo : this.a2lWpInfoMap.values()) {
        if (a2lWpInfoBo.getPidcId().equals(pidcId)) {
          return true;
        }
      }
      return false;
    });

    registerCnsChecker(MODEL_TYPE.A2L_WORK_PACKAGE, chData -> {
      Long pidcVersId = ((A2lWorkPackage) CnsUtils.getModel(chData)).getPidcVersId();
      for (A2LWPInfoBO a2lWpInfoBo : this.a2lWpInfoMap.values()) {
        if (a2lWpInfoBo.getPidcA2lBo().getPidcVersion().getId().equals(pidcVersId)) {
          return true;
        }
      }
      return false;
    });

    registerCnsChecker(MODEL_TYPE.PIDC_A2L, chData -> {
      Long pidcA2lId = chData.getObjId();
      for (A2LWPInfoBO a2lWpInfoBo : this.a2lWpInfoMap.values()) {
        if (a2lWpInfoBo.getPidcA2lBo().getPidcA2lId().equals(pidcA2lId)) {
          return true;
        }
      }
      return false;
    });

    registerCnsChecker(MODEL_TYPE.A2L_WP_RESPONSIBILITY, chData -> {
      Long wpDefnVersId = ((A2lWpResponsibility) CnsUtils.getModel(chData)).getWpDefnVersId();
      return this.a2lWpInfoMap.containsKey(wpDefnVersId) &&
          this.a2lWpInfoMap.get(wpDefnVersId).isWorkingSet(wpDefnVersId);
    });


    registerCnsChecker(MODEL_TYPE.A2L_VARIANT_GROUP, chdata -> {
      Long wpDefnVersId = ((A2lVariantGroup) CnsUtils.getModel(chdata)).getWpDefnVersId();
      return this.a2lWpInfoMap.containsKey(wpDefnVersId) &&
          this.a2lWpInfoMap.get(wpDefnVersId).isWorkingSet(wpDefnVersId);
    });

    registerCnsChecker(MODEL_TYPE.A2L_WP_PARAM_MAPPING, chData -> {
      Long wpDefnVersId = ((A2lWpParamMapping) CnsUtils.getModel(chData)).getWpDefnVersionId();
      A2LWPInfoBO a2lWpInfoBo = this.a2lWpInfoMap.get(wpDefnVersId);
      return (a2lWpInfoBo != null) && (a2lWpInfoBo.getA2lWpParamMappingModel() != null) &&
          a2lWpInfoBo.isWorkingSet(wpDefnVersId);
    });
    registerCnsChecker(MODEL_TYPE.A2L_WP_DEFN_VERSION, chData -> {
      Long wpDefnVersId = ((A2lWpDefnVersion) CnsUtils.getModel(chData)).getId();
      return this.a2lWpInfoMap.containsKey(wpDefnVersId);
    });
    registerCnsActionLocal(this::refreshWpResp, MODEL_TYPE.A2L_WP_RESPONSIBILITY);
    registerCnsAction(this::refreshWpRespForRemoteChanges, MODEL_TYPE.A2L_WP_RESPONSIBILITY);

    registerCnsActionLocal(this::refreshVarGrp, MODEL_TYPE.A2L_VARIANT_GROUP);
    registerCnsAction(this::refreshVarGrpForRemoteChanges, MODEL_TYPE.A2L_VARIANT_GROUP);

    registerCnsActionLocal(this::refreshA2lResp, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCnsAction(this::refreshA2lRespForRemoteChanges, MODEL_TYPE.A2L_RESPONSIBILITY);

    registerCnsActionLocal(this::refreshA2lWpParamMap, MODEL_TYPE.A2L_WP_PARAM_MAPPING);
    registerCnsAction(this::refreshA2lWpParamMapForRemoteChng, MODEL_TYPE.A2L_WP_PARAM_MAPPING);

    registerCnsActionLocal(this::refreshWorkPackage, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCnsAction(this::refreshWpForRemoteChngs, MODEL_TYPE.A2L_WORK_PACKAGE);

    registerCnsActionLocal(this::refreshWpDefnVersion, MODEL_TYPE.A2L_WP_DEFN_VERSION);
    registerCnsAction(this::refreshWPDefnVersionForRemoteChanges, MODEL_TYPE.A2L_WP_DEFN_VERSION);

    registerCnsActionLocal(this::refreshPidcA2l, MODEL_TYPE.PIDC_A2L);
  }

  /**
   * Refresh wp defn version.
   *
   * @param chData the ch data
   */
  private void refreshWpDefnVersion(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2lWpDefnVersion wpDefn = (A2lWpDefnVersion) chData.getNewData();
      Set<A2lVariantGroup> varGrpSet = this.a2lWpDefVarGrpMap.get(wpDefn);
      this.a2lWpDefVarGrpMap.put(wpDefn, varGrpSet);
      Long wpDefVersId = wpDefn.getId();
      A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefVersId).getA2lWpInfoBO();
      a2lWpInfoBo.getA2lWpDefnVersMap().put(wpDefn.getId(), wpDefn);
      // update the ref map
      this.a2lWpInfoMap.put(wpDefVersId, a2lWpInfoBo);
    }
  }

  /**
   * Method to handle A2lWpDefinitionVersion remote changes
   *
   * @param chDataInfoMap - change data map from cns server
   */
  private void refreshWPDefnVersionForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {

        try {
          A2lWpDefnVersion wpDefnVersion = new A2lWpDefinitionVersionServiceClient().get(Long.valueOf(data.getObjId()));
          Set<A2lVariantGroup> varGrpSet = this.a2lWpDefVarGrpMap.get(wpDefnVersion);
          this.a2lWpDefVarGrpMap.put(wpDefnVersion, varGrpSet);
          A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefnVersion.getId()).getA2lWpInfoBO();
          a2lWpInfoBo.getA2lWpDefnModel().setParamMappingAllowed(wpDefnVersion.isParamLevelChgAllowedFlag());
          a2lWpInfoBo.getA2lWpDefnVersMap().remove(wpDefnVersion.getId());
          a2lWpInfoBo.getA2lWpDefnVersMap().put(wpDefnVersion.getId(), wpDefnVersion);
          // update the ref map
          this.a2lWpInfoMap.put(wpDefnVersion.getId(), a2lWpInfoBo);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }

    }

  }

  /**
   * Refresh A 2 l wp param map for remote chng.
   *
   * @param chDataInfoMap the ch data info map
   */
  private void refreshA2lWpParamMapForRemoteChng(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    Set<Long> refreshedA2lWpDefIdSet = new HashSet<Long>();
    // Do complete refresh for remote change
    for (ChangeDataInfo chData : chDataInfoMap.values()) {
      A2lWpParamMapping a2lWpParamMapping = (A2lWpParamMapping) chData.getRemovedData();
      if (a2lWpParamMapping == null) {
        try {
          a2lWpParamMapping = new A2lWpParamMappingServiceClient().get(Long.valueOf(chData.getObjId()));
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      if (a2lWpParamMapping != null) {
        Long wpDefnVersionId = a2lWpParamMapping.getWpDefnVersionId();
        if (!refreshedA2lWpDefIdSet.contains(wpDefnVersionId)) {
          A2LWPInfoBO a2lWpInfoBo = this.a2lWpInfoMap.get(wpDefnVersionId);
          a2lWpInfoBo.initializeModelBasedOnWpDefVers(wpDefnVersionId, false, true);
          refreshedA2lWpDefIdSet.add(wpDefnVersionId);
        }
      }
    }
  }

  private void refreshWpRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    ChangeDataInfo changeData = chDataInfoMap.values().iterator().next();
    A2lWpResponsibility a2lWpResp = (A2lWpResponsibility) changeData.getRemovedData();

    if (a2lWpResp == null) {
      try {
        a2lWpResp = new A2lWpResponsibilityServiceClient().get(Long.valueOf(changeData.getObjId()));
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }

    if (a2lWpResp != null) {
      Long wpDefVersId = a2lWpResp.getWpDefnVersId();
      A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefVersId).getA2lWpInfoBO();
      A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBo);
      dataHandler.refreshWpRespForRemoteChanges(chDataInfoMap);
      a2lWpInfoBo.loadWpMappedToPidcVers();
      a2lWpInfoBo.formVirtualRecords();
      // update the ref map
      this.a2lWpInfoMap.put(wpDefVersId, a2lWpInfoBo);
    }

  }

  /**
   * Method to refresh a2lWpParamMapping related changes.
   *
   * @param chData the ch data
   */
  private void refreshA2lWpParamMap(final ChangeData<?> chData) {
    Long wpDefVersId = null;
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lWpParamMapping a2lWpParamMapping = (A2lWpParamMapping) chData.getNewData();
      wpDefVersId = a2lWpParamMapping.getWpDefnVersionId();

    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      A2lWpParamMapping deletedA2lWpParamMapping = (A2lWpParamMapping) chData.getOldData();
      wpDefVersId = deletedA2lWpParamMapping.getWpDefnVersionId();
    }
    A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefVersId).getA2lWpInfoBO();
    A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBo);
    dataHandler.refreshA2lWpParamMap(chData);
    a2lWpInfoBo.loadWpMappedToPidcVers();
    a2lWpInfoBo.formVirtualRecords();
    // update the ref map
    this.a2lWpInfoMap.put(wpDefVersId, a2lWpInfoBo);
  }

  private void refreshVarGrpForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      A2lVariantGroup varGrp = (A2lVariantGroup) changeData.getRemovedData();
      if (varGrp == null) {
        try {
          varGrp = new A2lVariantGroupServiceClient().get(Long.valueOf(changeData.getObjId()));

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      if (varGrp != null) {
        updateVarGrp(varGrp.getWpDefnVersId());
      }
    }
  }

  private void refreshVarGrp(final ChangeData<?> chData) {
    Long wpDefVersId = null;
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lVariantGroup varGrp = (A2lVariantGroup) chData.getNewData();
      wpDefVersId = varGrp.getWpDefnVersId();
    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      A2lVariantGroup delVarGrp = (A2lVariantGroup) chData.getOldData();
      wpDefVersId = delVarGrp.getWpDefnVersId();
    }
    updateVarGrp(wpDefVersId);
  }

  /**
   * @param wpDefVersId
   */
  private void updateVarGrp(final Long wpDefVersId) {
    A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefVersId).getA2lWpInfoBO();
    A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBo);
    dataHandler.getA2lDetailsModel(wpDefVersId);
    Map<Long, A2lVariantGroup> a2lVariantGrpMap = a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap();


    Map<Long, A2lVariantGroup> defModelA2lVarGrpMap = a2lWpInfoBo.getA2lWpDefnModel().getA2lVariantGroupMap();

    for (Entry<Long, A2lVariantGroup> a2lVarGrpEntry : a2lVariantGrpMap.entrySet()) {
      if (defModelA2lVarGrpMap.get(a2lVarGrpEntry.getKey()) == null) {
        defModelA2lVarGrpMap.put(a2lVarGrpEntry.getKey(), a2lVarGrpEntry.getValue());
      }

    }
    List<Long> varGrpsDeleted = new ArrayList<Long>();
    for (Entry<Long, A2lVariantGroup> defmodEntrySet : defModelA2lVarGrpMap.entrySet()) {
      if (a2lVariantGrpMap.get(defmodEntrySet.getKey()) == null) {
        varGrpsDeleted.add(defmodEntrySet.getKey());
      }
    }
    for (Long delVarGrpIds : varGrpsDeleted) {
      defModelA2lVarGrpMap.remove(delVarGrpIds);
    }
    a2lWpInfoBo.loadWpMappedToPidcVers();
    a2lWpInfoBo.formVirtualRecords();
    // update the ref map
    this.a2lWpInfoMap.put(wpDefVersId, a2lWpInfoBo);
    A2lWpDefnVersion a2lWpDefVer = getA2lWpInfoMap().get(wpDefVersId).getA2lWpDefnVersMap().get(wpDefVersId);
    Set<A2lVariantGroup> a2lVariantGroupSet = this.a2lWpDefVarGrpMap.get(a2lWpDefVer);
    Set<A2lVariantGroup> selA2lVarGrpSet = new HashSet<>();
    for (A2lVariantGroup a2lVarGrp : a2lVariantGroupSet) {
      if (a2lVarGrp != null) {
        selA2lVarGrpSet.add(a2lVariantGrpMap.get(a2lVarGrp.getId()));
      }
    }
    this.a2lWpDefVarGrpMap.put(a2lWpDefVer, selA2lVarGrpSet);
  }

  private void refreshWpResp(final ChangeData<?> chData) {
    Long wpDefVersId = null;
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lWpResponsibility wpResp = (A2lWpResponsibility) chData.getNewData();
      wpDefVersId = wpResp.getWpDefnVersId();

    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      A2lWpResponsibility wpResp = (A2lWpResponsibility) chData.getOldData();
      wpDefVersId = wpResp.getWpDefnVersId();
    }
    A2LWPInfoBO a2lWpInfoBo = this.a2lEditorDpMap.get(wpDefVersId).getA2lWpInfoBO();
    A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBo);
    dataHandler.refreshWpResp(chData);
    // update the ref map
    this.a2lWpInfoMap.put(wpDefVersId, a2lWpInfoBo);
  }

  /**
   * Method to refresh A2lWpResponsibility related changes.
   *
   * @param chData the ch data
   */
  private void refreshA2lResp(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lResponsibility a2lResp = (A2lResponsibility) chData.getNewData();
      for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
        A2LWPInfoBO a2lWpInfoBO = entrySet.getValue().getA2lWpInfoBO();
        // if pidc id are equal add the new resp
        if (a2lWpInfoBO.getPidcA2lBo().getPidcVersion().getPidcId().equals(a2lResp.getProjectId()) &&
            a2lWpInfoBO.isWorkingSet(entrySet.getKey())) {
          a2lWpInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap().put(a2lResp.getId(), a2lResp);
          if ((null != a2lResp.getUserId()) &&
              !a2lWpInfoBO.getA2lResponsibilityModel().getUserMap().containsKey(a2lResp.getUserId())) {
            try {
              a2lWpInfoBO.getA2lResponsibilityModel().getUserMap().put(a2lResp.getUserId(),
                  new UserServiceClient().getApicUserById(a2lResp.getUserId()));
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
          }
          a2lWpInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap().size();
          // update the ref map
          this.a2lWpInfoMap.put(entrySet.getKey(), a2lWpInfoBO);
        }
      }
    }
  }

  private void refreshA2lRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    ChangeDataInfo changeData = chDataInfoMap.values().iterator().next();
    A2lResponsibility oldData = (A2lResponsibility) changeData.getRemovedData();
    if (oldData == null) {
      try {
        oldData = new A2lResponsibilityServiceClient().get(Long.valueOf(changeData.getObjId()));

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    if (oldData != null) {
      for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
        A2LWPInfoBO a2lWpInfoBO = entrySet.getValue().getA2lWpInfoBO();
        if (a2lWpInfoBO.getPidcA2lBo().getPidcVersion().getPidcId().equals(oldData.getProjectId()) &&
            a2lWpInfoBO.isWorkingSet(entrySet.getKey())) {
          A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBO);
          dataHandler.refreshA2lRespForRemoteChanges(chDataInfoMap);
          // update the ref map
          this.a2lWpInfoMap.put(entrySet.getKey(), a2lWpInfoBO);
        }
      }
    }
  }

  /**
   * Method to refresh a2lWorkPackage related changes.
   *
   * @param chData the chdata
   */
  public void refreshWorkPackage(final ChangeData<?> chData) {
    A2lWorkPackage newData = (A2lWorkPackage) chData.getNewData();

    for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
      A2LWPInfoBO a2lWpInfoBO = entrySet.getValue().getA2lWpInfoBO();
      if (a2lWpInfoBO.getPidcA2lBo().getPidcVersion().getId().equals(newData.getPidcVersId()) &&
          a2lWpInfoBO.isWorkingSet(entrySet.getKey())) {
        A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBO);
        dataHandler.refreshWorkPackage(chData);
        // update the ref map
        this.a2lWpInfoMap.put(entrySet.getKey(), a2lWpInfoBO);
      }
    }
  }

  /**
   * Method to refresh a2lWorkPackage related changes remotely.
   */
  private void refreshWpForRemoteChngs(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    ChangeDataInfo changeData = chDataInfoMap.values().iterator().next();
    A2lWorkPackage oldData = (A2lWorkPackage) changeData.getRemovedData();
    if (oldData != null) {
      for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
        A2LWPInfoBO a2lWpInfoBO = entrySet.getValue().getA2lWpInfoBO();
        if (a2lWpInfoBO.getPidcA2lBo().getPidcVersion().getId().equals(oldData.getPidcVersId()) &&
            a2lWpInfoBO.isWorkingSet(entrySet.getKey())) {
          A2LEditorDataHandler dataHandler = new A2LEditorDataHandler(a2lWpInfoBO);
          dataHandler.refreshWpForRemoteChngs(chDataInfoMap);
          // update the ref map
          this.a2lWpInfoMap.put(entrySet.getKey(), a2lWpInfoBO);
        }
      }
    }
  }

  /**
   * Method to refresh a2lwpresppal related changes.
   *
   * @param chData the ch data
   */
  private void refreshPidcA2l(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      PidcA2l newData = (PidcA2l) chData.getNewData();
      for (Entry<Long, A2LEditorDataProvider> entrySet : this.a2lEditorDpMap.entrySet()) {
        A2LWPInfoBO a2lWpInfoBO = entrySet.getValue().getA2lWpInfoBO();
        if (a2lWpInfoBO.getPidcA2lBo().getPidcA2lId().equals(newData.getId()) &&
            a2lWpInfoBO.isWorkingSet(entrySet.getKey())) {
          CommonUtils.shallowCopy(a2lWpInfoBO.getPidcA2lBo().getPidcA2l(), newData);
          // update the ref map
          this.a2lWpInfoMap.put(entrySet.getKey(), a2lWpInfoBO);
        }
      }

    }

  }

}
