/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.AttributesPageCreator;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * This class is for the Attributes Editor
 *
 * @author adn1cob
 */
public class AttributesEditor extends AbstractFormEditor {

  /**
   * Attributes Editor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.apic.ui.editors.AttributesEditor";

  /**
   * CommandState instance
   */
  private CommandState expReportService = new CommandState();

  private final SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();
  /**
   * instance of attribute page
   */
  private AttributesPage attrPage;

  private AttributesEditorInput editorInput;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    // Add pages
    try {
      this.attrPage = new AttributesPage(this, "Attributes", "Attributes");
      addPage(this.attrPage);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof AttributesEditorInput) {
      // get the editor input
      this.editorInput = (AttributesEditorInput) input;
      super.init(site, input);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(null);
    this.editorInput.setOutlineDataHandler(dataHandler);
    return new AttributesPageCreator(dataHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    // ICDM-796
    // ICDM-865
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
    // iCDM-530
    IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW);
    if ((null != viewPartObj) && (viewPartObj instanceof OutlineViewPart)) {
      ((OutlineViewPart) viewPartObj).setTitleTooltip("Filter attributes on selection");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
    // No action

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    if (null != this.expReportService) {
      this.expReportService.setExportService(false);
    }
    super.dispose();
  }

  /**
   * @return the attrPage
   */
  public AttributesPage getAttrPage() {
    return this.attrPage;
  }

}
