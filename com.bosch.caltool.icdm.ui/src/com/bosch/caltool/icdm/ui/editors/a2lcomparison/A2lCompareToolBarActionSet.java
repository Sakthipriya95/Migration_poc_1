/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author bru2cob
 */
public class A2lCompareToolBarActionSet {

  /** Instance of WPLabelAssignPage page. */
  private final A2lParamComparePage page;

  /**
   * @param a2lParamComparePage
   */
  public A2lCompareToolBarActionSet(final A2lParamComparePage a2lParamComparePage) {
    super();
    this.page = a2lParamComparePage;
  }

  /**
   * @param toolBarFilters
   * @param toolBarManager
   */
  public void addComplianceFilterAction(final A2lCompareToolBarFilters toolBarFilters,
      final ToolBarManager toolBarManager) {

    Action isComplianceAction = new Action(CommonUIConstants.FILTER_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setComplianceFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for compliance filter
    isComplianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    isComplianceAction.setChecked(true);
    toolBarManager.add(isComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isComplianceAction, isComplianceAction.isChecked());
  }

  /**
   *
   */
  protected void applyFilter() {
    this.page.getA2lCompFilterGridLayer().getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.page.getA2lCompFilterGridLayer().getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.page.getA2lCompFilterGridLayer().getSortableColumnHeaderLayer()));
    this.page.setStatusBarMessage(false);
    this.page.getNatTable().redraw();
  }

  /**
   * @param toolBarFilters
   * @param toolBarManager
   */
  public void addNonComplianceFilterAction(final A2lCompareToolBarFilters toolBarFilters,
      final ToolBarManager toolBarManager) {

    Action isNonComplianceAction = new Action(CommonUIConstants.FILTER_NON_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonComplianceFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for compliance filter
    isNonComplianceAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));
    isNonComplianceAction.setChecked(true);
    toolBarManager.add(isNonComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isNonComplianceAction, isNonComplianceAction.isChecked());
  }

  /**
   * @param toolBarFilters
   * @param toolBarManager
   */
  public void addQSSDFilterAction(final A2lCompareToolBarFilters toolBarFilters, final ToolBarManager toolBarManager) {

    Action isQSSDAction = new Action(CommonUIConstants.FILTER_QSSD_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setqSSDFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for nonQSSD filter
    isQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    isQSSDAction.setChecked(true);
    toolBarManager.add(isQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isQSSDAction, isQSSDAction.isChecked());
  }

  /**
   * @param toolBarFilters
   * @param toolBarManager
   */
  public void addNonQSSDFilterAction(final A2lCompareToolBarFilters toolBarFilters, final ToolBarManager toolBarManager) {


    Action isNonQSSDAction = new Action(CommonUIConstants.FILTER_NON_QSSD_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonQSSDFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for non QSSD filter
    isNonQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    isNonQSSDAction.setChecked(true);
    toolBarManager.add(isNonQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isNonQSSDAction, isNonQSSDAction.isChecked());


  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addParamDiffFilterAction(final ToolBarManager toolBarManager,
      final A2lCompareToolBarFilters toolBarFilters) {
    Action paramDiffAction = new Action("Parameters with difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setParamDiff(isChecked());
        applyFilter();
      }


    };
    // Set the image for PIDC Not Dependent Filter
    paramDiffAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_YES_16X16));
    paramDiffAction.setChecked(true);
    toolBarManager.add(paramDiffAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(paramDiffAction, paramDiffAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addParamNotDiffFilterAction(final ToolBarManager toolBarManager,
      final A2lCompareToolBarFilters toolBarFilters) {
    Action attrNotDiffAction = new Action("Parameters with no difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setParamNotDiff(isChecked());
        applyFilter();
      }


    };
    // Set the image for PIDC Not Dependent Filter
    attrNotDiffAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_NO_16X16));
    attrNotDiffAction.setChecked(true);
    toolBarManager.add(attrNotDiffAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrNotDiffAction, attrNotDiffAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addBlackListFilterAction(final ToolBarManager toolBarManager,
      final A2lCompareToolBarFilters toolBarFilters) {

    Action blackListAction = new Action("Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setBlackListFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for compliance filter
    blackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    blackListAction.setChecked(true);
    toolBarManager.add(blackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(blackListAction, blackListAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addNonBlackListFilterAction(final ToolBarManager toolBarManager,
      final A2lCompareToolBarFilters toolBarFilters) {

    Action nonBlackListAction = new Action("Non Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonBlackListFlag(isChecked());
        applyFilter();
        A2lCompareToolBarActionSet.this.page.getNatTable().refresh();
      }
    };
    // Set the image for compliance filter
    nonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    nonBlackListAction.setChecked(true);
    toolBarManager.add(nonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackListAction, nonBlackListAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addReadOnlyAction(final ToolBarManager toolBarManager, final A2lCompareToolBarFilters toolBarFilters) {


    Action readOnlyAction = new Action("READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyParam(isChecked());
        applyFilter();
      }
    };
    // Set the image for compliance filter
    readOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.READ_ONLY_16X16));
    readOnlyAction.setChecked(true);
    toolBarManager.add(readOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(readOnlyAction, readOnlyAction.isChecked());


  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void addNotReadOnlyAction(final ToolBarManager toolBarManager, final A2lCompareToolBarFilters toolBarFilters) {

    Action notReadOnlyAction = new Action("Not READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReadOnlyParam(isChecked());
        applyFilter();
      }
    };
    notReadOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_READ_ONLY_16X16));
    notReadOnlyAction.setChecked(true);
    toolBarManager.add(notReadOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReadOnlyAction, notReadOnlyAction.isChecked());

  }

}
