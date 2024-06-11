/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.apic.ui.table.filters.PIDCA2lFileToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;

/**
 * @author TRL1COB
 */
public class PIDCA2lToolBarActionSet {

  /**
   * A2lFilePage
   */
  private final A2LFilePage a2lFilePage;

  /**
   * Defines the status line manager
   */
  private final IStatusLineManager statusLineManager;

  /**
   * Action for Active A2L Files
   */
  private Action activeA2lFilesAction;

  /**
   * Action for Inactive A2L Files
   */
  private Action inActiveA2lFilesAction;

  /**
   * @param statusLineManager
   * @param a2lFilePage
   */
  public PIDCA2lToolBarActionSet(final A2LFilePage a2lFilePage, final IStatusLineManager statusLineManager) {
    super();
    this.statusLineManager = statusLineManager;
    this.a2lFilePage = a2lFilePage;
  }


  /**
   * Action method for active a2l files filter
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   * @param viewer CustomGridTableViewer
   */
  public void activeA2lFilesFilterAction(final ToolBarManager toolBarManager,
      final PIDCA2lFileToolBarFilters toolBarFilters, final Viewer viewer) {

    // Action to filter Active A2L files
    this.activeA2lFilesAction = new Action("Active A2L Files", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(PIDCA2lToolBarActionSet.this.statusLineManager);
        }
        toolBarFilters.setActiveA2lFile(isChecked());
        viewer.refresh();
      }
    };

    this.activeA2lFilesAction.setChecked(true);
    // Image for Active A2L files action
    this.activeA2lFilesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.A2LFILE_ACTIVE));
    toolBarManager.add(this.activeA2lFilesAction);

    // Adding the default state to filters map
    this.a2lFilePage.addToToolBarFilterMap(this.activeA2lFilesAction, this.activeA2lFilesAction.isChecked());

  }

  /**
   * Action method for inactive a2l files filter
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   * @param viewer CustomGridTableViewer
   */
  public void inActiveA2lFilesFilterAction(final ToolBarManager toolBarManager,
      final PIDCA2lFileToolBarFilters toolBarFilters, final Viewer viewer) {

    // Action to filter Inactive A2L files
    this.inActiveA2lFilesAction = new Action("Inactive A2L Files", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(PIDCA2lToolBarActionSet.this.statusLineManager);
        }
        toolBarFilters.setInActiveA2lFile(isChecked());
        viewer.refresh();
      }
    };

    this.inActiveA2lFilesAction.setChecked(false);
    // Image for Inactive A2L action
    this.inActiveA2lFilesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.A2LFILE_16X16));
    toolBarManager.add(this.inActiveA2lFilesAction);

    // Adding the default state to filters map
    this.a2lFilePage.addToToolBarFilterMap(this.inActiveA2lFilesAction, this.inActiveA2lFilesAction.isChecked());

  }


  /**
   * @return the activeA2lFilesAction
   */
  public Action getActiveA2lFilesAction() {
    return this.activeA2lFilesAction;
  }


  /**
   * @return the inActiveA2lFilesAction
   */
  public Action getInActiveA2lFilesAction() {
    return this.inActiveA2lFilesAction;
  }


  /**
   * @param activeA2lFilesAction the activeA2lFilesAction to set
   */
  public void setActiveA2lFilesAction(final Action activeA2lFilesAction) {
    this.activeA2lFilesAction = activeA2lFilesAction;
  }


  /**
   * @param inActiveA2lFilesAction the inActiveA2lFilesAction to set
   */
  public void setInActiveA2lFilesAction(final Action inActiveA2lFilesAction) {
    this.inActiveA2lFilesAction = inActiveA2lFilesAction;
  }


  /**
   * @return the statusLineManager
   */
  public IStatusLineManager getStatusLineManager() {
    return this.statusLineManager;
  }


}
