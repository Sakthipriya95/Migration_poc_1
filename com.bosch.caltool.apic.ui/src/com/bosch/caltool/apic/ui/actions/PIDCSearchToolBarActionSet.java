/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.PIDCSearchToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * @author bru2cob ICDM-1158
 */
public class PIDCSearchToolBarActionSet {

  /**
   * Defines pidc attribute mandatory filter action
   */
  private Action attrMandtoryAction;
  /**
   * Defines pidc attribute non-mandatory filter action
   */
  private Action attrNonMandtoryAction;
  /**
   * Defines pidc attribute text value filter action
   */
  private Action attrTextAction;
  /**
   * Defines pidc attribute number type filter action
   */
  private Action attrNumberAction;
  /**
   * Defines pidc attribute boolean type filter action
   */
  private Action attrBoolAction;
  /**
   * Defines pidc attribute date type filter action
   */
  private Action attrDateAction;
  /**
   * Defines pidc attribute hyperlink type filter action
   */
  private Action attrLinkAction;

  /**
   * Defines pidc attribute selected action
   */
  private Action attrSelectedAction;

  /**
   * Defines pidc attribute not selected action
   */
  private Action attrNotSelectedAction;


  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrMandtoryAction = new Action("Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrMandatorySel(PIDCSearchToolBarActionSet.this.attrMandtoryAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_MANDATORY_16X16));
    this.attrMandtoryAction.setChecked(true);
    toolBarManager.add(this.attrMandtoryAction);
  }

  /**
   * This method creates PIDC attribute non-mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrNonMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrNonMandtoryAction = new Action("Non-Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonMandatorySel(PIDCSearchToolBarActionSet.this.attrNonMandtoryAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrNonMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_MANDATORY_28X30));
    this.attrNonMandtoryAction.setChecked(true);
    toolBarManager.add(this.attrNonMandtoryAction);
  }

  /**
   * This method creates PIDC attribute text filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrTextFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrTextAction = new Action("Text value type", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrTextSel(PIDCSearchToolBarActionSet.this.attrTextAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrTextAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_TEXT_16X16));
    this.attrTextAction.setChecked(true);
    toolBarManager.add(this.attrTextAction);
  }

  /**
   * This method creates PIDC attribute number filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrNumberFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrNumberAction = new Action("Number value type", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNumberSel(PIDCSearchToolBarActionSet.this.attrNumberAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrNumberAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_NUMBER_16X16));
    this.attrNumberAction.setChecked(true);
    toolBarManager.add(this.attrNumberAction);
  }

  /**
   * This method creates PIDC attribute boolean filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrBoolFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrBoolAction = new Action("Boolean value type", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrBoolSel(PIDCSearchToolBarActionSet.this.attrBoolAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrBoolAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_BOOL_16X16));
    this.attrBoolAction.setChecked(true);
    toolBarManager.add(this.attrBoolAction);
  }

  /**
   * This method creates PIDC attribute date filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrDateFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrDateAction = new Action("Date value type", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrDateSel(PIDCSearchToolBarActionSet.this.attrDateAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrDateAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CALENDAR_16X16));
    this.attrDateAction.setChecked(true);
    toolBarManager.add(this.attrDateAction);
  }

  /**
   * This method creates PIDC attribute link filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrLinkFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrLinkAction = new Action("Link value type", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrLinkSel(PIDCSearchToolBarActionSet.this.attrLinkAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HYPER_LINK_16X16));
    this.attrLinkAction.setChecked(true);
    toolBarManager.add(this.attrLinkAction);
  }

  /**
   * icdm-1291 This method creates PIDC attribute SELECTED filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrCheckedFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrSelectedAction = new Action("Show selected attributes/values", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrCheckedSel(PIDCSearchToolBarActionSet.this.attrSelectedAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrSelectedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_YES_16X16));
    this.attrSelectedAction.setChecked(true);
    toolBarManager.add(this.attrSelectedAction);
  }

  /**
   * icdm-1291 This method creates PIDC attribute NOT SELECTED filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void pidcAttrNotCheckedFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchToolBarFilters toolBarFilters, final CheckboxTreeViewer viewer) {
    this.attrNotSelectedAction = new Action("Show attributes/values not selected", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrUnCheckedSel(PIDCSearchToolBarActionSet.this.attrNotSelectedAction.isChecked());
        viewer.refresh();
      }
    };
    this.attrNotSelectedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_NO_16X16));
    this.attrNotSelectedAction.setChecked(true);
    toolBarManager.add(this.attrNotSelectedAction);
  }
}
