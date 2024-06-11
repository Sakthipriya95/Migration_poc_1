/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.PIDCAttrValueEditToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * @author dmo5cob
 */

public class PIDCAttrValueEditToolBarActionSet {


  /**
   * @param toolBarManager ToolBarManager
   * @param valTableViewer GridTableViewer
   * @param filters PIDCAttrValueEditToolBarFilters
   */
  public void createDepnFilterAction(final ToolBarManager toolBarManager, final PIDCAttrValueEditToolBarFilters filters,
      final GridTableViewer valTableViewer) {

    final Action depFilterAction = new Action("Visible Values", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setDepn(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    depFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DEPN_ATTR_28X30));
    depFilterAction.setChecked(true);
    toolBarManager.add(depFilterAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public void createNotDepnFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action depFilterAction = new Action("Invisible values due to dependency", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setNotDepn(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    depFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_NONDEP_16X16));
    depFilterAction.setChecked(false);
    toolBarManager.add(depFilterAction);

  }


  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public void createNotDeletedFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action notDelFilterAction = new Action("Not Deleted", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setNotDeleted(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    notDelFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_16X16));
    notDelFilterAction.setChecked(true);
    toolBarManager.add(notDelFilterAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public void createDeletedFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action delFilterAction = new Action("Deleted", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setDeleted(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    delFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    delFilterAction.setChecked(false);
    toolBarManager.add(delFilterAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public void createClearFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action clearFilterAction = new Action(" Cleared", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setClearSel(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    clearFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_16X16));
    clearFilterAction.setChecked(true);
    toolBarManager.add(clearFilterAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public Action createNotClearFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action notClearFilterAction = new Action("Not Cleared", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setNotClearSel(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    notClearFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_FILTER_16X16));
    notClearFilterAction.setChecked(false);
    toolBarManager.add(notClearFilterAction);
    return notClearFilterAction;
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param filters PIDCAttrValueEditToolBarFilters
   * @param valTableViewer GridTableViewer
   */
  public void createRejectedFilterAction(final ToolBarManager toolBarManager,
      final PIDCAttrValueEditToolBarFilters filters, final GridTableViewer valTableViewer) {

    final Action notClearFilterAction = new Action("Rejected", SWT.TOGGLE) {

      @Override
      public void run() {
        filters.setAttrValRejected(isChecked());
        valTableViewer.refresh();
      }
    };
    // Set the image for non defined filter action
    notClearFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REJECTED_VALUE_16X16));
    notClearFilterAction.setChecked(false);
    toolBarManager.add(notClearFilterAction);

  }
}
