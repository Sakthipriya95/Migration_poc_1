/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.PIDCSearchPage;
import com.bosch.caltool.apic.ui.views.PIDCSearchPageCreator;
import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class PIDCSearchEditor extends AbstractFormEditor {

  /**
   * pidc scout
   */
  private static final String PIDC_SCOUT = "PIDC Scout";
  /**
   * PIDC search Editor id
   */
  public static final String SEARCH_EDITOR_ID = "com.bosch.caltool.apic.ui.editors.PIDCSearchEditor";

  private PIDCSearchPage searchPage;


  /**
   * @return the searchPage
   */
  public PIDCSearchPage getSearchPage() {
    return this.searchPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {

    try {
      this.searchPage = new PIDCSearchPage(this, PIDC_SCOUT, PIDC_SCOUT);
      addPage(this.searchPage);
    }
    catch (PartInitException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(null);
    getEditorInput().setOutlineDataHandler(dataHandler);
    return new PIDCSearchPageCreator(dataHandler);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getPartName() {
    return PIDC_SCOUT;
  }

  // ICDM-1213
  /**
   * Set the excel export button disabled when pidc search editor is focussed {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(false);
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
    return false;
  }

  /**
   * @return data handler
   */
  @Override
  public PidcSearchEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCSearchEditorInput getEditorInput() {
    return (PIDCSearchEditorInput) super.getEditorInput();
  }

}