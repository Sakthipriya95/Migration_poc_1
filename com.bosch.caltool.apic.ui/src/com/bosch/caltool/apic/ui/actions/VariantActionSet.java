/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.VariantUIConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVarNSubVarCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author ukt1cob
 */
public class VariantActionSet {

  /**
   * Copy variants
   *
   * @param variantToCopy copied variant
   * @param selAttrVal selected attribute Value
   * @param pidcVersionBO PIDC version BO
   */
  public void copyToNewVariant(final PidcVariant variantToCopy, final AttributeValue selAttrVal,
      final PidcVersionBO pidcVersionBO) {

    PidcVariantCopyServiceClient pidcVarClient = new PidcVariantCopyServiceClient();
    PidcVariantData pidcVarCreationData = new PidcVariantData();
    pidcVarCreationData.setSrcPidcVar(variantToCopy);
    pidcVarCreationData.setVarNameAttrValue(selAttrVal);
    pidcVarCreationData.setPidcVersion(pidcVersionBO.getPidcVersion());
    try {
//      copying the selected variant to the specified variant
      pidcVarClient.create(pidcVarCreationData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * Copy variants and sub-variants
   *
   * @param variantToCopy copied variant
   * @param selectedAttrVal selected attribute value
   * @param pidcVersionBO PIDC version BO
   */
  public void copyToNewVariantAlongWithSubVariant(final PidcVariant variantToCopy, final AttributeValue selectedAttrVal,
      final PidcVersionBO pidcVersionBO) {

    SortedSet<PidcSubVariant> sourceSubVarSet = pidcVersionBO.getSubVariantsforSelVariant(variantToCopy.getId(), false);

    PidcVarNSubVarCopyServiceClient pidcVarNSubVarCopyClient = new PidcVarNSubVarCopyServiceClient();
    PidcVariantData pidcVarData = new PidcVariantData();
    pidcVarData.setSrcPidcVar(variantToCopy);
    pidcVarData.setVarNameAttrValue(selectedAttrVal);
    pidcVarData.setPidcVersion(pidcVersionBO.getPidcVersion());
    pidcVarData.setSrcSubVarSet(sourceSubVarSet);
    pidcVarData.setSubVarCopiedAlongWithVar(true);

    try {
//    copying the selected variant to the specified variant along with sub-variant
      pidcVarNSubVarCopyClient.create(pidcVarData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Confirmation for including sub-variants while copying any variant
   *
   * @param copiedVariant copied variant
   * @param pidcVersionBO PIDC version BO
   * @return 0 and 1 if user want to copy variant along with sub-variant
   */
  public int userCnfmToPasteSubVarAlongWithVar(final PidcVariant copiedVariant, final PidcVersionBO pidcVersionBO) {
    final SortedSet<PidcSubVariant> subVariantsSet =
        pidcVersionBO.getSubVariantsforSelVariant(copiedVariant.getId(), false);
//    check for sub-variants
    if (CommonUtils.isNotEmpty(subVariantsSet)) {
      MessageDialog confirmationMessageDialog =
          new MessageDialog(Display.getCurrent().getActiveShell(), VariantUIConstants.COPY_SUB_VARIANT, null,
              "The variant to be copied has sub-variant(s). Do you like to copy the variant along with sub-variant(s)?",
              MessageDialog.CONFIRM, new String[] { "Yes", "No", VariantUIConstants.MSG_DIALOG_OPTION_CANCEL },
              VariantUIConstants.COPY_SEL_CANCEL);
//      user confirmation dialog
      return confirmationMessageDialog.open();
    }
    return VariantUIConstants.COPY_SEL_NO;
  }


  /**
   * This method is for the user confirmation to replace all attribute values from source to destination variant
   *
   * @param userCopySelection option selected by user for copy
   * @return 0 if user want to override the existing attribute values of variant
   */
  public int userCnfmToOverrideAllVarAttrVal(final int userCopySelection) {
    if (userCopySelection == VariantUIConstants.COPY_SEL_NO) {
      MessageDialog confirmationMessageDialog = new MessageDialog(Display.getCurrent().getActiveShell(), "Copy Variant",
          null,
          "Do you like to replace all the existing attribute values of the destination variant with that of source?\n\nNote:\nOverride is not applicable if \n    1. Either source or destination variant's attribute value is <SUB-VARIANT>\n    2. The attribute is 'PVER name in SDOM'",
          MessageDialog.CONFIRM, new String[] { "Yes", "No", VariantUIConstants.MSG_DIALOG_OPTION_CANCEL },
          VariantUIConstants.COPY_SEL_CANCEL);
//    user confirmation dialog
      return confirmationMessageDialog.open();
    }
    return VariantUIConstants.COPY_SEL_NO_OVERRIDE;
  }


  /**
   * This method is for the user confirmation to replace all attribute values in destination variant and destination
   * sub-variant from the source
   *
   * @param userCopySelection option selected by user for copy
   * @return 0 if user want to override the existing attribute values of variant
   */
  public int userCnfmToOverrideAllVarNSubVarAttrVal(final int userCopySelection) {
    if (userCopySelection == VariantUIConstants.COPY_SEL_YES) {
      MessageDialog confirmationMessageDialog = new MessageDialog(Display.getCurrent().getActiveShell(), "Copy Variant",
          null,
          "Do you like to replace all the existing attribute values of the destination variant with that of source?\n\nNote:\nOverride is not applicable if the attribute is 'PVER name in SDOM'",
          MessageDialog.CONFIRM, new String[] { "Yes", "No", VariantUIConstants.MSG_DIALOG_OPTION_CANCEL },
          VariantUIConstants.COPY_SEL_CANCEL);
//    user confirmation dialog
      return confirmationMessageDialog.open();
    }
    return VariantUIConstants.COPY_SEL_NO_OVERRIDE;
  }

  /**
   * Undo delete and update action in variant
   *
   * @param pidcVariantToCopy copied pidc variant
   * @param pidcVariantToUpdate pidc variant to be updated
   * @param pidcVersion PidcVersion
   * @return PidcVarPasteOutput
   */
  public PidcVarPasteOutput undoDeleteNUpdateVarAction(final PidcVariant pidcVariantToCopy,
      final PidcVariant pidcVariantToUpdate, final PidcVersion pidcVersion) {
    int userConfmToOverrideVarAttr = userCnfmToOverrideAllVarAttrVal(VariantUIConstants.COPY_SEL_NO);

    if ((userConfmToOverrideVarAttr != VariantUIConstants.COPY_SEL_CANCEL) &&
        (userConfmToOverrideVarAttr != VariantUIConstants.MSG_DIALOG_CLOSE_BUTTON)) {
      // undo delete by setting flag to false
      pidcVariantToUpdate.setDeleted(false);

      // update the values of variant with values in copied variant
      PidcVariantCopyServiceClient pidcVarCopyServiceClient = new PidcVariantCopyServiceClient();

      PidcVariantData pidcVarData = new PidcVariantData();
      pidcVarData.setSrcPidcVar(pidcVariantToCopy);
      pidcVarData.setDestPidcVar(pidcVariantToUpdate);
      pidcVarData.setPidcVersion(pidcVersion);
      pidcVarData.setUndoDeleteNUpdateVar(true);
      if (userConfmToOverrideVarAttr == VariantUIConstants.COPY_SEL_OVERRIDE) {
        pidcVarData.setOverrideAllVarAttr(true);
      }
      try {
        return pidcVarCopyServiceClient.update(pidcVarData);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return null;
  }


  /**
   * This method updates the values of variant and sub-variant
   *
   * @param variantToCopy copied variant
   * @param variantToUpdate variant to update
   * @param pidcVersion pidc version
   * @param sourceSubVarSet sub variants in source variant
   * @param userCnfmCopyToDelSubVar user input for copy to deleted sub var
   * @param isDeleted true if variant to update is in deleted state
   * @param userConfmToOverrideVarNSubVarAttr user selection whether to override or not
   * @return PidcVarPasteOutput
   */
  public PidcVarPasteOutput updateVarAlongWithSubVar(final PidcVariant variantToCopy, final PidcVariant variantToUpdate,
      final PidcVersion pidcVersion, final SortedSet<PidcSubVariant> sourceSubVarSet, final int userCnfmCopyToDelSubVar,
      final boolean isDeleted, final int userConfmToOverrideVarNSubVarAttr) {
    if (userCnfmCopyToDelSubVar != VariantUIConstants.MSG_DIALOG_CLOSE_BUTTON) {
      // undo delete by setting flag to false
      variantToUpdate.setDeleted(false);

      // update the values of variant with values in copied variant
      PidcVarNSubVarCopyServiceClient pidcVarNSubVarCopyServiceClient = new PidcVarNSubVarCopyServiceClient();

      PidcVariantData pidcVarData = new PidcVariantData();
      pidcVarData.setSrcPidcVar(variantToCopy);
      pidcVarData.setDestPidcVar(variantToUpdate);
      pidcVarData.setPidcVersion(pidcVersion);
      pidcVarData.setSrcSubVarSet(sourceSubVarSet);
      pidcVarData.setSubVarCopiedAlongWithVar(true);

      if (isDeleted) {
        pidcVarData.setUndoDeleteNUpdateVar(true);
      }

      if (userCnfmCopyToDelSubVar == VariantUIConstants.UNDO_DELETED_SUB_VAR_YES) {
        pidcVarData.setUndoDeleteNUpdateSubVar(true);
      }
      if (userConfmToOverrideVarNSubVarAttr == VariantUIConstants.COPY_SEL_OVERRIDE) {
        pidcVarData.setOverrideAll(true);
      }

      try {
        return pidcVarNSubVarCopyServiceClient.update(pidcVarData);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return null;
  }

  /**
   * @param variantToCopy copied variant
   * @param variantToUpdate destination variant
   * @param pidcVersionBO pidc version BO
   * @param sourceSubVarSet sub-variants of source variant
   * @param userCopySelection if 0, copy along with subvar
   * @return 0 if user wants to proceed with copy of deleted sub var
   */
  public int userCnfmCopyToDeletedSubVar(final PidcVariant variantToCopy, final PidcVariant variantToUpdate,
      final PidcVersionBO pidcVersionBO, final SortedSet<PidcSubVariant> sourceSubVarSet, final int userCopySelection) {
    StringBuilder deletedSubVarNames = new StringBuilder();
    final SortedSet<Long> sourcePidcSubVariantIdSet = new TreeSet<>();
    if ((userCopySelection == VariantUIConstants.COPY_SEL_YES)) {
      for (PidcSubVariant sourcePidcSubVariant : sourceSubVarSet) {
        sourcePidcSubVariantIdSet.add(sourcePidcSubVariant.getNameValueId());
      }

      for (PidcSubVariant pidcSubVar : pidcVersionBO.getSubVariantsforSelVariant(variantToUpdate.getId(), true)) {
        if (sourcePidcSubVariantIdSet.contains(pidcSubVar.getNameValueId()) && pidcSubVar.isDeleted()) {
          deletedSubVarNames.append(pidcSubVar.getName());
          deletedSubVarNames.append("\n");
        }
      }

      if (CommonUtils.isNotEmptyString(deletedSubVarNames.toString())) {
        MessageDialog confirmationMessageDialog =
            new MessageDialog(Display.getCurrent().getActiveShell(), VariantUIConstants.COPY_SUB_VARIANT, null,
                "The following sub-variant(s) that are available in Variant \"" + variantToCopy.getName() +
                    "\" are in deleted state in variant \"" + variantToUpdate.getName() + "\"\n" + deletedSubVarNames +
                    "\nDo you like to undo delete and proceed with copy?\n",
                MessageDialog.CONFIRM, new String[] { "Yes", "No" }, VariantUIConstants.UNDO_DELETED_SUB_VAR_NO);
        return confirmationMessageDialog.open();
      }
    }
    return VariantUIConstants.UNDO_DELETED_SUB_VAR_NO;
  }

  /**
   * @param pidcVarOutputData updated pidc variant data
   * @param userConfmToOverrideVarAttr user confirmation to override variant attribute values
   */
  public void attributeValAlreadyExistInfoDialog(final PidcVarPasteOutput pidcVarOutputData,
      final int userConfmToOverrideVarAttr) {
    StringBuilder attrNames = new StringBuilder();
    // append variant's attribute whose value is already available in destination variant
    if ((pidcVarOutputData.getValAlreadyExists() != null) && !pidcVarOutputData.getValAlreadyExists().isEmpty()) {
      populateValAlreadyExistVarAttributes(pidcVarOutputData, attrNames, userConfmToOverrideVarAttr);
    }
    // append sub-variant's attribute whose value is already available in destination sub-variant
    if ((pidcVarOutputData.getValAlreadyExistsForSubVarMap() != null) &&
        !pidcVarOutputData.getValAlreadyExistsForSubVarMap().isEmpty()) {
      populateValAlreadyExistSubVarAttributes(pidcVarOutputData, attrNames);
    }
    if (CommonUtils.isNotEmptyString(attrNames.toString())) {
      CDMLogger.getInstance().infoDialog(attrNames.toString(), Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcVarOutputData
   * @param attrNames
   * @param userConfmToOverrideVarAttr
   */
  private void populateValAlreadyExistVarAttributes(final PidcVarPasteOutput pidcVarOutputData,
      final StringBuilder attrNames, final int userConfmToOverrideVarAttr) {
//    when destination variant already has the copied attributes with values
    if (userConfmToOverrideVarAttr != VariantUIConstants.COPY_SEL_OVERRIDE) {
      attrNames.append("The following Attribute's values are not copied as value already exists for the variant \"" +
          pidcVarOutputData.getPastedVariant().getName() + "\" : \n");
      for (PidcVariantAttribute varAttr : pidcVarOutputData.getValAlreadyExists()) {
        attrNames.append(varAttr.getName());
        attrNames.append("\n");
      }
    }
  }

  /**
   * @param pidcVarOutputData
   * @param attrNames attr
   */
  private void populateValAlreadyExistSubVarAttributes(final PidcVarPasteOutput pidcVarOutputData,
      final StringBuilder attrNames) {
    for (Map.Entry<String, List<PidcSubVariantAttribute>> valAlreadyExistForSubVarAttr : pidcVarOutputData
        .getValAlreadyExistsForSubVarMap().entrySet()) {
      if (CommonUtils.isNotEmpty(valAlreadyExistForSubVarAttr.getValue())) {
        attrNames.append("\n");
        attrNames
            .append("The following Attribute's values are not copied as value already exist for the sub-variant \"" +
                valAlreadyExistForSubVarAttr.getKey() + "\" :\n");
        for (PidcSubVariantAttribute subVarAttributeList : valAlreadyExistForSubVarAttr.getValue()) {
          attrNames.append(subVarAttributeList.getName());
          attrNames.append("\n");
        }
      }
    }
  }


}
