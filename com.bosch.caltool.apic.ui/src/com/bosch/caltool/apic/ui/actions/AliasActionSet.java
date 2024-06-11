/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.AliasAttrToolBarFilter;
import com.bosch.caltool.apic.ui.table.filters.AliasValueToolBarFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * Class for Attribute alias
 *
 * @author rgo7cob
 */
public class AliasActionSet {

  /**
   * Defines pidc attribute mandatory filter action
   */
  private Action attrWithAliasAction;
  /**
   * Defines pidc attribute non-mandatory filter action
   */
  /**
   * attr without alias
   */
  private Action attrWithoutAlias;
  /**
   * value with alias
   */
  protected Action valueWithAliasAction;
  /**
   * value without alias
   */
  protected Action valWithoutAliasAction;


  /**
   * Action for attribute with alias
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void attrWithAlias(final ToolBarManager toolBarManager, final AliasAttrToolBarFilter toolBarFilters,
      final CustomGridTableViewer viewer) {
    this.attrWithAliasAction = new Action("Attributes with alias", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrWithAlias(AliasActionSet.this.attrWithAliasAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrWithAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALIAS_16X16));
    this.attrWithAliasAction.setChecked(true);
    toolBarManager.add(this.attrWithAliasAction);
  }

  /**
   * Action for attribute with alias
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void valWithAlias(final ToolBarManager toolBarManager, final AliasValueToolBarFilter toolBarFilters,
      final CustomGridTableViewer viewer) {
    this.valueWithAliasAction = new Action("Value with alias", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setValWithAlias(AliasActionSet.this.valueWithAliasAction.isChecked());
        viewer.refresh();
      }
    };
    this.valueWithAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALIAS_16X16));
    this.valueWithAliasAction.setChecked(true);
    toolBarManager.add(this.valueWithAliasAction);
  }


  /**
   * Action for attribute without alias
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarManager
   * @param viewer table viewer
   */
  public void createAttrWithoutAliasAction(final ToolBarManager toolBarManager,
      final AliasAttrToolBarFilter toolBarFilters, final CustomGridTableViewer viewer) {
    this.attrWithoutAlias = new Action("Attributes without alias", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrWithoutAlias(AliasActionSet.this.attrWithoutAlias.isChecked());
        viewer.refresh();
      }
    };
    this.attrWithoutAlias.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTRIBUTES_EDITOR_16X16));
    this.attrWithoutAlias.setChecked(true);
    toolBarManager.add(this.attrWithoutAlias);
  }

  /**
   * Action for attribute without alias
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarManager
   * @param viewer table viewer
   */
  public void valWithoutAlias(final ToolBarManager toolBarManager, final AliasValueToolBarFilter toolBarFilters,
      final CustomGridTableViewer viewer) {
    this.valWithoutAliasAction = new Action("Value without alias", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setValWithoutAlias(AliasActionSet.this.valWithoutAliasAction.isChecked());
        viewer.refresh();
      }
    };
    this.valWithoutAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTRIBUTES_EDITOR_16X16));
    this.valWithoutAliasAction.setChecked(true);
    toolBarManager.add(this.valWithoutAliasAction);
  }
}
