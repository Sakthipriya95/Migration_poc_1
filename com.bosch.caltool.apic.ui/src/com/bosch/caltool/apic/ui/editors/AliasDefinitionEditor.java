/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.AliasDetailsPage;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.apic.ui.views.PIDCSearchPageCreator;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author rgo7cob
 */
public class AliasDefinitionEditor extends AbstractFormEditor {

  /**
   * pidc scout
   */
  private static final String ALIAS_DETAILS = "Alias Details";
  /**
   * editor input
   */
  private AliasDefEditorInput input;
  // Instance of search page
  private AliasDetailsPage searchPage;

  private SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }


  /**
   * @param selectionProviderMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selectionProviderMediator) {
    this.selectionProviderMediator = selectionProviderMediator;
  }


  /**
   * @return the searchPage
   */
  public AliasDetailsPage getSearchPage() {
    return this.searchPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    // add pages
    try {
      this.searchPage = new AliasDetailsPage(this, ALIAS_DETAILS, ALIAS_DETAILS);
      NodeAccessRightsPage accessPage = new NodeAccessRightsPage(this, this.input.getNodeAccess());
      addPage(this.searchPage);
      addPage(accessPage);
    }
    catch (PartInitException exception) {
      CDMLogger.getInstance().error(exception.getMessage(), exception);
    }

  }

  @Override
  public void init(final IEditorSite site, final IEditorInput inpt) throws PartInitException {
    if (inpt instanceof AliasDefEditorInput) {
      // get the editor input
      this.input = (AliasDefEditorInput) inpt;
      super.init(site, inpt);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(null);
    this.input.setOutlineDataHandler(dataHandler);
    return new PIDCSearchPageCreator(dataHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPartName() {
    // set alias name
    return this.input.getDataHandler().getAliasDef().getName();
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
    // Not applicable
    return false;
  }
}