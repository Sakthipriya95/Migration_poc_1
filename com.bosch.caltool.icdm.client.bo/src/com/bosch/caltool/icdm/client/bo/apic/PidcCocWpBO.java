/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.cocwp.PidcSubVarCocWpServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.cocwp.PidcVariantCocWpServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.cocwp.PidcVersCocWpServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author PDH2COB
 */
public class PidcCocWpBO {

  /**
   * Title of message confirmation dialog
   */
  private static final String QUESTION_MSG_DIALOG_TITLE = "Remove CoC WP at Variant";

  /**
   * Message to display in message confirmation dialog
   */
  private static final String QUESTION_TO_DISPLAY_PREFIX =
      "The following CoC Work Packages at variant/sub-variant level will be moved to PIDC/Variant level respectively:\n\n";

  private static final String QUESTION_TO_DISPLAY_SUFFIX = "Do you want to continue?";

  private PidcVersCocWpData pidcVersCocWpData;

  private PidcVersionBO pidcVersionBO;
  /**
   * Map to collect coc wp at child level which is marked as not relevant at parent
   */
  private final Map<IProjectCoCWP, List<IProjectCoCWP>> irrelevantWpCocpParentToChildMap = new HashMap<>();

  private final Map<IProjectCoCWP, String> movedToParentUsedFlagMap = new HashMap<>();


  /**
   * @param pidcVersionBO - BO of selection Pidc Version
   */
  public PidcCocWpBO(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * Method to refresh whole CoC WP data
   */
  public void refreshPidcVersCocWpData() {
    this.pidcVersCocWpData = null;
    getPidcVersCocWpData();
  }


  /**
   * Method to get PidcVersCocWp using ID
   *
   * @param pidcVerCocWpId as ID
   * @throws ApicWebServiceException as Exception
   */
  public PidcVersCocWp gerPidcVersCocWp(final Long pidcVerCocWpId) throws ApicWebServiceException {
    return new PidcVersCocWpServiceClient().get(pidcVerCocWpId);
  }

  /**
   * Method to get PidcVarCocWp using ID
   *
   * @param pidcVarCocWpId as ID
   * @throws ApicWebServiceException as Exception
   */
  public PidcVariantCocWp gerPidcVarCocWp(final Long pidcVarCocWpId) throws ApicWebServiceException {
    return new PidcVariantCocWpServiceClient().getbyId(pidcVarCocWpId);
  }

  /**
   * Method to get PidcSubVarCocWp using ID
   *
   * @param pidcSubVarCocWpId as ID
   * @throws ApicWebServiceException as Exception
   */
  public PidcSubVarCocWp gerPidcSubVarCocWp(final Long pidcSubVarCocWpId) throws ApicWebServiceException {
    return new PidcSubVarCocWpServiceClient().getbyId(pidcSubVarCocWpId);
  }

  /**
   * @param activePage for check.
   * @return true if value is set for qnaire config attribute
   */
  public boolean validateQnaireConfigAttrValueInPidcVersion(final boolean activePage) {
    boolean isValueSet = true;
    try {
      Long attrId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      if (CommonUtils.isNull(this.pidcVersionBO.getAttributes().get(attrId).getValue())) {
        isValueSet = false;
        // Only For COC WP page.
        if (activePage) {
          CDMLogger.getInstance().errorDialog(
              "Value for iCDM Questionnnaire Config attribute is not set. Please set a value to view COC Work Packages",
              Activator.PLUGIN_ID);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return isValueSet;
  }

  /**
   * Method to update the Pidc Coco WP
   *
   * @param pidcCocWpUpdationInputModel as input
   * @return PIDCCocWpUpdationModel
   */
  public PIDCCocWpUpdationModel updatePidcCocWPs(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel) {
    PIDCCocWpUpdationModel updatePidcCocWPs = null;
    try {
      pidcCocWpUpdationInputModel.setPidcVersionOld(this.pidcVersionBO.getPidcVersion());
      updatePidcCocWPs = new PidcVersCocWpServiceClient().updatePidcCocWPs(pidcCocWpUpdationInputModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return updatePidcCocWPs;
  }

  /**
   * Method to move the Pidc VariantCocWp to Common Level
   *
   * @param selectedPidcVariantCocWpList as
   */
  public void pidcVarCocWpMoveToCommon(final List<PidcVariantCocWp> selectedPidcVariantCocWpList) {

    Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate = new HashMap<>();
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap = new HashMap<>();

    if (canCocWpMovedFromVariantToPidc(selectedPidcVariantCocWpList)) {

      // populating variant and pidc version for moving to pidc level from variant
      populateVarMapForUpdationModel(selectedPidcVariantCocWpList, pidcVersCocWpBeforeUpdate, pidcVarCocWpDeletionMap);

      // setting maps in updation model and then calling service
      PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
      pidcCocWpUpdationInputModel.setPidcVersCocWpBeforeUpdate(pidcVersCocWpBeforeUpdate);
      pidcCocWpUpdationInputModel.setPidcVarCocWpDeletionMap(pidcVarCocWpDeletionMap);
      updatePidcCocWPs(pidcCocWpUpdationInputModel);
    }
  }


  /**
   * @param selectedPidcVariantCocWpList
   * @param pidcVersCocWpBeforeUpdate
   * @param pidcVarCocWpDeletionMap
   */
  private void populateVarMapForUpdationModel(final List<PidcVariantCocWp> selectedPidcVariantCocWpList,
      final Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap) {
    for (PidcVariantCocWp pidcVariantCocWp : getAllPidcCocVariant(selectedPidcVariantCocWpList)) {
      setPidcVarUpdAndDelMap(pidcVersCocWpBeforeUpdate, pidcVarCocWpDeletionMap, pidcVariantCocWp);
    }
  }


  /**
   * @param pidcVersCocWpBeforeUpdate
   * @param pidcVarCocWpDeletionMap
   * @param pidcVariantCocWp
   */
  private void setPidcVarUpdAndDelMap(final Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap, final PidcVariantCocWp pidcVariantCocWp) {
    // setting PidcVariantCocWp for delete
    if (pidcVarCocWpDeletionMap.containsKey(pidcVariantCocWp.getPidcVariantId())) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpMapLcl = pidcVarCocWpDeletionMap.get(pidcVariantCocWp.getPidcVariantId());
      pidcVarCocWpMapLcl.put(pidcVariantCocWp.getWPDivId(), pidcVariantCocWp);
    }
    else {
      // key - WPdivId Value - PidcVariantCocWp
      Map<Long, PidcVariantCocWp> pidcVarCocWpMap = new HashMap<>();
      pidcVarCocWpMap.put(pidcVariantCocWp.getWPDivId(), pidcVariantCocWp);
      pidcVarCocWpDeletionMap.put(pidcVariantCocWp.getPidcVariantId(), pidcVarCocWpMap);
    }

    // setting PidcVersCocWp for Update
    PidcVersCocWp pidcVersCocWp =
        getPidcVersCocWpData().getPidcVersCocWpMap().get(pidcVariantCocWp.getWPDivId()).clone();
    if (CommonUtils.isNotEmpty(this.movedToParentUsedFlagMap)) {
      pidcVersCocWp.setUsedFlag(
          this.movedToParentUsedFlagMap.getOrDefault(pidcVersCocWp, CoCWPUsedFlag.NOT_DEFINED.getDbType()));
    }
    pidcVersCocWp.setAtChildLevel(false);

    pidcVersCocWpBeforeUpdate.put(pidcVariantCocWp.getWPDivId(), pidcVersCocWp);
  }

  /**
   * method to check If a Coc WP can be moved from Variant level to PIDC level selected vers coc wp list to be passed
   * returns true if it is movable
   */
  private boolean canCocWpMovedFromVariantToPidc(final List<PidcVariantCocWp> selectedPidcVersCocWpList) {

    for (PidcVariantCocWp pidcVariantCocWp : selectedPidcVersCocWpList) {
      long wPDivId = pidcVariantCocWp.getWPDivId();
      if (!canCocWpMovedFromVariantToPidcByWpDivId(wPDivId)) {
        return false;
      }
    }
    return true;
  }

  /**
   * method to check If a Coc WP can be moved from Variant level to PIDC level wpDiv Id of the selected coc wp to be
   * passed returns true if it is movable
   */
  private boolean canCocWpMovedFromVariantToPidcByWpDivId(final long wPDivId) {
    for (Entry<Long, PidcVariant> varVal : this.pidcVersionBO.getVariantsMap().entrySet()) {
      Map<Long, PidcVariantCocWp> pidcVariantCocWpMap =
          this.pidcVersCocWpData.getPidcVarCocWpMap().get(varVal.getKey());
      if (pidcVariantCocWpMap.containsKey(wPDivId)) {
        PidcVariantCocWp pidcVarCocWp = pidcVariantCocWpMap.get(wPDivId);
        if (pidcVarCocWp.isAtChildLevel()) {
          CDMLogger.getInstance().infoDialog(
              "Workpackage is defined at Sub-Variant for Variant : " + varVal.getValue().getName(),
              Activator.PLUGIN_ID);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * method to get the coc wp from all the variants selected variant coc wp list to be passed returns set of all variant
   * coc wp
   */
  private Set<PidcVariantCocWp> getAllPidcCocVariant(final List<PidcVariantCocWp> selectedPidcVarCocWpList) {
    Set<PidcVariantCocWp> pidcVariantCocWpSet = new HashSet<>();
    for (PidcVariantCocWp pidcVariantCocWp : selectedPidcVarCocWpList) {
      long wPDivId = pidcVariantCocWp.getWPDivId();
      addAllPidcCocVariantToSet(pidcVariantCocWpSet, wPDivId);
    }
    return pidcVariantCocWpSet;
  }


  /**
   * @param pidcVariantCocWpSet
   * @param wPDivId
   */
  private void addAllPidcCocVariantToSet(final Set<PidcVariantCocWp> pidcVariantCocWpSet, final long wPDivId) {
    for (Entry<Long, PidcVariant> varVal : this.pidcVersionBO.getVariantsMap().entrySet()) {
      Map<Long, PidcVariantCocWp> pidcVariantCocWpMap =
          this.pidcVersCocWpData.getPidcVarCocWpMap().get(varVal.getKey());
      pidcVariantCocWpSet.add(pidcVariantCocWpMap.get(wPDivId));
    }
  }

  /**
   * method to get the coc wp from all the variants selected variant coc wp WP Div Id to be passed returns set of all
   * variant coc wp
   */
  private Set<PidcVariantCocWp> getAllPidcCocVariantByWPDivId(final long wPDivId) {
    Set<PidcVariantCocWp> pidcVariantCocWpSet = new HashSet<>();
    addAllPidcCocVariantToSet(pidcVariantCocWpSet, wPDivId);
    return pidcVariantCocWpSet;
  }

  /**
   * method to get the coc wp from all the sub variants for selected variant selected variant coc wp list , selected
   * pidcvariant to be passed returns set of all sub variant coc wp
   */
  private Set<PidcSubVarCocWp> getAllPidcCocSubVariant(final List<PidcSubVarCocWp> selectedPidcSubVersCocWpList,
      final PidcVariant pidcVariant) {
    Set<PidcSubVarCocWp> pidcSubVariantCocWpSet = new HashSet<>();
    for (PidcSubVarCocWp pidcSubVariantCocWp : selectedPidcSubVersCocWpList) {
      long wPDivId = pidcSubVariantCocWp.getWPDivId();
      addAllPidcCocSubVariantToSet(pidcVariant, pidcSubVariantCocWpSet, wPDivId);
    }
    return pidcSubVariantCocWpSet;
  }


  /**
   * @param pidcVariant
   * @param pidcSubVariantCocWpSet
   * @param wPDivId
   */
  private void addAllPidcCocSubVariantToSet(final PidcVariant pidcVariant,
      final Set<PidcSubVarCocWp> pidcSubVariantCocWpSet, final long wPDivId) {
    for (PidcSubVariant subVarVal : this.pidcVersionBO.getSubVariantsforSelVariant(pidcVariant.getId(), true)) {
      Map<Long, PidcSubVarCocWp> pidcSubVariantCocWpMap =
          this.pidcVersCocWpData.getPidcSubVarCocWpMap().get(subVarVal.getId());
      pidcSubVariantCocWpSet.add(pidcSubVariantCocWpMap.get(wPDivId));
    }
  }

  /**
   * method to get the coc wp from all the sub variants for selected variant selected variant coc wp list , selected
   * pidcvariant to be passed returns set of all sub variant coc wp
   */
  private Set<PidcSubVarCocWp> getAllPidcCocSubVariantForWPDivId(final long wPDivId, final PidcVariant pidcVariant) {
    Set<PidcSubVarCocWp> pidcSubVariantCocWpSet = new HashSet<>();
    addAllPidcCocSubVariantToSet(pidcVariant, pidcSubVariantCocWpSet, wPDivId);

    return pidcSubVariantCocWpSet;
  }

  /**
   * Method to move the Pidc Sub VariantCocWp to Variant Level
   *
   * @param selectedPidcSubVarCocWpList as
   * @param pidcVariant pidcVariant
   */
  public void pidcSubVarCocWpMoveToVariant(final List<PidcSubVarCocWp> selectedPidcSubVarCocWpList,
      final PidcVariant pidcVariant) {

    // Key - subvariant id, Value - Map of WPdivId with PidcSubVar Coc WP
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap = new HashMap<>();
    // key - variant id, Value - Map of WPdivId with PidcVar Coc WP
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate = new HashMap<>();

    // populate sub-variant and variant maps for moving to variant
    populateSubVarMapForUpdationModel(selectedPidcSubVarCocWpList, pidcVariant, pidcSubVarCocWpDeletionMap,
        pidcVarCocWpMapBeforeUpdate);

    // set maps in updation model and call service
    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    pidcCocWpUpdationInputModel.setPidcSubVarCocWpDeletionMap(pidcSubVarCocWpDeletionMap);
    pidcCocWpUpdationInputModel.setPidcVarCocWpMapBeforeUpdate(pidcVarCocWpMapBeforeUpdate);
    updatePidcCocWPs(pidcCocWpUpdationInputModel);

  }


  /**
   * @param selectedPidcSubVarCocWpList
   * @param pidcVariant
   * @param pidcSubVarCocWpDeletionMap
   * @param pidcVarCocWpMapBeforeUpdate
   */
  private void populateSubVarMapForUpdationModel(final List<PidcSubVarCocWp> selectedPidcSubVarCocWpList,
      final PidcVariant pidcVariant, final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate) {
    for (PidcSubVarCocWp pidcSubVarCocWp : getAllPidcCocSubVariant(selectedPidcSubVarCocWpList, pidcVariant)) {
      setPidcVarCocForUpdAndDel(pidcVariant, pidcSubVarCocWpDeletionMap, pidcVarCocWpMapBeforeUpdate, pidcSubVarCocWp);

    }
  }

  /**
   * @param pidcVariant
   * @param pidcSubVarCocWpDeletionMap
   * @param pidcVarCocWpMapBeforeUpdate
   * @param pidcSubVarCocWp
   */
  private void setPidcVarCocForUpdAndDel(final PidcVariant pidcVariant,
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate, final PidcSubVarCocWp pidcSubVarCocWp) {
    // setting PidcVariantCocWp for delete
    if (pidcSubVarCocWpDeletionMap.containsKey(pidcSubVarCocWp.getPidcSubVarId())) {
      Map<Long, PidcSubVarCocWp> pidcVarCocWpMapLcl = pidcSubVarCocWpDeletionMap.get(pidcSubVarCocWp.getPidcSubVarId());
      pidcVarCocWpMapLcl.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
    }
    else {
      // key - WPdivId Value - PidcVariantCocWp
      Map<Long, PidcSubVarCocWp> pidcVarCocWpMap = new HashMap<>();
      pidcVarCocWpMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
      pidcSubVarCocWpDeletionMap.put(pidcSubVarCocWp.getPidcSubVarId(), pidcVarCocWpMap);
    }

    // setting PidcVersCocWp for Update
    PidcVariantCocWp pidcVarCocWp =
        getPidcVersCocWpData().getPidcVarCocWpMap().get(pidcVariant.getId()).get(pidcSubVarCocWp.getWPDivId()).clone();
    if (CommonUtils.isNotEmpty(this.movedToParentUsedFlagMap)) {
      pidcVarCocWp
          .setUsedFlag(this.movedToParentUsedFlagMap.getOrDefault(pidcVarCocWp, CoCWPUsedFlag.NOT_DEFINED.getDbType()));
    }
    pidcVarCocWp.setAtChildLevel(false);

    if (pidcVarCocWpMapBeforeUpdate.containsKey(pidcVarCocWp.getPidcVariantId())) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpMapLcl = pidcVarCocWpMapBeforeUpdate.get(pidcVarCocWp.getPidcVariantId());
      pidcVarCocWpMapLcl.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
    }
    else {
      // key - WPdivId Value - PidcVariantCocWp
      Map<Long, PidcVariantCocWp> pidcVarCocWpMap = new HashMap<>();
      pidcVarCocWpMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
      pidcVarCocWpMapBeforeUpdate.put(pidcVarCocWp.getPidcVariantId(), pidcVarCocWpMap);
    }
  }

  /**
   * Method to move the Pidc Sub VariantCocWp to Variant Level
   *
   * @param wPDivId wPDivId
   * @param pidcVariant PidcVariant
   */
  public void pidcSubVarCocWpMoveToVariantForSingle(final long wPDivId, final PidcVariant pidcVariant) {

    // Key - subvariant id, Value - Map of WPdivId with PidcSubVar Coc WP
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap = new HashMap<>();
    // key - variant id, Value - Map of WPdivId with PidcVar Coc WP
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate = new HashMap<>();

    for (PidcSubVarCocWp pidcSubVarCocWp : getAllPidcCocSubVariantForWPDivId(wPDivId, pidcVariant)) {
      setPidcVarCocForUpdAndDel(pidcVariant, pidcSubVarCocWpDeletionMap, pidcVarCocWpMapBeforeUpdate, pidcSubVarCocWp);
    }

    // set updation model and call service
    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    pidcCocWpUpdationInputModel.setPidcSubVarCocWpDeletionMap(pidcSubVarCocWpDeletionMap);
    pidcCocWpUpdationInputModel.setPidcVarCocWpMapBeforeUpdate(pidcVarCocWpMapBeforeUpdate);
    updatePidcCocWPs(pidcCocWpUpdationInputModel);

  }

  /**
   * Method to Move the varCocWp to Sub Variant
   *
   * @param selectedPidcVarCocWpList as input
   */
  public void pidcVarCocWpMoveToSubVariant(final List<PidcVariantCocWp> selectedPidcVarCocWpList) {
    // key - variant id, Value - Map of WPdivId with PidcVar Coc
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate = new HashMap<>();
    // Key - subvariant id, Value - Map of WPdivId with PidcSubVar Coc WP
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpCreationMap = new HashMap<>();

    for (PidcVariantCocWp pidcVariantCocWp : selectedPidcVarCocWpList) {

      PidcVariantCocWp pidcVariantCocWpClone = pidcVariantCocWp.clone();
      // before moving to the child level mark parent coc wp as relevant at parent level
      pidcVariantCocWpClone.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      pidcVariantCocWpClone.setAtChildLevel(true);

      if (pidcVarCocWpMapBeforeUpdate.containsKey(pidcVariantCocWp.getPidcVariantId())) {
        Map<Long, PidcVariantCocWp> pidcVarCocWpMapLcl =
            pidcVarCocWpMapBeforeUpdate.get(pidcVariantCocWp.getPidcVariantId());
        pidcVarCocWpMapLcl.put(pidcVariantCocWp.getWPDivId(), pidcVariantCocWpClone);
      }
      else {
        // key - WPdivId Value - PidcVariantCocWp
        Map<Long, PidcVariantCocWp> pidcVarCocWpMap = new HashMap<>();
        pidcVarCocWpMap.put(pidcVariantCocWp.getWPDivId(), pidcVariantCocWpClone);
        pidcVarCocWpMapBeforeUpdate.put(pidcVariantCocWp.getPidcVariantId(), pidcVarCocWpMap);
      }
      constrctPidcSubVarCocWpCreationMap(pidcSubVarCocWpCreationMap, pidcVariantCocWp);
    }
    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();

    pidcCocWpUpdationInputModel.setPidcSubVarCocWpCreationMap(pidcSubVarCocWpCreationMap);
    pidcCocWpUpdationInputModel.setPidcVarCocWpMapBeforeUpdate(pidcVarCocWpMapBeforeUpdate);

    updatePidcCocWPs(pidcCocWpUpdationInputModel);
  }

  /**
   * @param pidcSubVarCocWpCreationMap
   * @param pidcVariantCocWp
   */
  private void constrctPidcSubVarCocWpCreationMap(
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpCreationMap, final PidcVariantCocWp pidcVariantCocWp) {
    for (PidcSubVariant pidcSubVariant : getPidcVersionBO()
        .getSubVariantsforSelVariant(pidcVariantCocWp.getPidcVariantId(), true)) {

      PidcSubVarCocWp pidcSubVarCocWp = new PidcSubVarCocWp();

      pidcSubVarCocWp.setName(pidcVariantCocWp.getName());
      pidcSubVarCocWp.setDescription(pidcVariantCocWp.getDescription());
      pidcSubVarCocWp.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      pidcSubVarCocWp.setPidcSubVarId(pidcSubVariant.getId());
      pidcSubVarCocWp.setAtChildLevel(false);
      pidcSubVarCocWp.setWPDivId(pidcVariantCocWp.getWPDivId());

      if (pidcSubVarCocWpCreationMap.containsKey(pidcSubVariant.getId())) {
        Map<Long, PidcSubVarCocWp> pidcSubVarCocWpMapLcl = pidcSubVarCocWpCreationMap.get(pidcSubVariant.getId());
        pidcSubVarCocWpMapLcl.put(pidcVariantCocWp.getWPDivId(), pidcSubVarCocWp);
      }
      else {
        // key - WPdivId Value - PidcVariantCocWp
        Map<Long, PidcSubVarCocWp> pidcSubVarCocWpMap = new HashMap<>();
        pidcSubVarCocWpMap.put(pidcVariantCocWp.getWPDivId(), pidcSubVarCocWp);
        pidcSubVarCocWpCreationMap.put(pidcSubVariant.getId(), pidcSubVarCocWpMap);
      }
    }
  }


  /**
   * Move Coc WP from PIDC to Variant level
   *
   * @param selectedPidcVersCocWpList selectedPidcVersCocWpList
   */
  public void pidcVerCocWPMoveToVariant(final List<PidcVersCocWp> selectedPidcVersCocWpList) {
    // key - variant id, Value - Map of WPdivId with PidcVar Coc
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpCreationMap = new HashMap<>();
    // Key - WP Div Id, Value - PidcVersCocWP
    Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate = new HashMap<>();
    // Key - WP Div Id, Value - PidcVersCocWP
    Map<Long, PidcVersCocWp> pidcVersCocWpCreationMap = new HashMap<>();
    for (PidcVersCocWp versCocWp : selectedPidcVersCocWpList) {
      PidcVersCocWp pidcVersCocWpClone = versCocWp.clone();
      // before moving to the child level mark parent coc wp as relevant at parent level
      pidcVersCocWpClone.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      pidcVersCocWpClone.setAtChildLevel(true);
      if (CommonUtils.isNull(pidcVersCocWpClone.getId())) {

        pidcVersCocWpCreationMap.put(pidcVersCocWpClone.getWPDivId(), pidcVersCocWpClone);
      }
      else {
        pidcVersCocWpBeforeUpdate.put(pidcVersCocWpClone.getWPDivId(), pidcVersCocWpClone);
      }

      constrctPidcVarCocWpCreationMap(pidcVersCocWpClone, pidcVarCocWpCreationMap);
    }

    if (isOkToUpdUsedFlag(false, selectedPidcVersCocWpList.get(0))) {
      PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();

      pidcCocWpUpdationInputModel.setPidcVarCocWpCreationMap(pidcVarCocWpCreationMap);
      pidcCocWpUpdationInputModel.setPidcVersCocWpCreationMap(pidcVersCocWpCreationMap);
      pidcCocWpUpdationInputModel.setPidcVersCocWpBeforeUpdate(pidcVersCocWpBeforeUpdate);

      // set flag InvokedOnUsedFlagUpd because on moving PidcVersCocWP to child, the used flag will automatically update
      // to 'Y'
      pidcCocWpUpdationInputModel.setInvokedOnUsedFlagUpd(true);
      updatePidcCocWPs(pidcCocWpUpdationInputModel);
    }
  }

  /**
   * @param pidcVersCocWp pidc version coc workpackage
   * @param pidcVarCocWpCreationMap map for creating pidc variant coc workpackage
   */
  private void constrctPidcVarCocWpCreationMap(final PidcVersCocWp pidcVersCocWp,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpCreationMap) {
    for (Entry<Long, PidcVariant> pidcVariantEntrySet : getPidcVersionBO().getVariantsMap().entrySet()) {
      PidcVariant pidcVariant = pidcVariantEntrySet.getValue();

      PidcVariantCocWp pidcVariantCocWp = new PidcVariantCocWp();
      pidcVariantCocWp.setName(pidcVersCocWp.getName());
      pidcVariantCocWp.setDescription(pidcVersCocWp.getDescription());
      pidcVariantCocWp.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      pidcVariantCocWp.setPidcVariantId(pidcVariant.getId());
      pidcVariantCocWp.setAtChildLevel(false);
      pidcVariantCocWp.setWPDivId(pidcVersCocWp.getWPDivId());

      if (pidcVarCocWpCreationMap.containsKey(pidcVariant.getId())) {
        Map<Long, PidcVariantCocWp> pidcVarCocWpMapLcl = pidcVarCocWpCreationMap.get(pidcVariant.getId());
        pidcVarCocWpMapLcl.put(pidcVersCocWp.getWPDivId(), pidcVariantCocWp);
      }
      else {
        // key - WPdivId Value - PidcVariantCocWp
        Map<Long, PidcVariantCocWp> pidcVarCocWpMap = new HashMap<>();
        pidcVarCocWpMap.put(pidcVersCocWp.getWPDivId(), pidcVariantCocWp);
        pidcVarCocWpCreationMap.put(pidcVariant.getId(), pidcVarCocWpMap);
      }
    }
  }

  /**
   * @return the pidcVersCocWpData
   */
  public PidcVersCocWpData getPidcVersCocWpData() {
    if (CommonUtils.isNull(this.pidcVersCocWpData)) {
      try {
        this.pidcVersCocWpData =
            new PidcVersCocWpServiceClient().getAllCocWpByPidcVersId(this.pidcVersionBO.getPidcVersion().getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.pidcVersCocWpData;
  }

  /**
   * @param selUsedFlagName String
   * @param wpUsedFlag String
   * @return String used flag name
   */
  public String getNewValofSelUsedFlag(final String selUsedFlagName, final String wpUsedFlag) {
    return selUsedFlagName.equals(wpUsedFlag) ? wpUsedFlag : selUsedFlagName;
  }

  /**
   * @param usedFlagColName selected used flag column
   * @return used flag equivalent stored in the DB
   */
  private String convertUsedFlagColNameToDbType(final int checkboxColIndex) {
    String usedFlagDbType = "";
    if (ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX == checkboxColIndex) {
      return CoCWPUsedFlag.NOT_DEFINED.getDbType();
    }
    if (ApicConstants.COC_WP_NO_COLUMN_INDEX == checkboxColIndex) {
      return CoCWPUsedFlag.NO.getDbType();
    }
    if (ApicConstants.COC_WP_YES_COLUMN_INDEX == checkboxColIndex) {
      return CoCWPUsedFlag.YES.getDbType();
    }
    return usedFlagDbType;
  }

  /***
   * @param selIProjectCoCWPList - (pidcversion/pidcvariant/pidc subvariant) workpackages
   * @param usedFlagName selected used flag column
   * @param pidcCocWpUpdationInputModel model to store edited workpackages at (pidcversion/pidcvariant/pidc subvariant)
   */
  public void setUsedFlagToUpdate(final String usedFlagName, final List<IProjectCoCWP> selIProjectCoCWPList,
      final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel) {

    if (selIProjectCoCWPList.get(0) instanceof PidcVersCocWp) {
      editProjectUsedFlagInfo(selIProjectCoCWPList, pidcCocWpUpdationInputModel, usedFlagName);
    }
    if (selIProjectCoCWPList.get(0) instanceof PidcVariantCocWp) {
      editProjectVarUsedFlagInfo(selIProjectCoCWPList, pidcCocWpUpdationInputModel, usedFlagName);
    }
    if (selIProjectCoCWPList.get(0) instanceof PidcSubVarCocWp) {
      editProjectSubVarWpUsedFlag(selIProjectCoCWPList, pidcCocWpUpdationInputModel, usedFlagName);
    }
  }

  /**
   * @param checkboxColIndex index of the selected column with checkbox
   * @return boolean(T/F) whether selected check box column is a used flag column
   */
  public boolean isUsedFlagCol(final int checkboxColIndex) {
    return (ApicConstants.COC_WP_YES_COLUMN_INDEX == checkboxColIndex) ||
        (ApicConstants.COC_WP_NO_COLUMN_INDEX == checkboxColIndex) ||
        (ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX == checkboxColIndex);
  }

  /**
   * @param checkboxColIndex index of the selected column with checkbox
   * @param selIProjectCoCWPList selected wp coc with check box
   */
  public void editUsedFlagViaChkBox(final int checkboxColIndex, final List<IProjectCoCWP> selIProjectCoCWPList) {
    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();

    if (isOkToUpdUsedFlag(true, selIProjectCoCWPList.get(0))) {
      setUsedFlagToUpdate(convertUsedFlagColNameToDbType(checkboxColIndex), selIProjectCoCWPList,
          pidcCocWpUpdationInputModel);
      pidcCocWpUpdationInputModel.setInvokedOnUsedFlagUpd(true);
      updatePidcCocWPs(pidcCocWpUpdationInputModel);
    }
  }

  /**
   * @param iProjectCoCWP
   * @return
   */
  public boolean isOkToUpdUsedFlag(final boolean isInvokedOnUsedFlagUpd, final IProjectCoCWP iProjectCoCWP) {
    boolean isOkToUpdUsedFlag = true;

    if (iProjectCoCWP instanceof PidcVersCocWp) {
      StringBuilder popupMessage = new StringBuilder();
      if (!isInvokedOnUsedFlagUpd) {
        popupMessage.append("Moving CoC Wp to child level will automatically update used flag to 'Yes'. ");
      }
      popupMessage.append(
          "On updating used flag of CoC WP, the relevant use case section will be added/removed as project use case. Would you like to proceed?");
      isOkToUpdUsedFlag =
          MessageDialogUtils.getConfirmMessageDialog("Update on Project Use Case", popupMessage.toString());
    }
    return isOkToUpdUsedFlag;
  }


  /**
   * @param iprojectCoCWPList list of wp cocs selected
   * @param pidcCocWpUpdationInputModel model containing edited wp cocs
   * @param selFlagName selected used flag
   */
  public void editProjectSubVarWpUsedFlag(final List<IProjectCoCWP> iprojectCoCWPList,
      final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel, final String selFlagName) {
    for (IProjectCoCWP iProjectCocWp : iprojectCoCWPList) {
      PidcSubVarCocWp pidcSubVarCocWp = (PidcSubVarCocWp) iProjectCocWp;
      pidcSubVarCocWp.setUsedFlag(getNewValofSelUsedFlag(selFlagName, pidcSubVarCocWp.getUsedFlag()));
      if (iProjectCocWp.getId() == null) {
        Map<Long, PidcSubVarCocWp> wpDivSubVarCocWpMap =
            null == pidcCocWpUpdationInputModel.getPidcSubVarCocWpCreationMap().get(pidcSubVarCocWp.getPidcSubVarId())
                ? new HashMap<>()
                : pidcCocWpUpdationInputModel.getPidcSubVarCocWpCreationMap().get(pidcSubVarCocWp.getPidcSubVarId());
        wpDivSubVarCocWpMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
        pidcCocWpUpdationInputModel.getPidcSubVarCocWpCreationMap().put(pidcSubVarCocWp.getPidcSubVarId(),
            wpDivSubVarCocWpMap);
      }
      else {

        Map<Long, PidcSubVarCocWp> wpDivSubVarCocWpMap = null == pidcCocWpUpdationInputModel
            .getPidcSubVarCocWpBeforeUpdateMap().get(pidcSubVarCocWp.getPidcSubVarId()) ? new HashMap<>()
                : pidcCocWpUpdationInputModel.getPidcSubVarCocWpBeforeUpdateMap()
                    .get(pidcSubVarCocWp.getPidcSubVarId());
        wpDivSubVarCocWpMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
        pidcCocWpUpdationInputModel.getPidcSubVarCocWpBeforeUpdateMap().put(pidcSubVarCocWp.getPidcSubVarId(),
            wpDivSubVarCocWpMap);
      }
    }

  }

  /**
   * @param iProjectCocWPList list of wp cocs selected
   * @param pidcCocWpUpdationInputModel model containing edited wp cocs
   * @param selUsedFlagDbType selected used flag
   */
  public void editProjectUsedFlagInfo(final List<IProjectCoCWP> iProjectCocWPList,
      final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel, final String selUsedFlagDbType) {

    // populate updation model maps for updating used flags of wp coc at child and same level
    setUsedFlagInUpdModel(iProjectCocWPList, pidcCocWpUpdationInputModel, selUsedFlagDbType);
    confirmNDeleteCoCWpAtVar(pidcCocWpUpdationInputModel);
    resetIrrelevantChildWpCocMaps();
  }


  /**
   * @param iProjectCocWPList
   * @param pidcCocWpUpdationInputModel
   * @param selUsedFlagDbType
   */
  private void setUsedFlagInUpdModel(final List<IProjectCoCWP> iProjectCocWPList,
      final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel, final String selUsedFlagDbType) {

    for (IProjectCoCWP iProjectCocWp : iProjectCocWPList) {

      // check if selected wp coc is at child level
      if (isNotRelCocAtVariant(iProjectCocWp, selUsedFlagDbType)) {
        moveToChildLevelWpCoC(iProjectCocWp, selUsedFlagDbType);
      }
      // wp coc at same level
      else {
        updateCocWpUsedFlag(pidcCocWpUpdationInputModel, selUsedFlagDbType, iProjectCocWp);
      }
    }
  }


  /**
   * @param pidcCocWpUpdationInputModel
   * @param selUsedFlagDbType
   * @param iProjectCocWp
   */
  private void updateCocWpUsedFlag(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel,
      final String selUsedFlagDbType, final IProjectCoCWP iProjectCocWp) {

    if (iProjectCocWp instanceof PidcVersCocWp) {
      setPidcVersCocInUpdationModel(pidcCocWpUpdationInputModel, selUsedFlagDbType, iProjectCocWp);
    }
    if (iProjectCocWp instanceof PidcVariantCocWp) {
      setVarWpCocInUpdationModel(pidcCocWpUpdationInputModel, iProjectCocWp, selUsedFlagDbType);
    }
  }


  /**
   * @param pidcCocWpUpdationInputModel
   * @param selUsedFlagDbType
   * @param iProjectCocWp
   * @param pidcVersCocWp
   */
  private void setPidcVersCocInUpdationModel(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel,
      final String selUsedFlagDbType, final IProjectCoCWP iProjectCocWp) {

    PidcVersCocWp pidcVersCocWp = (PidcVersCocWp) iProjectCocWp;
    pidcVersCocWp.setUsedFlag(getNewValofSelUsedFlag(selUsedFlagDbType, pidcVersCocWp.getUsedFlag()));
    if (CommonUtils.isNull(iProjectCocWp.getId())) {
      pidcCocWpUpdationInputModel.getPidcVersCocWpCreationMap().put(pidcVersCocWp.getWPDivId(), pidcVersCocWp);
    }
    else {
      pidcCocWpUpdationInputModel.getPidcVersCocWpBeforeUpdate().put(pidcVersCocWp.getWPDivId(), pidcVersCocWp);
    }
  }

  /**
   * @param pidcCocWpUpdationInputModel
   */
  private void deleteChildLevelCocWp(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel) {
    List<PidcVariantCocWp> pidcVarCocWpAtChildList = new ArrayList<>();
    Map<PidcVariantCocWp, List<PidcSubVarCocWp>> pidcSubVarCocWpChildMap = new HashMap<>();

    setVarSubVarMapToBeDeleted(pidcVarCocWpAtChildList, pidcSubVarCocWpChildMap);

    if (CommonUtils.isNotEmpty(pidcSubVarCocWpChildMap)) {
      // Key - subvariant id, Value - Map of WPdivId with PidcSubVar Coc WP
      Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap = new HashMap<>();
      // key - variant id, Value - Map of WPdivId with PidcVar Coc WP
      Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate = new HashMap<>();
      for (Map.Entry<PidcVariantCocWp, List<PidcSubVarCocWp>> entry : pidcSubVarCocWpChildMap.entrySet()) {

        populateSubVarMapForUpdationModel(entry.getValue(),
            this.pidcVersionBO.getVariantsMap().get(entry.getKey().getPidcVariantId()), pidcSubVarCocWpDeletionMap,
            pidcVarCocWpMapBeforeUpdate);
        pidcCocWpUpdationInputModel.setPidcSubVarCocWpDeletionMap(pidcSubVarCocWpDeletionMap);
        pidcCocWpUpdationInputModel.setPidcVarCocWpMapBeforeUpdate(pidcVarCocWpMapBeforeUpdate);
      }

    }
    if (CommonUtils.isNotEmpty(pidcVarCocWpAtChildList)) {
      // Key - WP Div Id, Value - PidcVersCocWP
      Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate = new HashMap<>();
      // key - variant id, Value - Map of WPdivId with PidcVar Coc WP
      Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap = new HashMap<>();
      populateVarMapForUpdationModel(pidcVarCocWpAtChildList, pidcVersCocWpBeforeUpdate, pidcVarCocWpDeletionMap);
      pidcCocWpUpdationInputModel.getPidcVersCocWpBeforeUpdate().putAll(pidcVersCocWpBeforeUpdate);
      pidcCocWpUpdationInputModel.setPidcVarCocWpDeletionMap(pidcVarCocWpDeletionMap);
    }
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapToUpdateNotInDeletMap =
        CommonUtils.getDifference(pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate(),
            pidcCocWpUpdationInputModel.getPidcVarCocWpDeletionMap());
    pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().clear();
    pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().putAll(pidcVarCocWpMapToUpdateNotInDeletMap);
  }


  /**
   * @param pidcVarCocWpAtChildList
   * @param pidcSubVarCocWpChildMap
   */
  private void setVarSubVarMapToBeDeleted(final List<PidcVariantCocWp> pidcVarCocWpAtChildList,
      final Map<PidcVariantCocWp, List<PidcSubVarCocWp>> pidcSubVarCocWpChildMap) {
    for (Map.Entry<IProjectCoCWP, List<IProjectCoCWP>> cocWpEntry : this.irrelevantWpCocpParentToChildMap.entrySet()) {
      // sub variant wp coc to be deleted
      if (cocWpEntry.getKey() instanceof PidcVariantCocWp) {
        List<PidcSubVarCocWp> pidcSubVarCocWpList =
            pidcSubVarCocWpChildMap.getOrDefault(cocWpEntry.getKey(), new ArrayList<>());
        pidcSubVarCocWpList.addAll((Collection<? extends PidcSubVarCocWp>) cocWpEntry.getValue());
        pidcSubVarCocWpChildMap.put((PidcVariantCocWp) cocWpEntry.getKey(), pidcSubVarCocWpList);
      }
      // variant coc to be deleted
      if (cocWpEntry.getKey() instanceof PidcVersCocWp) {
        pidcVarCocWpAtChildList.addAll((Collection<? extends PidcVariantCocWp>) cocWpEntry.getValue());
      }
    }
  }

  /**
   * @param iProjectCoCWParent
   * @param selUsedFlagDbType
   * @return
   */
  private boolean isNotRelCocAtVariant(final IProjectCoCWP iProjectCoCWParent, final String selUsedFlagDbType) {
    return isWpCocSetToNotRel(selUsedFlagDbType) && iProjectCoCWParent.isAtChildLevel();
  }


  /**
   * @param selUsedFlagDbType
   * @return
   */
  private boolean isWpCocSetToNotRel(final String selUsedFlagDbType) {
    return CoCWPUsedFlag.NO.getDbType().equals(selUsedFlagDbType) ||
        CoCWPUsedFlag.NOT_DEFINED.getDbType().equals(selUsedFlagDbType);
  }


  /**
   * @param iProjectCoCWParent
   * @param selUsedFlagDbType
   */
  private void moveToChildLevelWpCoC(final IProjectCoCWP parentWpCocAtVariant, final String selUsedFlagDbType) {


    if (parentWpCocAtVariant.isAtChildLevel()) {

      for (Map.Entry<IProjectCoCWP, List<IProjectCoCWP>> variantLevelCocWpEntry : getVariantLevelWpCoc(
          parentWpCocAtVariant).entrySet()) {
        for (IProjectCoCWP variantLevelCocWpCoc : variantLevelCocWpEntry.getValue()) {
          this.movedToParentUsedFlagMap.put(parentWpCocAtVariant, selUsedFlagDbType);
          if (variantLevelCocWpCoc instanceof PidcSubVarCocWp) {
            List<IProjectCoCWP> wpCocAtChildLevel =
                this.irrelevantWpCocpParentToChildMap.getOrDefault(parentWpCocAtVariant, new ArrayList<>());
            wpCocAtChildLevel.add(variantLevelCocWpCoc);
            this.irrelevantWpCocpParentToChildMap.put(parentWpCocAtVariant, wpCocAtChildLevel);
          }
          else {
            // wp coc at pidc variant
            List<IProjectCoCWP> wpCocAtChildLevel =
                this.irrelevantWpCocpParentToChildMap.getOrDefault(parentWpCocAtVariant, new ArrayList<>());
            wpCocAtChildLevel.add(variantLevelCocWpCoc);
            this.irrelevantWpCocpParentToChildMap.put(parentWpCocAtVariant, wpCocAtChildLevel);
            moveToChildLevelWpCoC(variantLevelCocWpCoc, selUsedFlagDbType);

          }
        }

      }
    }

  }

  private void resetIrrelevantChildWpCocMaps() {
    this.irrelevantWpCocpParentToChildMap.clear();
    this.movedToParentUsedFlagMap.clear();
  }

  /**
   * @param iProjectCoCWP
   * @return
   */
  private Map<IProjectCoCWP, List<IProjectCoCWP>> getVariantLevelWpCoc(final IProjectCoCWP iProjectCoCWP) {
    Map<IProjectCoCWP, List<IProjectCoCWP>> parentToVarLevelCocWpMap = new HashMap<>();
    if (iProjectCoCWP instanceof PidcVariantCocWp) {
      PidcVariantCocWp pidcVarCoWp = (PidcVariantCocWp) iProjectCoCWP;
      for (PidcSubVariant subVarVal : this.pidcVersionBO.getSubVariantsforSelVariant(pidcVarCoWp.getPidcVariantId(),
          true)) {
        populateSubVarCoC(iProjectCoCWP, parentToVarLevelCocWpMap, subVarVal);
      }
    }
    if (iProjectCoCWP instanceof PidcVersCocWp) {
      for (Entry<Long, PidcVariant> varVal : this.pidcVersionBO.getVariantsMap().entrySet()) {
        Map<Long, PidcVariantCocWp> pidcVariantCocWpMap =
            this.pidcVersCocWpData.getPidcVarCocWpMap().get(varVal.getKey());

        populatevarCoc(iProjectCoCWP, parentToVarLevelCocWpMap, pidcVariantCocWpMap);
      }
    }
    return parentToVarLevelCocWpMap;
  }


  /**
   * @param iProjectCoCWP
   * @param parentToVarLevelCocWpMap
   * @param pidcVariantCocWpMap
   */
  private void populatevarCoc(final IProjectCoCWP iProjectCoCWP,
      final Map<IProjectCoCWP, List<IProjectCoCWP>> parentToVarLevelCocWpMap,
      final Map<Long, PidcVariantCocWp> pidcVariantCocWpMap) {
    for (PidcVariantCocWp pidcVarCocWp : pidcVariantCocWpMap.values()) {
      if (pidcVarCocWp.getName().contains(iProjectCoCWP.getName())) {
        List<IProjectCoCWP> listofVarWpCoc = parentToVarLevelCocWpMap.getOrDefault(iProjectCoCWP, new ArrayList<>());
        listofVarWpCoc.add(pidcVarCocWp);
        parentToVarLevelCocWpMap.put(iProjectCoCWP, listofVarWpCoc);
      }
    }
  }


  /**
   * @param iProjectCoCWP
   * @param parentToVarLevelCocWpMap
   * @param subVarVal
   */
  private void populateSubVarCoC(final IProjectCoCWP iProjectCoCWP,
      final Map<IProjectCoCWP, List<IProjectCoCWP>> parentToVarLevelCocWpMap, final PidcSubVariant subVarVal) {
    Map<Long, PidcSubVarCocWp> pidcSubVariantCocWpMap =
        this.pidcVersCocWpData.getPidcSubVarCocWpMap().get(subVarVal.getId());
    for (PidcSubVarCocWp pidcSubVarCocWp : pidcSubVariantCocWpMap.values()) {
      if (pidcSubVarCocWp.getName().contains(iProjectCoCWP.getName())) {
        List<IProjectCoCWP> listofSubVarWpCoc = parentToVarLevelCocWpMap.getOrDefault(iProjectCoCWP, new ArrayList<>());
        listofSubVarWpCoc.add(pidcSubVarCocWp);
        parentToVarLevelCocWpMap.put(iProjectCoCWP, listofSubVarWpCoc);
      }
    }
  }


  /**
   * @param iProjectCoCWPList list of wp cocs selected
   * @param pidcCocWpUpdationInputModel model containing edited wp cocs
   * @param selUsedFlagDbType selected used flag
   */
  public void editProjectVarUsedFlagInfo(final List<IProjectCoCWP> iProjectCoCWPList,
      final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel, final String selUsedFlagDbType) {
    // populate updation model maps for updating used flags of wp coc at child and same level
    setUsedFlagInUpdModel(iProjectCoCWPList, pidcCocWpUpdationInputModel, selUsedFlagDbType);
    // populate updation model maps for updating used flags of wp coc at child and same level
    confirmNDeleteCoCWpAtVar(pidcCocWpUpdationInputModel);
    resetIrrelevantChildWpCocMaps();
  }


  /**
   *
   */
  private void confirmNDeleteCoCWpAtVar(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel) {
    if (CommonUtils.isNotEmpty(this.irrelevantWpCocpParentToChildMap)) {
      String moveToParentCocWpList = getMoveToParentCocWp();
      boolean hasUserConfirmedDeletion = MessageDialogUtils.getQuestionMessageDialog(QUESTION_MSG_DIALOG_TITLE,
          QUESTION_TO_DISPLAY_PREFIX + moveToParentCocWpList + QUESTION_TO_DISPLAY_SUFFIX);
      if (hasUserConfirmedDeletion) {
        deleteChildLevelCocWp(pidcCocWpUpdationInputModel);
      }
    }
  }


  /**
   * flatten list of variant coc wps to move
   */
  private String getMoveToParentCocWp() {
    StringJoiner varWpCocStrinJoiner = new StringJoiner("\n");
    for (IProjectCoCWP variantCocWpToMove : this.irrelevantWpCocpParentToChildMap.keySet()) {
      String cocWpName = variantCocWpToMove.getName();
      if (!varWpCocStrinJoiner.toString().contains(cocWpName)) {
        varWpCocStrinJoiner.add(cocWpName);
      }
    }
    return varWpCocStrinJoiner.add("\n").toString();
  }


  /**
   * @param pidcCocWpUpdationInputModel
   * @param iProjectCocWP
   * @param selUsedFlagDbType
   */
  private void setVarWpCocInUpdationModel(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel,
      final IProjectCoCWP iProjectCocWP, final String selUsedFlagDbType) {
    PidcVariantCocWp pidcVarCocWp = (PidcVariantCocWp) iProjectCocWP;
    pidcVarCocWp.setUsedFlag(getNewValofSelUsedFlag(selUsedFlagDbType, pidcVarCocWp.getUsedFlag()));
    if (iProjectCocWP.getId() == null) {
      Map<Long, PidcVariantCocWp> wpDivVarCocWpMap =
          null == pidcCocWpUpdationInputModel.getPidcVarCocWpCreationMap().get(pidcVarCocWp.getPidcVariantId())
              ? new HashMap<>()
              : pidcCocWpUpdationInputModel.getPidcVarCocWpCreationMap().get(pidcVarCocWp.getPidcVariantId());
      wpDivVarCocWpMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
      pidcCocWpUpdationInputModel.getPidcVarCocWpCreationMap().put(pidcVarCocWp.getPidcVariantId(), wpDivVarCocWpMap);
    }
    else {

      Map<Long, PidcVariantCocWp> wpDivVarCocWpMap =
          null == pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().get(pidcVarCocWp.getPidcVariantId())
              ? new HashMap<>()
              : pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().get(pidcVarCocWp.getPidcVariantId());
      wpDivVarCocWpMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
      pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().put(pidcVarCocWp.getPidcVariantId(),
          wpDivVarCocWpMap);
    }
  }

  /**
   * Move PIDC WPCoc to variant Level for single coc wp
   *
   * @param pidcVersCocWp pidcVersCocWp
   */
  public void moveFromPidcToVariant(final PidcVersCocWp pidcVersCocWp) {
    List<PidcVersCocWp> pidcVersCocWpList = new ArrayList<>();
    pidcVersCocWpList.add(pidcVersCocWp);
    pidcVerCocWPMoveToVariant(pidcVersCocWpList);
  }

  /**
   * Method to move the Pidc VariantCocWp to Common Level for single coc Wp
   *
   * @param wpDivId Wp Div ID
   */
  public void pidcVarCocWpMoveToCommonForSingle(final long wpDivId) {
    // Key - WP Div Id, Value - PidcVersCocWP
    Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate = new HashMap<>();
    // key - variant id, Value - Map of WPdivId with PidcVar Coc WP
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap = new HashMap<>();

    if (canCocWpMovedFromVariantToPidcByWpDivId(wpDivId)) {
      for (PidcVariantCocWp pidcVariantCocWp : getAllPidcCocVariantByWPDivId(wpDivId)) {
        setPidcVarUpdAndDelMap(pidcVersCocWpBeforeUpdate, pidcVarCocWpDeletionMap, pidcVariantCocWp);
      }

      PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
      pidcCocWpUpdationInputModel.setPidcVersCocWpBeforeUpdate(pidcVersCocWpBeforeUpdate);
      pidcCocWpUpdationInputModel.setPidcVarCocWpDeletionMap(pidcVarCocWpDeletionMap);

      updatePidcCocWPs(pidcCocWpUpdationInputModel);
    }
  }

  /**
   * Move PIDC WPCoc from variant Level to PIDC Level for single coc wp
   *
   * @param wpDivID Wp Div ID
   */
  public void moveFromVariantToPidc(final long wpDivID) {
    pidcVarCocWpMoveToCommonForSingle(wpDivID);
  }

  /**
   * Move PIDC WPCoc to sub variant Level for single coc wp
   *
   * @param pidcVariantCocWp pidcVariantCocWp
   */
  public void moveFromVariantToSubVariant(final PidcVariantCocWp pidcVariantCocWp) {
    List<PidcVariantCocWp> pidcVariantCocWpList = new ArrayList<>();
    pidcVariantCocWpList.add(pidcVariantCocWp);
    pidcVarCocWpMoveToSubVariant(pidcVariantCocWpList);
  }

  /**
   * Move PIDC WPCoc from variant Level to PIDC Level for single coc wp
   *
   * @param pidcVariantCocWp pidcVariantCocWp
   * @param pidcVariant PidcVariant
   */
  public void moveFromSubVariantToVariant(final PidcVariantCocWp pidcVariantCocWp, final PidcVariant pidcVariant) {
    pidcSubVarCocWpMoveToVariantForSingle(pidcVariantCocWp.getWPDivId(), pidcVariant);
  }

  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }


  /**
   * @param pidcVersionBO the pidcVersionBO to set
   */
  public void setPidcVersionBO(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * Check if Variant is available for given Version
   *
   * @return boolean
   */
  public boolean isVariantAvailable() {
    return !CommonUtils.isNullOrEmpty(getPidcVersionBO().getVariantsMap(false));
  }

  /**
   * Check if Sub Variant is available for given Variant
   *
   * @param variantId variantId
   * @return boolean
   */
  public boolean isSubVariantAvailForGivenVar(final long variantId) {
    return !CommonUtils.isNullOrEmpty(getPidcVersionBO().getSubVariantsforSelVariant(variantId, false));
  }

}

