/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.editors;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.editors.pages.ComponentDetailsPage;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class ComponentPackageEditor extends AbstractFormEditor {


  /**
   * ID of editor
   */
  public static final String EDITOR_ID = "com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditor";

  /**
   * Instances of various pages in the editor
   */
  private ComponentDetailsPage cmpDetailsPage;
  private NodeAccessRightsPage nodeAccessRightsPage;

  /**
   * CommandState instance
   */
  private CommandState expReportService = new CommandState();

  private SelectionProviderMediator selProvdrMediator = new SelectionProviderMediator();

  /**
   * @param partName part Name
   */
  public void setEditorPartName(final String partName) {
    setPartName(partName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      this.nodeAccessRightsPage = new NodeAccessRightsPage(this, getEditorInput().getNodeAccessBO());
      this.cmpDetailsPage = new ComponentDetailsPage(this);
      addPage(this.cmpDetailsPage);
      addPage(this.nodeAccessRightsPage);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
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
    if (input instanceof ComponentPackageEditorInput) {
      ComponentPackageEditorInput cmpPkgEditorInput = (ComponentPackageEditorInput) input;
      setPartName(cmpPkgEditorInput.getName());
      super.init(site, cmpPkgEditorInput);
    }
  }

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selProvdrMediator;
  }


  /**
   * @param selProvdrMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selProvdrMediator) {
    this.selProvdrMediator = selProvdrMediator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    this.cmpDetailsPage.setStatusBarMessage(this.cmpDetailsPage.getBcTableViewer());
    // ICDM-796
    // ICDM-865
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
  }


  /**
   * @return the cmpDetailsPage
   */
  public ComponentDetailsPage getCmpDetailsPage() {
    return this.cmpDetailsPage;
  }


  /**
   * @return the accessRightsPage
   */
  public NodeAccessRightsPage getAccessRightsPage() {
    return this.nodeAccessRightsPage;
  }

  @Override
  public ComponentPackageEditorInput getEditorInput() {
    return (ComponentPackageEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    setPartName(getEditorInput().getSelectedCmpPkg().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

}
