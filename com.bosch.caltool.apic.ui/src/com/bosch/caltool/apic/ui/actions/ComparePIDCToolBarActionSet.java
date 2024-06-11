/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.editors.compare.ComparePIDCPage;
import com.bosch.caltool.apic.ui.table.filters.ComparePIDCToolBarFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author bru2cob
 */
public class ComparePIDCToolBarActionSet {


  private final CustomFilterGridLayer filterGridLayer;

  /**
   * Defines pidc attribute mandatory filter action
   */
  private Action attrMandtoryAction;
  /**
   * Defines pidc attribute non-mandatory filter action
   */
  private Action attrNonMandtoryAction;
  private Action attrDepAction;
  private Action attrNonDepAction;

  private Action attrDiffAction;

  private Action attrNotDiffAction;

  private Action invisibleAttrAction;

  private final ComparePIDCPage comparePidcPage;

  /**
   * @param compPIDCFilterGridLayer layer instance
   */
  public ComparePIDCToolBarActionSet(final CustomFilterGridLayer compPIDCFilterGridLayer,
      final ComparePIDCPage comparePidcPage) {
    this.filterGridLayer = compPIDCFilterGridLayer;
    this.comparePidcPage = comparePidcPage;
  }


  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    // Create PIDC attribute mandatory filter action
    this.attrMandtoryAction = new Action("Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrMandatorySel(ComparePIDCToolBarActionSet.this.attrMandtoryAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrMandtoryAction.getText(),
            ComparePIDCToolBarActionSet.this.attrMandtoryAction.isChecked());

        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };
    // Set the image for PIDC attribute mandatory filter action
    this.attrMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_MANDATORY_16X16));
    this.attrMandtoryAction.setChecked(true);
    toolBarManager.add(this.attrMandtoryAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrMandtoryAction, this.attrMandtoryAction.isChecked());
  }

  /**
   * This method creates PIDC attribute non-mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrNonMandatoryFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    // Create PIDC attribute non-mandatory filter action
    this.attrNonMandtoryAction = new Action("Non-Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonMandatorySel(ComparePIDCToolBarActionSet.this.attrNonMandtoryAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrNonMandtoryAction.getText(),
            ComparePIDCToolBarActionSet.this.attrNonMandtoryAction.isChecked());


        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };
    // Set the image for PIDC attribute non-mandatory filter action
    this.attrNonMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_MANDATORY_28X30));
    this.attrNonMandtoryAction.setChecked(true);
    toolBarManager.add(this.attrNonMandtoryAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrNonMandtoryAction, this.attrNonMandtoryAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void pidcAttrInvisibleAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    // Filter for attributes controlling the visibility of other attributes
    this.invisibleAttrAction = new Action("Invisible Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrVisibility(ComparePIDCToolBarActionSet.this.invisibleAttrAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.invisibleAttrAction.getText(),
            ComparePIDCToolBarActionSet.this.invisibleAttrAction.isChecked());

        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }


    };
    // Set the image for PIDC attribute mandatory filter action
    this.invisibleAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    this.invisibleAttrAction.setChecked(false);
    toolBarManager.add(this.invisibleAttrAction);
    if (!this.invisibleAttrAction.isChecked()) {
      this.invisibleAttrAction.run();
    }

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.invisibleAttrAction, this.invisibleAttrAction.isChecked());

  }


  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  public void pidcAttrDepenFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    // Filter for attributes controlling the visibility of other attributes
    this.attrDepAction = new Action("Dependent Attributes", SWT.TOGGLE) {

      // Set attribute dependency flag
      @Override
      public void run() {
        toolBarFilters.setAttrDependency(ComparePIDCToolBarActionSet.this.attrDepAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrDepAction.getText(),
            ComparePIDCToolBarActionSet.this.attrDepAction.isChecked());


        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }


    };
    // Set the image for PIDC attribute mandatory filter action
    this.attrDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DEPN_ATTR_28X30));
    this.attrDepAction.setChecked(true);
    toolBarManager.add(this.attrDepAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrDepAction, this.attrDepAction.isChecked());

  }


  /**
   * Filter for attribute dependents
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  public void pidcAttrNotDepenFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    this.attrNonDepAction = new Action("Non Dependent Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonDep(ComparePIDCToolBarActionSet.this.attrNonDepAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrNonDepAction.getText(),
            ComparePIDCToolBarActionSet.this.attrNonDepAction.isChecked());

        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }


    };
    // Set the image for PIDC Not Dependent Filter
    this.attrNonDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_NONDEP_16X16));
    this.attrNonDepAction.setChecked(true);
    toolBarManager.add(this.attrNonDepAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrNonDepAction, this.attrNonDepAction.isChecked());

  }

  /**
   * Filter for attribute dependents
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  public void pidcAttrDiffFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    this.attrDiffAction = new Action("Attributes with difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrDiffSel(ComparePIDCToolBarActionSet.this.attrDiffAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrDiffAction.getText(),
            ComparePIDCToolBarActionSet.this.attrDiffAction.isChecked());


        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }


    };
    // Set the image for PIDC Not Dependent Filter
    this.attrDiffAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_YES_16X16));
    this.attrDiffAction.setChecked(true);
    toolBarManager.add(this.attrDiffAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrDiffAction, this.attrDiffAction.isChecked());

  }

  /**
   * Filter for attribute dependents
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  public void pidcAttrNotDiffFilterAction(final ToolBarManager toolBarManager,
      final ComparePIDCToolBarFilter toolBarFilters) {
    this.attrNotDiffAction = new Action("Attributes with no difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNotDiffSel(ComparePIDCToolBarActionSet.this.attrNotDiffAction.isChecked());
        ComparePIDCToolBarActionSet.this.comparePidcPage.getToolBarFilterStateMap().put(
            ComparePIDCToolBarActionSet.this.attrNotDiffAction.getText(),
            ComparePIDCToolBarActionSet.this.attrNotDiffAction.isChecked());


        ComparePIDCToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ComparePIDCToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }


    };
    // Set the image for PIDC Not Dependent Filter
    this.attrNotDiffAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_NO_16X16));
    this.attrNotDiffAction.setChecked(true);
    toolBarManager.add(this.attrNotDiffAction);

    // Adding the default state to filters map
    this.comparePidcPage.addToToolBarFilterMap(this.attrNotDiffAction, this.attrNotDiffAction.isChecked());

  }


  /**
   * @return the attrDiffAction
   */
  public Action getAttrDiffAction() {
    return this.attrDiffAction;
  }


  /**
   * @return the attrNotDiffAction
   */
  public Action getAttrNotDiffAction() {
    return this.attrNotDiffAction;
  }


  /**
   * @return the attrMandtoryAction
   */
  public Action getAttrMandtoryAction() {
    return this.attrMandtoryAction;
  }


  /**
   * @param attrMandtoryAction the attrMandtoryAction to set
   */
  public void setAttrMandtoryAction(final Action attrMandtoryAction) {
    this.attrMandtoryAction = attrMandtoryAction;
  }


  /**
   * @return the attrNonMandtoryAction
   */
  public Action getAttrNonMandtoryAction() {
    return this.attrNonMandtoryAction;
  }


  /**
   * @param attrNonMandtoryAction the attrNonMandtoryAction to set
   */
  public void setAttrNonMandtoryAction(final Action attrNonMandtoryAction) {
    this.attrNonMandtoryAction = attrNonMandtoryAction;
  }


  /**
   * @return the attrDepAction
   */
  public Action getAttrDepAction() {
    return this.attrDepAction;
  }


  /**
   * @param attrDepAction the attrDepAction to set
   */
  public void setAttrDepAction(final Action attrDepAction) {
    this.attrDepAction = attrDepAction;
  }


  /**
   * @return the attrNonDepAction
   */
  public Action getAttrNonDepAction() {
    return this.attrNonDepAction;
  }


  /**
   * @param attrNonDepAction the attrNonDepAction to set
   */
  public void setAttrNonDepAction(final Action attrNonDepAction) {
    this.attrNonDepAction = attrNonDepAction;
  }


  /**
   * @return the invisibleAttrAction
   */
  public Action getInvisibleAttrAction() {
    return this.invisibleAttrAction;
  }


  /**
   * @param invisibleAttrAction the invisibleAttrAction to set
   */
  public void setInvisibleAttrAction(final Action invisibleAttrAction) {
    this.invisibleAttrAction = invisibleAttrAction;
  }


  /**
   * @param attrDiffAction the attrDiffAction to set
   */
  public void setAttrDiffAction(final Action attrDiffAction) {
    this.attrDiffAction = attrDiffAction;
  }


  /**
   * @param attrNotDiffAction the attrNotDiffAction to set
   */
  public void setAttrNotDiffAction(final Action attrNotDiffAction) {
    this.attrNotDiffAction = attrNotDiffAction;
  }


}
