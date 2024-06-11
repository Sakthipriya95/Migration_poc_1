/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author bru2cob
 */
public class A2LCompareEditor extends AbstractFormEditor {


  /**
   * Defines PIDCCompareEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2LCompareEditor";
  /**
   * Compare pidc page instance
   */
  private A2lParamComparePage a2lComparePage;
  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      this.a2lComparePage = new A2lParamComparePage(this, getEditorInput().getParamCompareHandler());
      addPage(this.a2lComparePage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  @Override
  public void setFocus() {
    this.a2lComparePage.setStatusBarMessage(false);
    // enable the excel icon when focus is set for compare editor
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
    super.setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2LCompareEditorInput getEditorInput() {
    return (A2LCompareEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  public void firePropChange(final int propertyId) {
    firePropertyChange(propertyId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
    // TODO Auto-generated method stub

  }


  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof A2LCompareEditorInput) {
      setPartName("A2L Compare");
      super.init(site, input);
    }
  }

  /**
   * Updating the status bar
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  public void updateStatusBar(final boolean outlineSelection, final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part

    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart").getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    // Updation of status based on selection in editor
    else {
      statusLine = getEditorSite().getActionBars().getStatusLineManager();
    }
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
  public void doSaveAs() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // TODO Auto-generated method stub
    return false;
  }


  /**
   * @return the a2lComparePage
   */
  public final A2lParamComparePage getA2lComparePage() {
    return this.a2lComparePage;
  }
}
