/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.WPArchivalsListPage;
import com.bosch.caltool.cdr.ui.table.filters.WPArchivalToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 *
 */
public class WPArchivalsPageToolBarActionSet {

  /**
   * CustomFilterGridLayer instance
   */
  private final CustomFilterGridLayer filterGridLayer;

  private final WPArchivalsListPage page;


  /**
   * @param wpArchivalFilterGridLayer
   * @param wpArchivalsListPage
   */
  public WPArchivalsPageToolBarActionSet(final CustomFilterGridLayer<WpArchival> wpArchivalFilterGridLayer,
      final WPArchivalsListPage wpArchivalsListPage) {
    this.filterGridLayer = wpArchivalFilterGridLayer;
    this.page = wpArchivalsListPage;
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showInProgressAction(final ToolBarManager toolBarManager, final WPArchivalToolBarFilters toolBarFilters) {
    final Action inProgressAction = new Action("In Progress", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setInProgress(isChecked());
        // fire the filter event
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    inProgressAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.YELLOW_MARK_8X8));
    // initially let the toggle action be pressed
    inProgressAction.setChecked(true);
    toolBarManager.add(inProgressAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(inProgressAction, inProgressAction.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showCompletedAction(final ToolBarManager toolBarManager, final WPArchivalToolBarFilters toolBarFilters) {
    final Action inProgressAction = new Action("Completed", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setCompleted(isChecked());

        // fire the filter event
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    inProgressAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.YES_28X30));
    // initially let the toggle action be pressed
    inProgressAction.setChecked(true);
    toolBarManager.add(inProgressAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(inProgressAction, inProgressAction.isChecked());

  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showFailedAction(final ToolBarManager toolBarManager, final WPArchivalToolBarFilters toolBarFilters) {
    final Action inProgressAction = new Action("Failed", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setFailed(isChecked());
        // fire the filter event
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    inProgressAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    // initially let the toggle action be pressed
    inProgressAction.setChecked(true);
    toolBarManager.add(inProgressAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(inProgressAction, inProgressAction.isChecked());

  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showNotAvailableAction(final ToolBarManager toolBarManager,
      final WPArchivalToolBarFilters toolBarFilters) {
    final Action inProgressAction = new Action("Not Available", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNotAvailable(isChecked());
        // fire the filter event
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                WPArchivalsPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    inProgressAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    // initially let the toggle action be pressed
    inProgressAction.setChecked(true);
    toolBarManager.add(inProgressAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(inProgressAction, inProgressAction.isChecked());

  }
}
