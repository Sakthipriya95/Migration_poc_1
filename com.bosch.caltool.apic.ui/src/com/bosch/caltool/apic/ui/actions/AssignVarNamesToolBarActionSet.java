/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.AssignVarNameToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * @author dmo5cob
 */

public class AssignVarNamesToolBarActionSet {


  private Action notInFileFilterAction;
  private Action inFileFilterAction;


  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   */
  public void createNotInFileFilterAction(final ToolBarManager toolBarManager,
      final AssignVarNameToolBarFilters toolBarFilters, final GridTableViewer viewer) {


    this.notInFileFilterAction = new Action("Not in curr. file", SWT.TOGGLE) {

      @Override
      public void run() {
        controlNotInFileFilterAction(toolBarFilters, viewer);
      }
    };
    // Set the image for non defined filter action
    this.notInFileFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_LAB_16X16));

    this.notInFileFilterAction.setChecked(true);
    toolBarManager.add(this.notInFileFilterAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   */
  public void createInFileFilterAction(final ToolBarManager toolBarManager,
      final AssignVarNameToolBarFilters toolBarFilters, final GridTableViewer viewer) {


    this.inFileFilterAction = new Action("In curr. file", SWT.TOGGLE) {

      @Override
      public void run() {
        controlInFileFilterAction(toolBarFilters, viewer);
      }
    };


    // Set the image for non defined filter action
    this.inFileFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FILE_16X16));
    this.inFileFilterAction.setChecked(true);
    toolBarManager.add(this.inFileFilterAction);

  }

  /**
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  protected void controlInFileFilterAction(final AssignVarNameToolBarFilters toolBarFilters,
      final GridTableViewer viewer) {
    controlInFileSelInfo(toolBarFilters, AssignVarNamesToolBarActionSet.this.inFileFilterAction);
    viewer.refresh();

  }

  /**
   * @param toolBarFilters
   * @param action
   */
  private boolean controlInFileSelInfo(final AssignVarNameToolBarFilters toolBarFilters, final Action action) {
    toolBarFilters.setInFileSel(action.isChecked());
    return action.isChecked();

  }


  /**
   * @param toolBarFilters toolBarFilters
   * @param viewer Pidc table viewer
   */
  protected void controlNotInFileFilterAction(final AssignVarNameToolBarFilters toolBarFilters,
      final GridTableViewer viewer) {
    controlNotInFileSelInfo(toolBarFilters, AssignVarNamesToolBarActionSet.this.notInFileFilterAction);
    viewer.refresh();

  }

  /**
   * @param toolBarFilters
   * @param action
   */
  private boolean controlNotInFileSelInfo(final AssignVarNameToolBarFilters toolBarFilters, final Action action) {
    toolBarFilters.setNotInFileSel(action.isChecked());
    return action.isChecked();

  }


}
