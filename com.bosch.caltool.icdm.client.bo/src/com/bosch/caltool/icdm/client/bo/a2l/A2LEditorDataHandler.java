/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * The Class A2LEditorDataHandler.
 *
 * @author gge6cob
 */
public class A2LEditorDataHandler extends AbstractClientDataHandler {

  /** This bo contains wp related details. */
  private final A2LWPInfoBO a2lWpInfoBo;

  private A2LDetailsStructureModel detailsStrucModel;


  private Long wpVersionId;

  /**
   * Instantiates a new a 2 L wp resp editor data handler.
   *
   * @param a2lWpInfoBo a2l wp info bo
   */
  public A2LEditorDataHandler(final A2LWPInfoBO a2lWpInfoBo) {
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBILITY, chData -> {
      Long pidcId = ((A2lResponsibility) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getA2lWpInfoBo().getPidcId(), pidcId);
    });

    registerCnsChecker(MODEL_TYPE.A2L_WORK_PACKAGE, chData -> {
      Long pidcVersId = ((A2lWorkPackage) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getA2lWpInfoBo().getPidcA2lBo().getPidcVersion().getId(), pidcVersId);
    });

    registerCnsChecker(MODEL_TYPE.A2L_WP_RESPONSIBILITY, chData -> {
      Long wpDefnVersId = ((A2lWpResponsibility) CnsUtils.getModel(chData)).getWpDefnVersId();
      return CommonUtils.isEqual(this.a2lWpInfoBo.getA2lWpDefnModel().getSelectedWpDefnVersionId(), wpDefnVersId);
    });

    registerCnsChecker(MODEL_TYPE.PIDC_A2L, chData -> {
      Long pidcA2lId = chData.getObjId();
      return CommonUtils.isEqual(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2lId(), pidcA2lId);
    });

    registerCnsChecker(MODEL_TYPE.A2L_VARIANT_GROUP, chdata -> {
      Long wpDefnVersId = ((A2lVariantGroup) CnsUtils.getModel(chdata)).getWpDefnVersId();
      return CommonUtils.isEqual(this.a2lWpInfoBo.getA2lWpDefnModel().getSelectedWpDefnVersionId(), wpDefnVersId) ||
          CommonUtils.isEqual(this.a2lWpInfoBo.getWorkingSet().getId(), wpDefnVersId);

    });

    registerCnsChecker(MODEL_TYPE.VARIANT, chdata -> {
      PidcVariant variant = ((PidcVariant) CnsUtils.getModel(chdata));
      return isVarAvailable(variant);

    });
    registerCnsChecker(MODEL_TYPE.A2L_VAR_GRP_VAR_MAPPING, chdata -> {
      A2lVarGrpVariantMapping a2lGrpVarMapping = ((A2lVarGrpVariantMapping) CnsUtils.getModel(chdata));
      return isMappingApplicable(a2lGrpVarMapping);

    });
    registerCnsChecker(MODEL_TYPE.A2L_WP_DEFN_VERSION, chData -> {
      Long pidcA2lId = ((A2lWpDefnVersion) CnsUtils.getModel(chData)).getPidcA2lId();
      return CommonUtils.isEqual(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2lId(), pidcA2lId);
    });
    registerCnsChecker(MODEL_TYPE.A2L_WP_PARAM_MAPPING, chData -> {
      Long wpDefnVersId = ((A2lWpParamMapping) CnsUtils.getModel(chData)).getWpDefnVersionId();
      if (this.a2lWpInfoBo.getA2lWpParamMappingModel() != null) {
        return CommonUtils.isEqual(this.a2lWpInfoBo.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(),
            wpDefnVersId);
      }
      return false;
    });

    // to do
    // During cns refresh of A2lWpResponsibility, following changes are needed
    // removal of A2lWpResponsibility object from ParamMappingModel
    // removal of A2lWpParamMapping objects from ParamMappingModel

    registerCnsActionLocal(this::refreshWpResp, MODEL_TYPE.A2L_WP_RESPONSIBILITY);
    registerCnsAction(this::refreshWpRespForRemoteChanges, MODEL_TYPE.A2L_WP_RESPONSIBILITY);

    registerCnsActionLocal(this::refreshA2lResp, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCnsAction(this::refreshA2lRespForRemoteChanges, MODEL_TYPE.A2L_RESPONSIBILITY);

    registerCnsActionLocal(this::refreshWpDefnVersion, MODEL_TYPE.A2L_WP_DEFN_VERSION);
    registerCnsAction(this::refreshWPDefnVersionForRemoteChanges, MODEL_TYPE.A2L_WP_DEFN_VERSION);

    registerCnsActionLocal(this::refreshA2lWpParamMap, MODEL_TYPE.A2L_WP_PARAM_MAPPING);
    registerCnsAction(this::refreshA2lWpParamMapForRemoteChng, MODEL_TYPE.A2L_WP_PARAM_MAPPING);

    registerCnsActionLocal(this::refreshWorkPackage, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCnsAction(this::refreshWpForRemoteChngs, MODEL_TYPE.A2L_WORK_PACKAGE);

    registerCnsActionLocal(this::refreshPidcA2l, MODEL_TYPE.PIDC_A2L);
    registerCnsAction(this::refreshPidcA2lRemote, MODEL_TYPE.PIDC_A2L);

    registerCnsChecker(MODEL_TYPE.NODE_ACCESS, chData -> {
      Long nodeId = ((NodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(this.a2lWpInfoBo.getPidcA2lBo().getPidcVersion().getPidcId());
    });

    registerCnsChecker(MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES, chData -> {
      Long nodeId = ((ActiveDirectoryGroupNodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(this.a2lWpInfoBo.getPidcA2lBo().getPidcVersion().getPidcId());
    });

    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBLITY_BSHGRP_USR, chData -> {
      Long projectId = ((A2lRespBoschGroupUser) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(this.a2lWpInfoBo.getPidcId(), projectId);
    });

    registerCnsActionLocal(this::refreshA2lBoschGrpUser, MODEL_TYPE.A2L_RESPONSIBLITY_BSHGRP_USR);
  }

  private void refreshA2lBoschGrpUser(final ChangeData<?> chData) {
    if (CHANGE_OPERATION.CREATE.equals(chData.getChangeType())) {
      A2lRespBoschGroupUser a2lRespBoschGroupUser = (A2lRespBoschGroupUser) chData.getNewData();

      Map<Long, A2lRespBoschGroupUser> a2lBshUsrMap = this.a2lWpInfoBo.getA2lResponsibilityModel()
          .getA2lBoschGrpUserMap().get(a2lRespBoschGroupUser.getA2lRespId());

      if (a2lBshUsrMap == null) {
        a2lBshUsrMap = new HashMap<>();
      }

      if (!a2lBshUsrMap.containsKey(a2lRespBoschGroupUser.getId())) {
        a2lBshUsrMap.put(a2lRespBoschGroupUser.getId(), a2lRespBoschGroupUser);
      }

      this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lBoschGrpUserMap().put(a2lRespBoschGroupUser.getA2lRespId(),
          a2lBshUsrMap);


    }
    if (CHANGE_OPERATION.DELETE.equals(chData.getChangeType())) {
      A2lRespBoschGroupUser a2lRespBoschGroupUser = (A2lRespBoschGroupUser) chData.getOldData();

      if (this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lBoschGrpUserMap()
          .containsKey(a2lRespBoschGroupUser.getA2lRespId())) {
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lBoschGrpUserMap().get(a2lRespBoschGroupUser.getA2lRespId())
            .remove(a2lRespBoschGroupUser.getId());
      }
    }
  }

  /**
   *
   */
  public void refreshPidcA2lRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo chData : chDataInfoMap.values()) {
      if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          PidcA2l newData = new PidcA2lServiceClient().getById(chData.getObjId());
          CommonUtils.shallowCopy(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2l(), newData);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
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
    A2lWorkPackage oldData = getA2lWpInfoBo().getAllWpMapppedToPidcVers().get(chData.getObjId());
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      refreshA2lWPRespMap(newData, oldData);
      updateWorkPkgsMap(newData);
    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      updateWorkPkgsMap(newData);
    }
  }

  /**
   * Updates the WorkPackage map with the new data object.
   *
   * @param newData
   */
  private void updateWorkPkgsMap(final A2lWorkPackage newData) {
    if (newData != null) {
      getA2lWpInfoBo().getAllWpMapppedToPidcVers().put(newData.getId(), newData);
    }
  }

  /**
   * Refresh the A2lWpResponsibility map when workpackage is modified in Pidc editor.
   *
   * @param a2lWPNewData A2lWorkPackage
   * @param a2lWPOldData A2lWorkPackage
   */
  private void refreshA2lWPRespMap(final A2lWorkPackage a2lWPNewData, final A2lWorkPackage a2lWPOldData) {

    A2lWpDefinitionModel a2lWpDefnModel = getA2lWpInfoBo().getA2lWpDefnModel();
    Set<A2lWpResponsibility> mappedA2lWpRespSet =
        a2lWpDefnModel.getA2lWpRespNodeMergedMap().get(a2lWPOldData.getName());
    if (null != mappedA2lWpRespSet) {
      mappedA2lWpRespSet.forEach(wpRespObj -> {
        wpRespObj.setName(a2lWPNewData.getName());
        wpRespObj.setWpNameCust(a2lWPNewData.getNameCustomer());
        wpRespObj.setDescription(a2lWPNewData.getDescription());
        a2lWpDefnModel.getWpRespMap().put(wpRespObj.getId(), wpRespObj);
      });

      // Added for handling WP refresh in WP tab of A2L Outline View
      for (SortedSet<A2lWpResponsibility> a2lWpResponsibilitySet : a2lWpDefnModel.getA2lWPResponsibleMap().values()) {
        refreshWpForA2lOutline(a2lWPNewData, a2lWPOldData, a2lWpResponsibilitySet);
      }
      // Added for handling WP refresh in WP-Param and Parameter tab of A2L Outline View
      for (SortedSet<A2lWpResponsibility> a2lWpResponsibilitySet : getA2lWpInfoBo().getA2lWpParamMappingModel()
          .getA2lWPResponsibleMap().values()) {
        refreshWpForA2lOutline(a2lWPNewData, a2lWPOldData, a2lWpResponsibilitySet);
      }

      a2lWpDefnModel.getA2lWpRespNodeMergedMap().remove(a2lWPOldData.getName());
      a2lWpDefnModel.getA2lWpRespNodeMergedMap().put(a2lWPNewData.getName(), mappedA2lWpRespSet);
    }
  }

  /**
   * @param a2lWPNewData
   * @param a2lWPOldData
   * @param a2lWpResponsibilitySet
   */
  private void refreshWpForA2lOutline(final A2lWorkPackage a2lWPNewData, final A2lWorkPackage a2lWPOldData,
      final SortedSet<A2lWpResponsibility> a2lWpResponsibilitySet) {
    for (A2lWpResponsibility a2lWpResponsibility : a2lWpResponsibilitySet) {
      if (a2lWPOldData.getId().equals(a2lWpResponsibility.getA2lWpId())) {
        a2lWpResponsibility.setName(a2lWPNewData.getName());
        a2lWpResponsibility.setWpNameCust(a2lWPNewData.getNameCustomer());
        a2lWpResponsibility.setDescription(a2lWPNewData.getDescription());
      }
    }
  }

  /**
   * Method to refresh a2lWorkPackage related changes remotely.
   */
  public void refreshWpForRemoteChngs(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        A2lWorkPackage newData = getNewA2lWpData(data);
        A2lWorkPackage oldData = getA2lWpInfoBo().getAllWpMapppedToPidcVers().get(data.getObjId());
        refreshA2lWPRespMap(newData, oldData);
        updateWorkPkgsMap(newData);
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        updateWorkPkgsMap(getNewA2lWpData(data));
      }
    }

  }

  /**
   * Get New A2lWorkPackage from service for remote changes.
   *
   * @param data ChangeDataInfo
   */
  private A2lWorkPackage getNewA2lWpData(final ChangeDataInfo data) {
    A2lWorkPackage a2lWp = null;
    try {
      a2lWp = new A2lWorkPackageServiceClient().getById(data.getObjId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return a2lWp;
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
      CommonUtils.shallowCopy(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2l(), newData);
    }

  }

  /**
   * @param a2lGrpVarMapping
   * @return
   */
  private boolean isMappingApplicable(final A2lVarGrpVariantMapping a2lGrpVarMapping) {
    Long a2lVarGroupId = a2lGrpVarMapping.getA2lVarGroupId();
    return this.detailsStrucModel.getA2lVariantGrpMap().get(a2lVarGroupId) != null;
  }

  public boolean isVarAvailable(final PidcVariant variant) {

    // for delete operation please take the already existing data model
    if (variant.isDeleted()) {
      PidcVariant a2lMAppedVarAvailable = this.detailsStrucModel.getA2lMappedVariantsMap().get(variant.getId());
      return a2lMAppedVarAvailable != null;
    }

    // for create and update reload the model
    A2LDetailsStructureModel a2lDetailsModel = getA2lDetailsModel(this.wpVersionId);

    if (a2lDetailsModel != null) {
      PidcVariant a2lMAppedVarAvailable = this.detailsStrucModel.getA2lMappedVariantsMap().get(variant.getId());
      return a2lMAppedVarAvailable != null;
    }
    return false;
  }

  /**
   * @param wpDefVerId wpDefVerId
   * @return the details model
   */
  public A2LDetailsStructureModel getA2lDetailsModel(final Long wpDefVerId) {
    try {
      A2lVariantGroupServiceClient varGrpServiceClient = new A2lVariantGroupServiceClient();
      this.detailsStrucModel = varGrpServiceClient.getA2lDetailsStructureData(wpDefVerId);
      this.a2lWpInfoBo.setDetailsStrucModel(this.detailsStrucModel);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return this.detailsStrucModel;
  }

  /**
   * Refresh wp defn version.
   *
   * @param chData the ch data
   */
  private void refreshWpDefnVersion(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      try {
        A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
        // to include changes related to both newly created version as well as existing active version
        Map<Long, A2lWpDefnVersion> versMap =
            client.getWPDefnVersForPidcA2l(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2lId());
        this.a2lWpInfoBo.getA2lWpDefnVersMap().putAll(versMap);
        A2lWpDefnVersion wpDefn = (A2lWpDefnVersion) chData.getNewData();
        // set the flag to true in A2lWpRespModel after importing Parameter Assignment ,when resp is different from wp
        // level to allowing resetting of wp resp
        this.a2lWpInfoBo.getA2lWpDefnModel()
            .setParamMappingAllowed(wpDefn.isWorkingSet() && wpDefn.isParamLevelChgAllowedFlag());
        if (wpDefn.isMasterRefreshApplicable()) {
          this.a2lWpInfoBo.initializeModelBasedOnWpDefVers(wpDefn.getId(), true, true);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2lWpDefnVersion wpDefn = (A2lWpDefnVersion) chData.getNewData();
      this.a2lWpInfoBo.getA2lWpDefnModel()
          .setParamMappingAllowed(wpDefn.isWorkingSet() && wpDefn.isParamLevelChgAllowedFlag());
      this.a2lWpInfoBo.getA2lWpDefnVersMap().put(wpDefn.getId(), wpDefn);
    }
  }


  public void refreshWpRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {

          removeOldDataForWpRespRemote(data);


          A2lWpResponsibility wpResp = new A2lWpResponsibilityServiceClient().get(Long.valueOf(data.getObjId()));
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().remove(data.getObjId());
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().put(data.getObjId(), wpResp);

          // changes to add user details to map
          addUserDetailsForResp(wpResp);
          this.a2lWpInfoBo.constructA2lWpDefRespMap(this.a2lWpInfoBo.getA2lWpDefnModel());
          // contruct the map for the outline view
          // Updating with new A2lWpRespMap in A2lParamapping Model for outline view if only the A2lWpResp is mapped to
          // any parameter
          if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) &&
              this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().containsKey(wpResp.getId())) {
            this.a2lWpInfoBo.getRespName(this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap(),
                this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpResp.getA2lRespId()),
                wpResp);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().remove(data.getObjId());
        this.a2lWpInfoBo.constructA2lWpDefRespMap(this.a2lWpInfoBo.getA2lWpDefnModel());
      }

    }

  }

  /**
   * @param data
   */
  private void removeOldDataForWpRespRemote(final ChangeDataInfo data) {
    // To Remove old data for maps used in outline page only for update operation
    if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) &&
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().containsKey(data.getObjId())) {
      A2lWpResponsibility oldWpResp =
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(Long.valueOf(data.getObjId()));
      String oldRespName = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(oldWpResp.getA2lRespId()).getName();
      // Updating the A2lWpRespMap in A2lParamapping Model
      if (this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().containsKey(oldRespName)) {
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(oldRespName);
      }
    }
  }

  /**
   * @param wpResp
   * @throws ApicWebServiceException
   */
  private void addUserDetailsForResp(final A2lWpResponsibility wpResp) throws ApicWebServiceException {
    if ((wpResp.getA2lRespId() != null) &&
        !this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().containsKey(wpResp.getA2lRespId())) {

      this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().put(wpResp.getA2lRespId(),
          new A2lResponsibilityServiceClient().get(wpResp.getA2lRespId()));

      Long userId =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpResp.getA2lRespId()).getUserId();
      if ((userId != null) && !this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().containsKey(userId)) {

        this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().put(userId,
            new UserServiceClient().getApicUserById(userId));
      }

    }
  }

  public void refreshA2lRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          A2lResponsibility a2lResp = new A2lResponsibilityServiceClient().get(Long.valueOf(data.getObjId()));
          A2lResponsibility a2lRespOld =
              getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lResp.getId());
          getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().put(data.getObjId(), a2lResp);

          Long userId =
              this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lResp.getId()).getUserId();
          if ((userId != null) && !getA2lWpInfoBo().getA2lResponsibilityModel().getUserMap().containsKey(userId)) {
            getA2lWpInfoBo().getA2lResponsibilityModel().getUserMap().put(userId,
                new UserServiceClient().getApicUserById(userId));
          }
          setA2lResponsibilityForOutlinePage(a2lResp, a2lRespOld);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        A2lResponsibility a2lResp = (A2lResponsibility) data.getRemovedData();
        getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().remove(a2lResp.getId());
        getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(a2lResp.getName());
      }

    }
  }


  /**
   * Method to set the a2lresponsibilty for the wp def and param mapping page
   *
   * @param a2lResp
   * @param a2lRespOld
   */
  private void setA2lResponsibilityForOutlinePage(final A2lResponsibility a2lResp, final A2lResponsibility a2lRespOld) {
    if (getA2lWpInfoBo() != null) {
      // Update the A2l Resp for wp def page outline view
      if ((getA2lWpInfoBo().getA2lWpDefnModel() != null) && (a2lRespOld != null) &&
          getA2lWpInfoBo().getA2lWpDefnModel().getA2lWPResponsibleMap().containsKey(a2lRespOld.getName())) {
        SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
            getA2lWpInfoBo().getA2lWpDefnModel().getA2lWPResponsibleMap().get(a2lRespOld.getName());
        getA2lWpInfoBo().getA2lWpDefnModel().getA2lWPResponsibleMap().remove(a2lRespOld.getName());
        getA2lWpInfoBo().getA2lWpDefnModel().getA2lWPResponsibleMap().put(a2lResp.getName(), a2lWpResponsibilities);
      }
      // Update the A2l Resp for paramter page outline view
      if ((getA2lWpInfoBo().getA2lWpParamMappingModel() != null) && (a2lRespOld != null) &&
          getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().containsKey(a2lRespOld.getName())) {
        SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
            getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(a2lRespOld.getName());
        getA2lWpInfoBo().getA2lWpParamMappingModel().getParamIdWithWpAndRespMap().entrySet()
            .forEach(paramWPMap -> paramWPMap.getValue().entrySet().forEach(wpRespMap -> {
              if (wpRespMap.getValue().equals(a2lRespOld.getName())) {
                wpRespMap.setValue(a2lResp.getName());
              }
            }));
        a2lWpResponsibilities.forEach(a2lWpResp -> a2lWpResp.setMappedWpRespName(a2lResp.getName()));

        getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(a2lRespOld.getName());
        getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().put(a2lResp.getName(),
            a2lWpResponsibilities);
      }
    }
  }


  /**
   * Method to handle A2lWpDefinitionVersion remote changes
   *
   * @param chDataInfoMap - change data map from cns server
   */
  private void refreshWPDefnVersionForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if ((data.getChangeType() == CHANGE_OPERATION.UPDATE) || (data.getChangeType() == CHANGE_OPERATION.CREATE)) {

        try {
          A2lWpDefnVersion wpDefnVersion = new A2lWpDefinitionVersionServiceClient().get(Long.valueOf(data.getObjId()));
          this.a2lWpInfoBo.getA2lWpDefnModel().setParamMappingAllowed(wpDefnVersion.isParamLevelChgAllowedFlag());
          this.a2lWpInfoBo.getA2lWpDefnVersMap().remove(wpDefnVersion.getId());
          this.a2lWpInfoBo.getA2lWpDefnVersMap().put(wpDefnVersion.getId(), wpDefnVersion);
          if (data.isMasterRefreshApplicable()) {
            this.a2lWpInfoBo.initializeModelBasedOnWpDefVers(Long.valueOf(data.getObjId()), true, true);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        this.a2lWpInfoBo.getA2lWpDefnVersMap().remove(data.getObjId());
      }

    }
  }


  /**
   * Method to refresh A2lWpResponsibility related changes.
   *
   * @param chData the ch data
   */
  public void refreshWpResp(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lWpResponsibility oldWpResp = (A2lWpResponsibility) chData.getOldData();
      // For Update of Workpackage Responsible status in ActiveA2lWpDefnModel
      if ((chData.getOldData() != null) && (this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap()
          .get(((A2lWpResponsibility) chData.getOldData()).getId()) == null)) {
        A2lWpResponsibility newWpResp = (A2lWpResponsibility) chData.getNewData();
        if (CommonUtils.isNotNull(this.a2lWpInfoBo.getActiveA2lWpDefnModel().getActiveWpDefnVersionId()) &&
            this.a2lWpInfoBo.getActiveA2lWpDefnModel().getActiveWpDefnVersionId().equals(newWpResp.getWpDefnVersId())) {
          A2lResponsibility a2lResponsibility =
              this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(newWpResp.getA2lRespId());
          SortedSet<A2lWpResponsibility> a2lWpResp =
              this.a2lWpInfoBo.getActiveA2lWpDefnModel().getA2lWPResponsibleMap().get(a2lResponsibility.getName());
          if (a2lWpResp.contains(oldWpResp)) {
            newWpResp.setMappedWpRespName(a2lResponsibility.getName());
            a2lWpResp.remove(oldWpResp);
            a2lWpResp.add(newWpResp);
          }
        }
      }
      else {
        updateA2lWpRespValues(chData, oldWpResp);
      }
    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE))

    {
      A2lWpResponsibility wpResp = (A2lWpResponsibility) chData.getOldData();
      removeOldDataFromOutline(chData);
      this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().remove(wpResp.getId());
      this.a2lWpInfoBo.constructA2lWpDefRespMap(this.a2lWpInfoBo.getA2lWpDefnModel());
    }
  }

  /**
   * @param chData
   * @param oldWpResp
   */
  private void updateA2lWpRespValues(final ChangeData<?> chData, A2lWpResponsibility oldWpResp) {
    // To Remove old data for maps used in outline page

    Map<String, SortedSet<A2lWpResponsibility>> a2lDefWpResponsibleMap =
        cloneHashMap(this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap());
    Map<String, SortedSet<A2lWpResponsibility>> a2lParamWpResponsibleMap =
        cloneHashMap(this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap());

    A2lWpResponsibility oldWpRespTest = null;
    String oldRespName = null;
    if (chData.getOldData() != null) {
      oldWpRespTest = (A2lWpResponsibility) chData.getOldData();
      oldWpResp = this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(oldWpRespTest.getId());
      oldRespName = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(oldWpResp.getA2lRespId())
          .getName();
      removeOldWpRespByRespName(a2lDefWpResponsibleMap, oldWpResp, oldRespName);
      // Updating the A2lWpRespMap in A2lParamapping Model
      updateA2lParamMappingModel(a2lParamWpResponsibleMap, oldWpResp, oldRespName);
    }

    A2lWpResponsibility wpResp = (A2lWpResponsibility) chData.getNewData();
    this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().put(wpResp.getId(), wpResp);
    String respName =
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpResp.getA2lRespId()).getName();
    SortedSet<A2lWpResponsibility> respSet =
        this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().get(respName);
    if (respSet == null) {
      respSet = new TreeSet<>();
    }
    respSet.add(wpResp);
    this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().put(respName, respSet);

    Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap =
        this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWpRespNodeMergedMap();
    Set<A2lWpResponsibility> wpRespSet = a2lWpRespNodeMergedMap.get(wpResp.getName());
    if (wpRespSet == null) {
      wpRespSet = new TreeSet<>();
    }
    wpRespSet.add(wpResp);

    a2lWpRespNodeMergedMap.put(wpResp.getName(), wpRespSet);

    // copy of a2lWpRespNodeMergedMap is created in order to avoid ConcurrentModificationException
    Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMapCopy = new HashMap<>();

    // case - same WPs at default and variant group level -- only relevant elements should be present in the set
    // of a2lWpRespNodeMergedMap
    checkAndUpdateRelevantData(chData, oldWpResp, wpResp, a2lWpRespNodeMergedMap, a2lWpRespNodeMergedMapCopy);

    // contruct the map for the outline view
    // Updating the A2lWpRespMap in A2lParamapping Model for outline view if only the A2lWpResp is mapped to any
    // parameter
    constructOutlineViewForA2lWpResp(chData, wpResp, respName);
  }

  /**
   * @param a2lDefWpResponsibleMap
   * @param oldWpResp
   * @param oldRespName
   */
  private void removeOldWpRespByRespName(final Map<String, SortedSet<A2lWpResponsibility>> a2lDefWpResponsibleMap,
      final A2lWpResponsibility oldWpResp, final String oldRespName) {
    if (a2lDefWpResponsibleMap.containsKey(oldRespName)) {
      for (A2lWpResponsibility a2lWpResponsibility : a2lDefWpResponsibleMap.get(oldRespName)) {
        if (a2lWpResponsibility.equals(oldWpResp)) {
          this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().get(oldRespName).remove(a2lWpResponsibility);
        }
      }
      if (this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().get(oldRespName).isEmpty()) {
        this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().remove(oldRespName);
      }
    }
  }

  /**
   * @param chData
   * @param wpResp
   * @param respName
   */
  private void constructOutlineViewForA2lWpResp(final ChangeData<?> chData, final A2lWpResponsibility wpResp,
      final String respName) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) &&
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().containsKey(wpResp.getId())) {
      this.a2lWpInfoBo.getRespName(this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap(),
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpResp.getA2lRespId()), wpResp);
      for (A2LWpParamInfo a2lWpParamInfo : this.a2lWpInfoBo.getA2lWParamInfoMap().values()) {
        if (a2lWpParamInfo.getWpRespId().equals(wpResp.getId())) {
          this.a2lWpInfoBo.constructParamIdMap(
              this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap(), a2lWpParamInfo, wpResp,
              respName);
        }
      }
      this.a2lWpInfoBo.setReconstructA2lWpParamMapApplicable(false);
    }
  }

  /**
   * @param a2lParamWpResponsibleMap
   * @param oldWpResp
   * @param oldRespName
   */
  private void updateA2lParamMappingModel(final Map<String, SortedSet<A2lWpResponsibility>> a2lParamWpResponsibleMap,
      final A2lWpResponsibility oldWpResp, final String oldRespName) {
    if (a2lParamWpResponsibleMap.containsKey(oldRespName)) {
      for (A2lWpResponsibility a2lWpResponsibility : a2lParamWpResponsibleMap.get(oldRespName)) {
        if (a2lWpResponsibility.equals(oldWpResp)) {
          this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(oldRespName)
              .remove(a2lWpResponsibility);
        }
      }
      if (this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(oldRespName).isEmpty()) {
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(oldRespName);
      }
    }
  }

  /**
   * @param chData
   * @param oldWpResp
   * @param wpResp
   * @param a2lWpRespNodeMergedMap
   * @param a2lWpRespNodeMergedMapCopy
   */
  private void checkAndUpdateRelevantData(final ChangeData<?> chData, final A2lWpResponsibility oldWpResp,
      final A2lWpResponsibility wpResp, final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap,
      final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMapCopy) {
    if ((null != oldWpResp) && chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) &&
        a2lWpRespNodeMergedMap.containsKey(oldWpResp.getName()) &&
        oldWpResp.getA2lRespId().equals(wpResp.getA2lRespId())) {

      for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {

        for (A2lWpResponsibility a2lWpResp : wpEntry.getValue()) {

          // if default level's WP is edited, variant group's WP should not get edited and vice versa
          checkDefaultLevelForVarGrp(oldWpResp, a2lWpRespNodeMergedMapCopy, a2lWpResp);

        }
      }
      // a2lWpRespNodeMergedMapCopy contents should be set for displaying in the outline view
      this.a2lWpInfoBo.getA2lWpDefnModel().setA2lWpRespNodeMergedMap(a2lWpRespNodeMergedMapCopy);

    }
  }

  /**
   * @param oldWpResp
   * @param a2lWpRespNodeMergedMapCopy
   * @param a2lWpResp
   */
  private void checkDefaultLevelForVarGrp(final A2lWpResponsibility oldWpResp,
      final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMapCopy, final A2lWpResponsibility a2lWpResp) {
    if (!((a2lWpResp.getName().equals(oldWpResp.getName())) && validateVarGrpId(oldWpResp, a2lWpResp))) {

      // only add the relevant elements to the set of the a2lWpRespNodeMergedMapCopy
      Set<A2lWpResponsibility> wpRespPalSet = a2lWpRespNodeMergedMapCopy.get(a2lWpResp.getName());
      if (null == wpRespPalSet) {
        wpRespPalSet = new HashSet<>();
      }
      wpRespPalSet.add(a2lWpResp);

      a2lWpRespNodeMergedMapCopy.put(a2lWpResp.getName(), wpRespPalSet);
    }
  }

  /**
   * @param oldWpResp
   * @param a2lWpResp
   * @return
   */
  private boolean validateVarGrpId(final A2lWpResponsibility oldWpResp, final A2lWpResponsibility a2lWpResp) {
    return isOldNewVarGrpIdNull(oldWpResp, a2lWpResp) || isOldNewVarGrpIdSame(oldWpResp, a2lWpResp);
  }

  /**
   * @param oldWpResp
   * @param a2lWpResp
   * @return
   */
  private boolean isOldNewVarGrpIdSame(final A2lWpResponsibility oldWpResp, final A2lWpResponsibility a2lWpResp) {
    return (null != a2lWpResp.getVariantGrpId()) && a2lWpResp.getVariantGrpId().equals(oldWpResp.getVariantGrpId());
  }

  /**
   * @param oldWpResp
   * @param a2lWpResp
   * @return
   */
  private boolean isOldNewVarGrpIdNull(final A2lWpResponsibility oldWpResp, final A2lWpResponsibility a2lWpResp) {
    return (null == oldWpResp.getVariantGrpId()) && (null == a2lWpResp.getVariantGrpId());
  }

  private Map<String, SortedSet<A2lWpResponsibility>> cloneHashMap(
      final Map<String, SortedSet<A2lWpResponsibility>> a2lWPRespoinsibleMap) {
    return a2lWPRespoinsibleMap.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> new TreeSet<A2lWpResponsibility>(e.getValue())));
  }

  /**
   * @param chData
   */
  private void removeOldDataFromOutline(final ChangeData<?> chData) {
    if (chData.getOldData() != null) {
      String oldRespName = null;
      A2lWpResponsibility oldWpRespTest = (A2lWpResponsibility) chData.getOldData();
      A2lWpResponsibility oldWpResp = this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(oldWpRespTest.getId());
      oldRespName = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(oldWpResp.getA2lRespId())
          .getName();
      if (this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().containsKey(oldRespName)) {
        this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWPResponsibleMap().remove(oldRespName);
      }

      Map<Long, Set<Long>> paramAndRespPalMap = this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap();
      Set<Long> paramIdsSet = paramAndRespPalMap.get(oldWpRespTest.getId());
      if (null != paramIdsSet) {
        Map<Long, String> wpIdRespMap = new HashMap<>();
        for (Long paramId : paramIdsSet) {
          wpIdRespMap.putAll(this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap().get(paramId));
        }

        wpIdRespMap.entrySet().forEach(wpRespEntrySet -> {
          if (wpRespEntrySet.getKey().equals(oldWpResp.getId())) {
            this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(wpRespEntrySet.getValue());
          }
        });

      }
    }
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
      A2lResponsibility a2lRespOld =
          getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lResp.getId());
      getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().put(a2lResp.getId(), a2lResp);
      setA2lResponsibilityForOutlinePage(a2lResp, a2lRespOld);
    }
    if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      A2lResponsibility a2lResp = (A2lResponsibility) chData.getOldData();
      getA2lWpInfoBo().getA2lResponsibilityModel().getA2lResponsibilityMap().remove(a2lResp.getId());
      getA2lWpInfoBo().getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(a2lResp.getName());
    }
  }

  /**
   * Method to refresh a2lWpParamMapping related changes.
   *
   * @param chData the ch data
   */
  public void refreshA2lWpParamMap(final ChangeData<?> chData) {

    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {

      A2lWpParamMapping a2lWpParamMapping = (A2lWpParamMapping) chData.getNewData();

      A2lWpParamMapping oldA2lWpParamMapping =
          this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWpParamMapping().get(a2lWpParamMapping.getId());

      // Done during Update Operation for Default Level alone
      removeOldRespfromOutline(chData, a2lWpParamMapping);
      // Done for Create operation for virtual level alone
      removeVirtualRespNodsFromOutline(chData, a2lWpParamMapping);

      this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWpParamMapping().put(a2lWpParamMapping.getId(),
          a2lWpParamMapping);

      A2lWpResponsibility a2lWpResponsibility =
          getA2lWpInfoBo().getA2lWpDefnModel().getWpRespMap().get(a2lWpParamMapping.getWpRespId());

      A2LWpParamInfo a2lWpParamInfo;
      if (a2lWpResponsibility.getVariantGrpId() == null) {
        // Set A2lWpParamMapping details in edited row object
        a2lWpParamInfo = this.a2lWpInfoBo.getA2lWParamInfoMap().get(a2lWpParamMapping.getParamId());
      }
      else {
        a2lWpParamInfo = this.a2lWpInfoBo.getA2lWParamInfoMap().get(a2lWpParamMapping.getParamId()).clone();
      }

      constructOutlineForWpParamInfo(a2lWpParamMapping, a2lWpResponsibility, a2lWpParamInfo);

      removeA2lWpRespForUpdate(chData, oldA2lWpParamMapping);
    }

    else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      removeDeletedA2lWpResp(chData);
    }
    this.a2lWpInfoBo.setReconstructA2lWpParamMapApplicable(true);
  }

  /**
   * @param chData
   */
  private void removeDeletedA2lWpResp(final ChangeData<?> chData) {
    A2lWpParamMapping deletedA2lWpParamMapping = (A2lWpParamMapping) chData.getOldData();
    A2lWpResponsibility a2lWpResponsibility =
        getA2lWpInfoBo().getA2lWpDefnModel().getWpRespMap().get(deletedA2lWpParamMapping.getWpRespId());
    A2lResponsibility a2lResponsibility;
    if (null != a2lWpResponsibility) {
      if (!deletedA2lWpParamMapping.isWpRespInherited()) {
        a2lResponsibility = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
            .get(deletedA2lWpParamMapping.getParA2lRespId());
      }
      else {
        a2lResponsibility = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
            .get(a2lWpResponsibility.getA2lRespId());
      }
      // removes data from outline view when Reseting the Resp for params at variant group level
      removeOldA2lWpRespMapForOutline(a2lWpResponsibility, a2lResponsibility);

      if ((this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap()
          .get(a2lWpResponsibility.getId()) != null)) {
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().get(a2lWpResponsibility.getId())
            .remove(deletedA2lWpParamMapping.getParamId());
        if (this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().get(a2lWpResponsibility.getId())
            .isEmpty()) {
          this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().remove(a2lWpResponsibility.getId());
        }
      }
      this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWpParamMapping().remove(deletedA2lWpParamMapping.getId());
      this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
          .remove(deletedA2lWpParamMapping.getParamId());

    }
  }

  /**
   * @param chData
   * @param oldA2lWpParamMapping
   */
  private void removeA2lWpRespForUpdate(final ChangeData<?> chData, final A2lWpParamMapping oldA2lWpParamMapping) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2lWpResponsibility olda2lWpResponsibility =
          getA2lWpInfoBo().getA2lWpDefnModel().getWpRespMap().get(oldA2lWpParamMapping.getWpRespId());
      if ((null != olda2lWpResponsibility) && (null != this.a2lWpInfoBo.getA2lWpParamMappingModel()
          .getParamAndRespPalMap().get(olda2lWpResponsibility.getId()))) {
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().get(olda2lWpResponsibility.getId())
            .remove(oldA2lWpParamMapping.getParamId());
        if (this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().get(olda2lWpResponsibility.getId())
            .isEmpty()) {
          this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamAndRespPalMap().remove(olda2lWpResponsibility.getId());
        }
      }
    }
  }

  /**
   * @param a2lWpParamMapping
   * @param a2lWpResponsibility
   * @param a2lWpParamInfo
   */
  private void constructOutlineForWpParamInfo(final A2lWpParamMapping a2lWpParamMapping,
      final A2lWpResponsibility a2lWpResponsibility, final A2LWpParamInfo a2lWpParamInfo) {
    if (a2lWpParamInfo != null) {
      setA2lWpParamMapping(a2lWpParamMapping, a2lWpParamInfo);

      String respName = "";
      if (a2lWpParamInfo.isWpRespInherited()) {
        // contruct the map for the outline view
        respName = this.a2lWpInfoBo.getRespName(
            this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap(), this.a2lWpInfoBo
                .getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpResponsibility.getA2lRespId()),
            a2lWpResponsibility);
      }
      else {
        // contruct the map for the outline view
        respName = this.a2lWpInfoBo.getRespName(this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap(),
            this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId()),
            a2lWpResponsibility);
      }
      this.a2lWpInfoBo.constructParamIdMap(this.a2lWpInfoBo.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap(),
          a2lWpParamInfo, a2lWpResponsibility, respName);
    }
  }

  /**
   * For Removing outline data for variant node
   *
   * @param chData
   * @param a2lWpParamMapping
   */
  private void removeVirtualRespNodsFromOutline(final ChangeData<?> chData, final A2lWpParamMapping a2lWpParamMapping) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      for (Map<Long, A2LWpParamInfo> paramIdMap : this.a2lWpInfoBo.getVirtualRecordsMap().values()) {
        if (paramIdMap.containsKey(a2lWpParamMapping.getParamId())) {
          A2LWpParamInfo a2lWpParamInfo = paramIdMap.get(a2lWpParamMapping.getParamId());
          if (isVarGroupMatches(a2lWpParamInfo)) {
            removeWpRespMap(a2lWpParamInfo);
          }
        }
      }
    }
  }

  /**
   * @param a2lWpParamInfo
   */
  private void removeWpRespMap(final A2LWpParamInfo a2lWpParamInfo) {
    A2lWpResponsibility a2lWpResponsibilityOld =
        getA2lWpInfoBo().getA2lWpDefnModel().getWpRespMap().get(a2lWpParamInfo.getWpRespId());
    A2lResponsibility a2lResponsibility;
    if (a2lWpParamInfo.isWpRespInherited()) {
      // contruct the map for the outline view
      a2lResponsibility = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(a2lWpResponsibilityOld.getA2lRespId());
    }
    else {
      // contruct the map for the outline view
      a2lResponsibility =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
    }

    removeOldA2lWpRespMapForOutline(a2lWpResponsibilityOld, a2lResponsibility);
  }

  /**
   * @param a2lWpParamInfo
   * @return
   */
  private boolean isVarGroupMatches(final A2LWpParamInfo a2lWpParamInfo) {
    return (a2lWpParamInfo != null) && (a2lWpParamInfo.getVariantGroupId() != null) &&
        (this.a2lWpInfoBo.getSelectedA2lVarGroup() != null) &&
        a2lWpParamInfo.getVariantGroupId().equals(this.a2lWpInfoBo.getSelectedA2lVarGroup().getId());
  }

  /**
   * For Removing outline data for Default node
   *
   * @param chData
   * @param a2lWpParamMapping
   */
  private void removeOldRespfromOutline(final ChangeData<?> chData, final A2lWpParamMapping a2lWpParamMapping) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2LWpParamInfo cloneParamInfo =
          this.a2lWpInfoBo.getA2lWParamInfoMap().get(a2lWpParamMapping.getParamId()).clone();
      A2lWpResponsibility a2lWpResponsibilityOld =
          getA2lWpInfoBo().getA2lWpDefnModel().getWpRespMap().get(cloneParamInfo.getWpRespId());
      A2lResponsibility a2lResponsibility;
      if (null != a2lWpResponsibilityOld) {
        if (cloneParamInfo.isWpRespInherited()) {
          // contruct the map for the outline view
          a2lResponsibility = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
              .get(a2lWpResponsibilityOld.getA2lRespId());
        }
        else {
          // contruct the map for the outline view
          a2lResponsibility =
              this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(cloneParamInfo.getA2lRespId());

        }

        removeOldA2lWpRespMapForOutline(a2lWpResponsibilityOld, a2lResponsibility);
      }
    }
  }

  /**
   * Remove wp and Resp from getA2lWpParamMappingModel().getA2lWPResponsibleMap()
   *
   * @param a2lWpResponsibility
   * @param a2lResponsibility
   */
  private void removeOldA2lWpRespMapForOutline(final A2lWpResponsibility a2lWpResponsibility,
      final A2lResponsibility a2lResponsibility) {
    if ((null != a2lWpResponsibility) && CommonUtils.isNotNull(a2lResponsibility) &&
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap()
            .containsKey(a2lResponsibility.getName()) &&
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(a2lResponsibility.getName())
            .contains(a2lWpResponsibility)) {
      this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(a2lResponsibility.getName())
          .remove(a2lWpResponsibility);
      if (this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(a2lResponsibility.getName())
          .isEmpty()) {
        this.a2lWpInfoBo.getA2lWpParamMappingModel().getA2lWPResponsibleMap().remove(a2lResponsibility.getName());
      }
    }
  }


  /**
   * Sets the A 2 l wp param mapping.
   *
   * @param a2lWpParamMapping A2lWpParamMapping
   * @param a2lWpParamInfo A2LWpParamInfo
   */
  public void setA2lWpParamMapping(final A2lWpParamMapping a2lWpParamMapping, final A2LWpParamInfo a2lWpParamInfo) {
    if (CommonUtils.isNotNull(a2lWpParamInfo)) {
      a2lWpParamInfo.setA2lWpParamMappingId(a2lWpParamMapping.getId());

      a2lWpParamInfo.setWpRespId(a2lWpParamMapping.getWpRespId());
      a2lWpParamInfo.setWpNameInherited(a2lWpParamMapping.isWpNameCustInherited());
      a2lWpParamInfo.setWpRespInherited(a2lWpParamMapping.isWpRespInherited());

      if (a2lWpParamMapping.isWpNameCustInherited()) {
        A2lWpResponsibility palObj =
            this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(a2lWpParamMapping.getWpRespId());
        a2lWpParamInfo.setWpNameCust(palObj.getWpNameCust());
      }
      else {
        a2lWpParamInfo.setWpNameCust(a2lWpParamMapping.getWpNameCust());
      }

      setWpRespUser(a2lWpParamInfo, a2lWpParamMapping);
    }
  }


  /**
   * @param a2lWpParamInfo
   * @param a2lWpParamMapping
   */
  private void setWpRespUser(final A2LWpParamInfo a2lWpParamInfo, final A2lWpParamMapping a2lWpParamMapping) {
    if (a2lWpParamMapping.isWpRespInherited()) {
      A2lWpResponsibility a2lResp =
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(a2lWpParamMapping.getWpRespId());
      a2lWpParamInfo.setA2lRespId(a2lResp.getA2lRespId());

    }
    else {
      a2lWpParamInfo.setA2lRespId(a2lWpParamMapping.getParA2lRespId());
    }

  }

  /**
   * Refresh A 2 l wp param map for remote chng.
   *
   * @param chDataInfoMap the ch data info map
   */
  private void refreshA2lWpParamMapForRemoteChng(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // Do complete refresh for remote change
    this.a2lWpInfoBo.initializeModelBasedOnWpDefVers(this.a2lWpInfoBo.getA2lWpDefnModel().getSelectedWpDefnVersionId(),
        false, true);
  }

  /**
   * Gets the a 2 l wp info bo.
   *
   * @return the a2lWpInfoBo
   */
  public A2LWPInfoBO getA2lWpInfoBo() {
    return this.a2lWpInfoBo;
  }


  /**
   * @param wpVersionId wpVersionId
   */
  public void setWpVersionId(final Long wpVersionId) {
    this.wpVersionId = wpVersionId;
  }


  /**
   * @return the wpVersionId
   */
  public Long getWpVersionId() {
    return this.wpVersionId;
  }


  /**
   * @return the detailsStrucModel
   */
  public A2LDetailsStructureModel getDetailsStrucModel() {
    return this.detailsStrucModel;
  }

}
