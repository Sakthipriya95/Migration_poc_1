/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.function.IntPredicate;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.VariantActionSet;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.VariantUIConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author mga1cob
 */
// ICDM-150
public class VariantPasteDialog extends PIDCAttrValueEditDialog {

  /**
   * Defines source of PIDCVariant
   */
  private final PidcVariant copiedObject;

  /**
   * variant action instance
   */
  private final VariantActionSet varActionSet = new VariantActionSet();

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param pidcVersionBO instance
   * @param copiedObject defines the copied source of ApicObject
   * @param viewer instance
   * @param destObject PIDCard
   */
  public VariantPasteDialog(final Shell parentShell, final PidcVersionBO pidcVersionBO,
      final IProjectObject copiedObject, final ColumnViewer viewer, final PidcVersion destObject) {
    super(parentShell, pidcVersionBO, copiedObject, (TreeViewer) viewer, destObject);
    this.copiedObject = (PidcVariant) copiedObject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle(ApicUiConstants.PASTE_VARIANT);
    // Set the message
    setMessage(ApicUiConstants.PASTE_VARIANT + " - " + this.copiedObject.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getSectionName() {
    return ApicUiConstants.VARIANT_NAMES;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(ApicUiConstants.PASTE_VARIANT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    final com.bosch.caltool.icdm.model.apic.attr.AttributeValue selAttrVal = getSelAttrValFromTabViewer();
    // Selected attrval is already available but deleted, then get confirmation from user
    if (userConfmForDeletedVariantCopy(selAttrVal)) {
      // ICDM-222 progress for pasting PIDC,variant,Sub Variant
      try {

        ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
        dialog.run(true, true, monitor -> {
          monitor.beginTask("Copying Variant...", 100);
          monitor.worked(20);
          addPasteVariantCommand(VariantPasteDialog.this.copiedObject, selAttrVal);
          monitor.worked(100);
          monitor.done();
        });
      }
      catch (InvocationTargetException | InterruptedException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  /**
   * @param selAttrVal selected attribute value
   * @return true, if user want to proceed with copy
   */
  public boolean userConfmForDeletedVariantCopy(
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue selAttrVal) {

    // get all variants of slected pid card
    final SortedSet<PidcVariant> variantsSet = this.pidcVersionBO.getVariantsSet(true);
    for (PidcVariant pidcVariant : variantsSet) {
      if (pidcVariant.getNameValueId().equals(selAttrVal.getId()) && pidcVariant.isDeleted()) {
        IntPredicate isToProceedWithCopy = returnVal -> returnVal == 0;
        MessageDialog confirmationMessageDialog = new MessageDialog(Display.getCurrent().getActiveShell(),
            "Warning : Deleted Variant", null,
            "The variant name which you have selected is already available but in deleted state. Do you like to undo delete and proceed with copy?",
            MessageDialog.CONFIRM, new String[] { "Yes", "No" }, 1);
        return isToProceedWithCopy.test(confirmationMessageDialog.open());

      }
    }
    return true;
  }


  /**
   * @param apicDataProvider
   * @param copiedVariant
   * @param selAttrVal
   */
  private void addPasteVariantCommand(final PidcVariant copiedVariant, final AttributeValue selAttrVal) {
    // iCDM-1155
    // get all variants of slected pid card
    final SortedSet<PidcVariant> variantsSet = this.pidcVersionBO.getVariantsSet(true);

    // check if varaint is used
    Display.getDefault().asyncExec(() -> {

      if (isCopyToNewVariant(selAttrVal, variantsSet, copiedVariant)) {
        // copy to new variant
        int userNewVarCopySelection = VariantPasteDialog.this.varActionSet
            .userCnfmToPasteSubVarAlongWithVar(copiedVariant, VariantPasteDialog.this.pidcVersionBO);
        if (userNewVarCopySelection == VariantUIConstants.COPY_SEL_NO) {
          // copy variant without sub variant
          VariantPasteDialog.this.varActionSet.copyToNewVariant(copiedVariant, selAttrVal,
              VariantPasteDialog.this.pidcVersionBO);
        }
        else if (userNewVarCopySelection == VariantUIConstants.COPY_SEL_YES) {
          // copy variant along with sub variant
          VariantPasteDialog.this.varActionSet.copyToNewVariantAlongWithSubVariant(copiedVariant, selAttrVal,
              VariantPasteDialog.this.pidcVersionBO);
        }
      }
    });
  }


  /**
   * @param copiedVariant copied variant
   * @param selectedAttrVal selected attribute value
   * @param pidcVariantsSet src variants
   * @return true, if copy new variant else false
   */
  public boolean isCopyToNewVariant(final AttributeValue selectedAttrVal, final SortedSet<PidcVariant> pidcVariantsSet,
      final PidcVariant copiedVariant) {
    boolean copyNewVariant = true;
//    traversing through pidc variants
    for (PidcVariant pidcVariant : pidcVariantsSet) {
      if (pidcVariant.getNameValueId().equals(selectedAttrVal.getId())) {
        copyNewVariant = false;

        if (pidcVariant.isDeleted()) {
          copyToDeletedVariant(copiedVariant, pidcVariant);
          break;
        }
        // report error, if variant is already used.
        CDMLogger.getInstance().errorDialog(
            "Selected variant is already used in this Project ID card. Kindly select un-used variants",
            Activator.PLUGIN_ID);
      }
    }
    return copyNewVariant;
  }

  /**
   * @param copiedVariant
   * @param pidcVariant
   */
  private void copyToDeletedVariant(final PidcVariant copiedVariant, final PidcVariant pidcVariant) {
    int userCopySelection = VariantPasteDialog.this.varActionSet.userCnfmToPasteSubVarAlongWithVar(copiedVariant,
        VariantPasteDialog.this.pidcVersionBO);
    SortedSet<PidcSubVariant> sourceSubVarSet =
        this.pidcVersionBO.getSubVariantsforSelVariant(copiedVariant.getId(), false);

    if (isNotToCancelCopy().test(userCopySelection)) {
      int userConfmToOverrideVarNSubVarAttr =
          this.varActionSet.userCnfmToOverrideAllVarNSubVarAttrVal(userCopySelection);

      if (isNotToCancelCopy().test(userConfmToOverrideVarNSubVarAttr)) {
        int userCnfmCopyToDelSubVar = VariantPasteDialog.this.varActionSet.userCnfmCopyToDeletedSubVar(copiedVariant,
            pidcVariant, this.pidcVersionBO, sourceSubVarSet, userCopySelection);

        // copy to deleted variant
        PidcVarPasteOutput pidcVarOutputData = (userCopySelection == VariantUIConstants.COPY_SEL_NO)
            ? this.varActionSet.undoDeleteNUpdateVarAction(copiedVariant, pidcVariant, this.pidcVer)
            : this.varActionSet.updateVarAlongWithSubVar(copiedVariant, pidcVariant, this.pidcVer, sourceSubVarSet,
                userCnfmCopyToDelSubVar, true, userConfmToOverrideVarNSubVarAttr);

        // inform user about value not copied for already available attribute's value
        if (null != pidcVarOutputData) {
          this.varActionSet.attributeValAlreadyExistInfoDialog(pidcVarOutputData, userConfmToOverrideVarNSubVarAttr);
        }
      }
    }
  }

  /**
   * @return
   */
  private IntPredicate isNotToCancelCopy() {
    return userSelection -> ((userSelection != VariantUIConstants.COPY_SEL_CANCEL) &&
        (userSelection != VariantUIConstants.MSG_DIALOG_CLOSE_BUTTON));
  }
}