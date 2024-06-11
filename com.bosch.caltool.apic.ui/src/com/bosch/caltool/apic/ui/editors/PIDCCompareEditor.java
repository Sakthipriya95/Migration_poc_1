/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.compare.ComparePIDCPage;
import com.bosch.caltool.apic.ui.views.ComparePIDCOutlinePageCreator;
import com.bosch.caltool.apic.ui.views.PidcCompDetailPageCreator;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IEditorWithStructure;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author bru2cob
 */
public class PIDCCompareEditor extends AbstractFormEditor implements IEditorWithStructure {

  /**
   * Defines PIDCCompareEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.apic.ui.editors.PIDCCompareEditor";
  /**
   * List of compare objects
   */
  private List<IProjectObject> compareObjs;
  /**
   * Compare pidc page instance
   */
  private ComparePIDCPage comparePIDCPage;
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
      this.comparePIDCPage = new ComparePIDCPage(this, getEditorInput().getCompareObjs());
      addPage(this.comparePIDCPage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
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
    if (input instanceof PIDCCompareEditorInput) {
      PIDCCompareEditorInput editorInput = (PIDCCompareEditorInput) input;
      this.compareObjs = editorInput.getCompareObjs();

      // name for PIDC variant compare editor
      if (this.compareObjs.get(0) instanceof PidcVariant) {
        setPartName("Project Variant Compare");
      }
      // name for PIDC sub-variant compare editor
      else if (this.compareObjs.get(0) instanceof PidcSubVariant) {
        setPartName("Project Sub-Variant Compare");
      }
      // name for PIDC version compare
      else {
        setPartName("Project Compare");
      }

      super.init(site, input);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(getEditorInput().getComparePidcHandler()
        .getCompareObjectsHandlerMap().get(getEditorInput().getCompareObjs().get(0).getId()).getPidcDataHandler()
        .getPidcVersionInfo().getPidcVersion());
    getEditorInput().setOutlineViewDataHandler(dataHandler);
    // set compare pidc outline page
    return new ComparePIDCOutlinePageCreator(getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCCompareEditorInput getEditorInput() {
    return (PIDCCompareEditorInput) super.getEditorInput();
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
   * @return the comparePIDCPage
   */
  public ComparePIDCPage getComparePIDCPage() {
    return this.comparePIDCPage;
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
    // if both the count are same , set error msg to null
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
    this.comparePIDCPage.setStatusBarMessage(false);
    // disable the excel icon when focus is set for compare editor
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
    super.setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStructurePageCreator getStrucurePageCreator(final PIDCDetailsViewPart viewPart) {

    return new PidcCompDetailPageCreator(this, viewPart);
  }
}
