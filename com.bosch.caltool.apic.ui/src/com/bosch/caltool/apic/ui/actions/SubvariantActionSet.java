/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVarCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Action set for Subvariant Copy Actions
 * 
 * @author ukt1cob
 */
public class SubvariantActionSet {


  /**
   * This method copy Source Subvariant to destination subvariant
   *
   * @param destObject PidcSubVariant
   * @param copiedSubVar source PidcSubVariant
   */
  public void copySubVariant(final Object destObject, final PidcSubVariant copiedSubVar) {
    final PidcSubVariant destSubVariant = (PidcSubVariant) destObject;
    PidcSubVarCopyServiceClient pidcVarClient = new PidcSubVarCopyServiceClient();
    PidcSubVariantData pidcSubVarCreationData = new PidcSubVariantData();
    pidcSubVarCreationData.setSrcPidcSubVar(copiedSubVar);
    pidcSubVarCreationData.setDestPidcSubVar(destSubVariant);
    pidcSubVarCreationData.setPidcVersionId(copiedSubVar.getPidcVersionId());
    try {
      PidcSubVarPasteOutput pidcSubVarOutputData = pidcVarClient.update(pidcSubVarCreationData);
      // inform user about value not copied for already available attribute's value
      if (CommonUtils.isNotNull(pidcSubVarOutputData)) {
        attributeNotCopiedInfoDialogForSubVarCopy(pidcSubVarOutputData);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method undeletes the destination subvariant and update with the values from source subvariant
   *
   * @param pidcSubVariantToCopy sub variant to be copied
   * @param pidcSubVariantToUpdate sub variant to be updated
   * @return PidcSubVarPasteOutput
   */
  public PidcSubVarPasteOutput undoDeleteNUpdateSubVarAction(final PidcSubVariant pidcSubVariantToCopy,
      final PidcSubVariant pidcSubVariantToUpdate) {

    // undo delete by setting flag to false
    pidcSubVariantToUpdate.setDeleted(false);

    // update the values of sub-variant with values in copied sub-variant
    PidcSubVarCopyServiceClient pidcSubVarCopyClient = new PidcSubVarCopyServiceClient();

    PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
    pidcSubVarData.setSrcPidcSubVar(pidcSubVariantToCopy);
    pidcSubVarData.setDestPidcSubVar(pidcSubVariantToUpdate);
    pidcSubVarData.setPidcVersionId(pidcSubVariantToCopy.getPidcVersionId());
    pidcSubVarData.setUndoDeleteNUpdateSubVar(true);

    try {
      return pidcSubVarCopyClient.update(pidcSubVarData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return null;
  }

  /**
   * This method displays the info dialog with the attributes that will are not copied to destination subvariant
   *
   * @param pidcSubVarOutputData updated pidc sub-variant data
   */
  public void attributeNotCopiedInfoDialogForSubVarCopy(final PidcSubVarPasteOutput pidcSubVarOutputData) {
    StringBuilder attrNames = new StringBuilder();
    // append sub-variant's attribute whose value is already available in destination sub-variant
    if (CommonUtils.isNotEmpty(pidcSubVarOutputData.getValAlreadyExists())) {
      populateValAlreadyExistSubVarAttr(pidcSubVarOutputData, attrNames);
    }

    if (CommonUtils.isNotEmptyString(attrNames.toString())) {
      CDMLogger.getInstance().infoDialog(attrNames.toString(), Activator.PLUGIN_ID);
    }
  }


  /**
   * This method constructs the message for the dialog with the attributes for which values are already existing in
   * destination subvariant
   *
   * @param pidcSubVarOutputData
   * @param attrNames
   */
  private void populateValAlreadyExistSubVarAttr(final PidcSubVarPasteOutput pidcSubVarOutputData,
      final StringBuilder attrNames) {
    attrNames.append("The following Attribute's values are not copied as value already exists for the sub-variant \"" +
        pidcSubVarOutputData.getPastedSubVariant().getName() + "\" : \n");
    for (PidcSubVariantAttribute varAttr : pidcSubVarOutputData.getValAlreadyExists()) {
      attrNames.append(varAttr.getName());
      attrNames.append("\n");
    }
  }


}
