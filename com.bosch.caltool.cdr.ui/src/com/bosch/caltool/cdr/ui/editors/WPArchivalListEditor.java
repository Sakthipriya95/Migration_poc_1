/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.WPArchivalsListPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * ICDM-784616 Editor to display WP Archivals
 *
 * @author ukt1cob
 */
public class WPArchivalListEditor extends AbstractFormEditor {

  /**
   * wpArchialsListPage page instance
   */
  private WPArchivalsListPage wpArchialsListPage;
  /**
   * Defines PIDCEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.WPArchivalListEditor";

  /**
   * Constant defining the outline view part id
   */
  public static final String OUTLINE_TREE_VIEW = "com.bosch.caltool.icdm.common.ui.views.OutlineViewPart";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      // wpArchialsListPage instance is added to the pages of editor
      this.wpArchialsListPage = new WPArchivalsListPage(this, "", "Workpackage Archivals");
      addPage(this.wpArchialsListPage);
      setPartName("Workpackage Archivals : " + ((WPArchivalListEditorInput) getEditorInput()).getsEffectiveName());
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
   * @param outlineSelection boolean
   * @param totTableRowCount int
   * @param preferredRowCount int
   */
  public void updateStatusBar(final boolean outlineSelection, final int totTableRowCount, final int preferredRowCount) {
    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(preferredRowCount).append(" out of ").append(totTableRowCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(OUTLINE_TREE_VIEW).getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    // Updation of status based on selection in editor
    else {
      statusLine = getEditorSite().getActionBars().getStatusLineManager();
    }
    if (totTableRowCount == preferredRowCount) {
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
    super.setFocus();
    if (getActivePage() == 0) {
      // when the focus is obtained by the editor , the status bar is updated
      this.wpArchialsListPage.setStatusBarMessage(this.wpArchialsListPage.getGroupByHeaderLayer(), false);
    }
  }

}
