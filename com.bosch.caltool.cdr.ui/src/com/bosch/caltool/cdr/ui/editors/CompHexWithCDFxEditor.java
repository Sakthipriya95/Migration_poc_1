/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.CompHexWithCdfNatPage;
import com.bosch.caltool.cdr.ui.views.CompHexCdfA2LPageCreator;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * icdm-2497 editor to show the result of comparison
 *
 * @author mkl2cob
 */
public class CompHexWithCDFxEditor extends AbstractFormEditor {

  /**
   * Defines PIDCEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditor";
  /**
   * CompHexWithCdfNatPage
   */
  private CompHexWithCdfNatPage compResNATPage;
  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  /**
   * the synchronized table graph viewer
   */
  // ICDM-2498
  private CalDataViewerDialog synchCalDataViewerDialog;

  /**
   * the normal table graph viewer
   */
  // ICDM-2498
  private CalDataViewerDialog unSynchCalDataViewerDialog;

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    setPartName(input.getName());
    super.init(site, input);
    String userNotificationMsg = getEditorInput().getUserNotificationMsg();
    if (CommonUtils.isNotEmptyString(userNotificationMsg)) {
      MessageDialogUtils.getInfoMessageDialog("WP Finished Status Reset Info", userNotificationMsg);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    return new CompHexCdfA2LPageCreator(getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    this.compResNATPage = new CompHexWithCdfNatPage(this, "Compare Hex Results", "Compare Hex Results");
    try {
      addPage(this.compResNATPage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor arg0) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }

  /**
   * Status of the focussed page is set
   */
  // ICDM-343
  public void setStatus() {
    if (getActivePage() == 0) {
      this.compResNATPage.setStatusBarMsgAndStatHdr(false);
    }
  }

  // ICDM-1703
  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();

    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
    setStatus();
  }

  /**
   * @return the synchCalDataViewerDialog
   */
  public CalDataViewerDialog getSynchCalDataViewerDialog() {
    return this.synchCalDataViewerDialog;
  }


  /**
   * @param synchCalDataViewerDialog the synchCalDataViewerDialog to set
   */
  public void setSynchCalDataViewerDialog(final CalDataViewerDialog synchCalDataViewerDialog) {
    this.synchCalDataViewerDialog = synchCalDataViewerDialog;
  }


  /**
   * @return the unSynchCalDataViewerDialog
   */
  public CalDataViewerDialog getUnSynchCalDataViewerDialog() {
    return this.unSynchCalDataViewerDialog;
  }


  /**
   * @param unSynchCalDataViewerDialog the unSynchCalDataViewerDialog to set
   */
  public void setUnSynchCalDataViewerDialog(final CalDataViewerDialog unSynchCalDataViewerDialog) {
    this.unSynchCalDataViewerDialog = unSynchCalDataViewerDialog;
  }

  @Override
  public CompHexWithCDFxEditorInput getEditorInput() {
    return (CompHexWithCDFxEditorInput) super.getEditorInput();
  }
}
