/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.editors.pages.PIDCCocWpPage;
import com.bosch.caltool.apic.ui.table.filters.PIDCCocWpToolBarFilters;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.actions.FilterAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author RDP2COB
 */
public class PIDCCocWpToolbarActionSet {

  private final CustomFilterGridLayer<IProjectCoCWP> cocFilterGridLayer;
  private final PIDCCocWpPage cocWpPage;
  private Action newCoCWpAction;
  private Action usedNotDefinedAction;
  private Action usedAction;
  private Action notUsedAction;
  private Action variantLevelAction;
  private Action pidcLevelAction;
  private Action allCocWPAction;


  /**
   * @param cocFilterGridLayer cocFilterGridLayer
   * @param cocWpPage page
   */
  public PIDCCocWpToolbarActionSet(final CustomFilterGridLayer<IProjectCoCWP> cocFilterGridLayer, final PIDCCocWpPage cocWpPage) {
    this.cocFilterGridLayer = cocFilterGridLayer;
    this.cocWpPage = cocWpPage;
  }

  /**
   * Show all attributes including invisible attributes
   *
   * @param toolBarManager toolbarmgr
   * @param toolBarFilters filters
   */
  public void showAllCocWpAction(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {
    this.allCocWPAction = new FilterAction(CommonUIConstants.FILTER_ALL_COC_WP,
        ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16), false) {

      @Override
      public void run() {
        toolBarFilters.setAllCocWp(PIDCCocWpToolbarActionSet.this.allCocWPAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.allCocWPAction.getText(),
            PIDCCocWpToolbarActionSet.this.allCocWPAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));
        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };

    // Set the image for all attributes filter action
    toolBarManager.add(this.allCocWPAction);

    if (!this.allCocWPAction.isChecked()) {
      this.allCocWPAction.run();
    }

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.allCocWPAction, this.allCocWPAction.isChecked());
  }


  /**
   * @param toolBarManager - tool bar manager
   * @param toolBarFilters - tool bar filter
   */
  public void newCocWpFilter(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {

    this.newCoCWpAction = new Action(CommonUIConstants.FILTER_NEW_COC_WP, SWT.TOGGLE) {

      @Override
      public void run() {
        // set the flag value
        toolBarFilters.setNewCocWp(PIDCCocWpToolbarActionSet.this.newCoCWpAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.newCoCWpAction.getText(),
            PIDCCocWpToolbarActionSet.this.newCoCWpAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));

        // refresh the nattable
        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };

    this.newCoCWpAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NEW_VERSION_16X16));
    this.newCoCWpAction.setChecked(true);
    this.newCoCWpAction.setEnabled(true);

    toolBarManager.add(this.newCoCWpAction);

    this.cocWpPage.addToToolBarFilterMap(this.newCoCWpAction, this.newCoCWpAction.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void usedNotDefinedFilterAction(final ToolBarManager toolBarManager,
      final PIDCCocWpToolBarFilters toolBarFilters) {
    // Create used not known filter action
    this.usedNotDefinedAction = new Action(Messages.getString(IMessageConstants.UNKNOWN_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWpUsedNotDefined(PIDCCocWpToolbarActionSet.this.usedNotDefinedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.usedNotDefinedAction.getText(),
            PIDCCocWpToolbarActionSet.this.usedNotDefinedAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));
        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };
    // Set the image filter action
    this.usedNotDefinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FILTER_QUESTION_16X16));

    this.usedNotDefinedAction.setChecked(true);
    toolBarManager.add(this.usedNotDefinedAction);

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.usedNotDefinedAction, this.usedNotDefinedAction.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void usedFilterAction(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {
    // Create used filter action
    this.usedAction = new Action(Messages.getString(IMessageConstants.USED_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setUsed(PIDCCocWpToolbarActionSet.this.usedAction.isChecked());


        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.usedAction.getText(), PIDCCocWpToolbarActionSet.this.usedAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));
        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };
    // Set the image for PIDC attribute used not known filter action
    this.usedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.USED_16X16));

    this.usedAction.setChecked(true);
    toolBarManager.add(this.usedAction);

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.usedAction, this.usedAction.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */

  public void notUsedFilterAction(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {
    // Create not used filter action
    this.notUsedAction = new Action(Messages.getString(IMessageConstants.NOTUSED_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNotUsed(PIDCCocWpToolbarActionSet.this.notUsedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.notUsedAction.getText(),
            PIDCCocWpToolbarActionSet.this.notUsedAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));

        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };

    // Set the image for PIDC attribute used not known filter action
    this.notUsedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_USED_16X16));
    this.notUsedAction.setChecked(true);
    toolBarManager.add(this.notUsedAction);

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.notUsedAction, this.notUsedAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */

  public void variantFilterAction(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {
    this.variantLevelAction = new Action(CommonUIConstants.FILTER_VARIANT_COCWP, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setVariantLevel(PIDCCocWpToolbarActionSet.this.variantLevelAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.variantLevelAction.getText(),
            PIDCCocWpToolbarActionSet.this.variantLevelAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));
        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };

    // Set the image for variant filter action
    this.variantLevelAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_VARIANTS_28X30));
    this.variantLevelAction.setChecked(true);
    toolBarManager.add(this.variantLevelAction);

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.variantLevelAction, this.variantLevelAction.isChecked());
  }

  /**
   * This method creates non variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcLevelFilterAction(final ToolBarManager toolBarManager, final PIDCCocWpToolBarFilters toolBarFilters) {
    this.pidcLevelAction = new Action(CommonUIConstants.FILTER_NON_VARIANT_COCWP, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setPidcLevel(PIDCCocWpToolbarActionSet.this.pidcLevelAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCCocWpToolbarActionSet.this.cocWpPage.getToolBarFilterStateMap().put(
            PIDCCocWpToolbarActionSet.this.pidcLevelAction.getText(),
            PIDCCocWpToolbarActionSet.this.pidcLevelAction.isChecked());

        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(PIDCCocWpToolbarActionSet.this.cocFilterGridLayer.getSortableColumnHeaderLayer()));

        PIDCCocWpToolbarActionSet.this.cocWpPage.getNatTable().refresh();
      }
    };
    // Set the image for variant filter action
    this.pidcLevelAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_VARIANT_28X30));
    this.pidcLevelAction.setChecked(true);
    toolBarManager.add(this.pidcLevelAction);

    // Adding the default state to filters map
    this.cocWpPage.addToToolBarFilterMap(this.pidcLevelAction, this.pidcLevelAction.isChecked());
  }


}
