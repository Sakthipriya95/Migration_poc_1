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
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentBaselinesPage;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * Editor to show the list data assessment baselines
 *
 * @author msp5cob
 */
public class DataAssessmentBaselineEditor extends AbstractFormEditor {

  /**
   * Defines DataAssessmentEditor Id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.DataAssessmentBaselineEditor";


  private DataAssessmentBaselinesPage baselinesPage;


  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    // Contructing custom title for this Editor
    // The Editor Input is common to 2 Editors, hence not using IEditorInput - getName method
    String partName =
        "Data Assessment Baselines for A2L file - " + ((DataAssessmentReportEditorInput) input).getA2lFileName();
    setPartName(partName);
    super.init(site, input);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    this.baselinesPage = new DataAssessmentBaselinesPage(this);
    try {
      addPage(this.baselinesPage);
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
   * {@inheritDoc}
   */
  @Override
  public void setActivePage(final int pageIndex) {
    super.setActivePage(pageIndex);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    setStatus();
  }

  /**
   * Set status according to the page
   */
  public void setStatus() {
    this.baselinesPage.updateStatusBar(false);
  }

  @Override
  public DataAssessmentReportEditorInput getEditorInput() {
    return (DataAssessmentReportEditorInput) super.getEditorInput();
  }


  @Override
  public IClientDataHandler getDataHandler() {
    return getEditorInput().getDataAssmntReportDataHandler();
  }
}
