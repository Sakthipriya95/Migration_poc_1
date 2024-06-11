/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
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
import com.bosch.caltool.apic.ui.actions.SubvariantActionSet;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVarCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author mga1cob
 */
// ICDM-150
public class SubVariantPasteDialog extends PIDCAttrValueEditDialog {

  /**
   * PIDCSubVariant instance
   */
  private final PidcSubVariant copiedObject;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param pidcPage instance
   * @param copiedObject defines ApicObject
   * @param viewer instance
   * @param pidcVersionBO PidcVersionHandler
   */
  public SubVariantPasteDialog(final Shell parentShell, final PIDCAttrPage pidcPage, final IProjectObject copiedObject,
      final ColumnViewer viewer, final PidcVersionBO pidcVersionBO) {
    super(parentShell, pidcVersionBO, copiedObject, (TreeViewer) viewer,
        pidcVersionBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion());
    this.copiedObject = (PidcSubVariant) copiedObject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle(ApicUiConstants.PASTE_SUB_VARIANT);
    // Set the message
    setMessage(ApicUiConstants.PASTE_SUB_VARIANT + " - " + this.copiedObject.getName(), IMessageProvider.INFORMATION);

    return contents;
  }

  @Override
  protected String getSectionName() {
    return ApicUiConstants.SUB_VARIANT_NAMES;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(ApicUiConstants.PASTE_SUB_VARIANT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    final AttributeValue selAttrVal = getSelAttrValFromTabViewer();
    // Selected attrval is already available but deleted, then get confirmation from user
    if (userConfmForDeletedSubVariantCopy(selAttrVal)) {
      // ICDM-222 progress for pasting PIDC,variant,Sub Variant
      try {

        ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
        dialog.run(true, true, (final IProgressMonitor monitor) -> {

          monitor.beginTask("Copying Sub Variant...", 100);
          monitor.worked(20);
          setPasteSubVariantCmd(SubVariantPasteDialog.this.copiedObject, selAttrVal);
          monitor.worked(100);
          monitor.done();
        });
      }
      catch (InvocationTargetException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
      catch (InterruptedException exp) {
        Thread.currentThread().interrupt();
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  /**
   * This method returns true, if the user confirms for undeleting the destination subvariant and copy values for that
   * subvariant
   *
   * @param selAttrVal selected Attribute value
   * @return true, if user want to proceed with copy
   */
  public boolean userConfmForDeletedSubVariantCopy(final AttributeValue selAttrVal) {
    // get all sub variants of selected pid card
    PidcVariantBO varHandler = new PidcVariantBO(getPidcVersionBO().getPidcVersion(),
        SubVariantPasteDialog.this.pidcDataHandler.getVariantMap().get(this.copiedObject.getPidcVariantId()),
        SubVariantPasteDialog.this.pidcDataHandler);
    SortedSet<PidcSubVariant> subVariantSet = varHandler.getSubVariantsSet(true);
    for (PidcSubVariant pidcSubVariant : subVariantSet) {
      if (pidcSubVariant.getNameValueId().equals(selAttrVal.getId()) && pidcSubVariant.isDeleted()) {
        MessageDialog confirmationMessageDialog = new MessageDialog(Display.getCurrent().getActiveShell(),
            "Warning : Deleted Sub-Variant", null,
            "The sub-variant name which you have selected is already available but deleted. Do you like to undo delete and proceed with copy?",
            MessageDialog.CONFIRM, new String[] { "Yes", "No" }, 1);
        if (confirmationMessageDialog.open() == 1) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param selAttrVal
   * @param copiedVariant
   */
  private void setPasteSubVariantCmd(final PidcSubVariant copiedSubVariant, final AttributeValue selAttrVal) {
    // iCDM-1155
    // check if sub-varaint is used
    Display.getDefault().asyncExec(() -> copySubvariant(copiedSubVariant, selAttrVal));
  }

  /**
   * @param copiedSubVariant
   * @param selAttrVal
   */
  private void copySubvariant(final PidcSubVariant copiedSubVariant, final AttributeValue selAttrVal) {
    // get all sub-variants of selected variant
    PidcVariantBO varHandler = new PidcVariantBO(getPidcVersionBO().getPidcVersion(),
        SubVariantPasteDialog.this.pidcDataHandler.getVariantMap().get(copiedSubVariant.getPidcVariantId()),
        SubVariantPasteDialog.this.pidcDataHandler);
    SortedSet<PidcSubVariant> subVariantSet = varHandler.getSubVariantsSet(true);
    // check if varaint is used
    if (!checkSubVariantExists(selAttrVal, copiedSubVariant, subVariantSet)) {
      try {
        copyToNewSubVariant(copiedSubVariant, selAttrVal);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * This method constructs data for Copy sub-variants and calls the service to copy subvariants
   *
   * @param copiedSVar
   * @param selValue
   * @throws ApicWebServiceException
   */
  private void copyToNewSubVariant(final PidcSubVariant copiedSVar, final AttributeValue selValue)
      throws ApicWebServiceException {
    PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
    pidcSubVarData.setSrcPidcSubVar(copiedSVar);
    pidcSubVarData.setSubvarNameAttrValue(selValue);
    pidcSubVarData.setPidcVariantId(copiedSVar.getPidcVariantId());
    pidcSubVarData.setPidcVersionId(copiedSVar.getPidcVersionId());
    PidcSubVarCopyServiceClient pidcSubVarClient = new PidcSubVarCopyServiceClient();
    pidcSubVarClient.create(pidcSubVarData);
  }

  /**
   * Checks if the sub-variant is used in set of all sub-variants
   *
   * @param attributeValue Attribute Value
   * @param copiedSubVariant copied Sub-Variant
   * @param subVariantsSet sub-Variants Set
   * @return true if sub-vaiant is used
   */
  protected boolean checkSubVariantExists(final AttributeValue attributeValue, final PidcSubVariant copiedSubVariant,
      final SortedSet<PidcSubVariant> subVariantsSet) {
    boolean usedSubVariant = false;
    for (PidcSubVariant pidcSubVariant : subVariantsSet) {
      if (pidcSubVariant.getNameValueId().equals(attributeValue.getId())) {
        usedSubVariant = true;
        if (pidcSubVariant.isDeleted()) {
          final SubvariantActionSet actionset = new SubvariantActionSet();
          // un-delete if variant is used and marked as deleted
          PidcSubVarPasteOutput pidcSubVarOutputData =
              actionset.undoDeleteNUpdateSubVarAction(copiedSubVariant, pidcSubVariant);
          // inform user about value not copied for already available
          actionset.attributeNotCopiedInfoDialogForSubVarCopy(pidcSubVarOutputData);
          break;
        }
        // report error, if sub-variant is already used.
        CDMLogger.getInstance().errorDialog(
            "Selected sub-variant is already used in this Variant. Kindly select un-used sub-variants",
            Activator.PLUGIN_ID);
        break;
      }
    }
    return usedSubVariant;
  }
}
