/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPNatFormPage;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPVersionsPage;

/**
 * The Class FC2WPEditor.
 *
 * @author gge6cob
 */
public class FC2WPEditor extends AbstractFormEditor {

  /**
   * Unique ID for this editor class.
   */
  public static final String PART_ID = "com.bosch.caltool.icdm.ui.editors.FC2WPEditor";

  /** The fc 2 wp nat form page. */
  private FC2WPNatFormPage fc2wpNatFormPage;

  /** The selection provider mediator. */
  private SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();

  /**
   * Gets the FC 2 WP nat form page.
   *
   * @return the prmNatFormPage
   */
  public FC2WPNatFormPage getFC2WPNatFormPage() {
    return this.fc2wpNatFormPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FC2WPEditorInput getEditorInput() {
    return (FC2WPEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    this.fc2wpNatFormPage = new FC2WPNatFormPage(this, getEditorInput());
    NodeAccessRightsPage nodeAccessRightsPage = new NodeAccessRightsPage(this, getEditorInput().getNodeAccessBO());
    FC2WPVersionsPage fc2wpVersionsPage = new FC2WPVersionsPage(this, getEditorInput());
    // Add pages to the editor form
    try {
      addPage(this.fc2wpNatFormPage);
      addPage(fc2wpVersionsPage);
      addPage(nodeAccessRightsPage);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * Creates the page.
   *
   * @param newPageIndex the new page index
   */
  public void createPage(final int newPageIndex) {
    pageChange(newPageIndex);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor iprogressmonitor) {
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
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    setPartName(input.getName());
    super.init(site, input);
  }

  /**
   * Gets the selection provider mediator.
   *
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }


  /**
   * Sets the selection provider mediator.
   *
   * @param selectionProviderMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selectionProviderMediator) {
    this.selectionProviderMediator = selectionProviderMediator;
  }

  /**
   * Updates status bar.
   *
   * @param totalItemCount row count total
   * @param filteredItemCount filtered count
   */
  public void updateStatusBar(final int totalItemCount, final int filteredItemCount) {
    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(!this.fc2wpNatFormPage.isCompareEditor());
    // 391709
    super.setFocus();
  }
}
