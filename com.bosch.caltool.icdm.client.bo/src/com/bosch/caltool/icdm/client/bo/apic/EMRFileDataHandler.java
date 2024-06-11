/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class EMRFileDataHandler extends AbstractClientDataHandler {

  private final EMRFileBO emrFileBO;

  /**
   * @param emrFileBO EMRFileBO
   */
  public EMRFileDataHandler(final EMRFileBO emrFileBO) {
    this.emrFileBO = emrFileBO;
  }

  /**
   * @return the emrSheetBO
   */
  public EMRFileBO getEmrFileBO() {
    return this.emrFileBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.EMR_FILE, chData -> {
      Long pidcVerId = ((EmrFile) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getEmrFileBO().getPidcVerId(), pidcVerId);
    });
    registerCnsActionLocal(this::refreshEMRFile, MODEL_TYPE.EMR_FILE);
    registerCnsAction(this::refreshEMRFileForRemoteChanges, MODEL_TYPE.EMR_FILE);

    registerCnsChecker(MODEL_TYPE.EMR_PIDC_VARIANT, chData -> {
      Long emrFileId = ((EmrPidcVariant) CnsUtils.getModel(chData)).getEmrFileId();
      return getEmrFileBO().getFileMap().keySet().contains(emrFileId);
    });

    registerCnsActionLocal(this::refreshEMRFileWithVariants, MODEL_TYPE.EMR_PIDC_VARIANT);
    registerCnsAction(this::refreshEMRFileWithVarForRemoteChanges, MODEL_TYPE.EMR_PIDC_VARIANT);
    // Cns checker for changes in NodeAccess Object
    registerCnsCheckerForNodeAccess(MODEL_TYPE.PIDC, this::getPidc);
  }

  /**
   * @param action
   * @param emrPidcVariant
   */
  private void refreshEMRFileWithVarForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)||data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        getEmrFileBO().getCodexExcelFileResultWS();
      }
    }
  }


  /**
   * @param action
   * @param emrPidcVariant
   */
  private void refreshEMRFileWithVariants(final ChangeData<?> chData) {

    SortedSet<PidcVariant> pidcVariantSet;
    PidcVariantServiceClient pidcVarServiceClient = new PidcVariantServiceClient();

    try {

      // to refresh ui when variant is assigned to an emr file
      if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {

        EmrPidcVariant emrPidcVariant = (EmrPidcVariant) chData.getNewData();
        pidcVariantSet = getMappedPidcVariant(emrPidcVariant);

        pidcVariantSet.add(pidcVarServiceClient.get(emrPidcVariant.getPidcVariantId()));
      }
      // to refresh nat page when variant mapping is deleted
      else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {

        EmrPidcVariant emrPidcVariant = (EmrPidcVariant) chData.getOldData();
        pidcVariantSet = getMappedPidcVariant(emrPidcVariant);

        pidcVariantSet.remove(pidcVarServiceClient.get(emrPidcVariant.getPidcVariantId()));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param emrPidcVariant
   * @return
   */
  private SortedSet<PidcVariant> getMappedPidcVariant(final EmrPidcVariant emrPidcVariant) {
    return this.emrFileBO.getFileMap().get(emrPidcVariant.getEmrFileId()).getVariantSet();
  }


  /**
   * @param action
   * @param emrFile
   */
  private void refreshEMRFile(final ChangeData<?> chData) {
    // to refresh ui when emr file is uploaded without variants
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      EmrFile emrFile = (EmrFile) chData.getNewData();
      EmrFileMapping emrFileMapping = new EmrFileMapping();
      emrFileMapping.setEmrFile(emrFile);
      this.emrFileBO.getFileMap().put(emrFile.getId(), emrFileMapping);
      // When variants are not assigned to emr file
      if (!emrFile.getIsVariant()) {
        // When variants are not assigned to emr file
        emrFileMapping.setVariantSet(this.emrFileBO.getPidcVersionBO().getVariantsSet());
      }


    }
    // to refresh changes related to update description or delete flag in emr file table
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      EmrFile emrFile = (EmrFile) chData.getNewData();

      EmrFileMapping emrFileMapping = this.emrFileBO.getFileMap().get(emrFile.getId());
      emrFileMapping.setEmrFile(emrFile);
    }
  }


  private void refreshEMRFileForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        getEmrFileBO().getCodexExcelFileResultWS();
      }
    }
  }

  /**
   * @return Pidc
   */
  private Pidc getPidc() {
    return getEmrFileBO().getPidcVersionBO().getPidc();
  }
}