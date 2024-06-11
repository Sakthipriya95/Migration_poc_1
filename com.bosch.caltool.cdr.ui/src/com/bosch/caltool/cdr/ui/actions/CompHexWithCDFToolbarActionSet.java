/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.CompHexWithCdfNatPage;
import com.bosch.caltool.cdr.ui.table.filters.CompHexWithCdfToolBarFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;

/**
 * @author mkl2cob
 */
public class CompHexWithCDFToolbarActionSet {

  private final CustomFilterGridLayer<CompHexWithCDFParam> compRprtFilterGridLayer;

  private final CompHexWithCdfNatPage page;

  /**
   * @param compRprtFilterGridLayer
   */
  public CompHexWithCDFToolbarActionSet(final CustomFilterGridLayer<CompHexWithCDFParam> compRprtFilterGridLayer,
      final CompHexWithCdfNatPage page) {
    this.compRprtFilterGridLayer = compRprtFilterGridLayer;
    this.page = page;
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showMatchLatestFuncVer(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action latestFunAction =
        new Action("Parameter with Function Version equal to latest Review Function Version", SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in filter class
            toolBarFilters.setParamLatestFunc(isChecked());
            // fire the filter event
            CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
            CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
          }
        };

    latestFunAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_CIRCLE_16X16));

    latestFunAction.setChecked(true);
    toolBarManager.add(latestFunAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(latestFunAction, latestFunAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showNotMatchLatestFuncVer(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {

    final Action notlatestFunAction =
        new Action("Parameter with Function Version not equal to latest Review Function Version", SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in filter class
            toolBarFilters.setParamNoLatestFunc(isChecked());
            // fire the filter event
            CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
            CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
          }
        };

    notlatestFunAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NONE_16X16));

    notlatestFunAction.setChecked(true);
    toolBarManager.add(notlatestFunAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notlatestFunAction, notlatestFunAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void complianceFilterAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    // Filter for compliance parameters
    final Action complianceAction = new Action("Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setComplianceFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for compliance parameters
    complianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    complianceAction.setChecked(true);
    toolBarManager.add(complianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(complianceAction, complianceAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void nonComplianceFilterAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    // Filter for non compliance parameters
    final Action nonComplianceAction = new Action("Non Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonComplianceFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for non compliance parameters
    nonComplianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));
    nonComplianceAction.setChecked(true);
    toolBarManager.add(nonComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonComplianceAction, nonComplianceAction.isChecked());
  }

  /**
   * This method creates toolbar action for showing only parameters having review result as COMPLI
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public void showCompli(final ToolBarManager toolBarManager, final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action okAction = new Action("Compli Result : Failed", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setResultCompliFlag(isChecked());
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    okAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RESULT_COMPLI_16X16));
    okAction.setEnabled(true);
    okAction.setChecked(true);
    toolBarManager.add(okAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(okAction, okAction.isChecked());

  }

  /**
   * This method creates toolbar action for showing only parameters having review result as OK
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public final void showOk(final ToolBarManager toolBarManager, final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action okAction = new Action("Compli Result : Ok", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setOkFlag(isChecked());
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    okAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OK_16X16));
    okAction.setEnabled(true);
    okAction.setChecked(true);
    toolBarManager.add(okAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(okAction, okAction.isChecked());

  }

  // ICDM-632
  /**
   * This method creates toolbar action for showing only parameters having review result Not-ok,low and high
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public final void showNotApplicable(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action notOkAction = new Action("Compli Result : NA", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotApplicableFlag(isChecked());
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    notOkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_PT_NOT_RELEVANT_16X16));
    notOkAction.setEnabled(true);
    notOkAction.setChecked(true);
    toolBarManager.add(notOkAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notOkAction, notOkAction.isChecked());
  }

  /**
  *
  */
  private void applyColumnFilter() {
    // Toolbar filter for all Columns
    this.compRprtFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showReadOnlyAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action readOnlyAction = new Action("Read Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for compliance parameters
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
  public void showNotReadOnlyAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action notReadOnlyAction = new Action("Not Read Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReadOnlyFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for compliance parameters
    notReadOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_READ_ONLY_16X16));
    notReadOnlyAction.setChecked(true);
    toolBarManager.add(notReadOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReadOnlyAction, notReadOnlyAction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CompHexWithCdfToolBarFilter
   */
  public void qSSDFilterAction(final ToolBarManager toolBarManager, final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action qSSDFilterAction = new Action("QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setqSSDFlag(isChecked());
        // fire the filter event
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for QSSD parameters
    qSSDFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    qSSDFilterAction.setChecked(true);
    toolBarManager.add(qSSDFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(qSSDFilterAction, qSSDFilterAction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CompHexWithCdfToolBarFilter
   */
  public void nonQSSDFilterAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action nonQSSDFilterAction = new Action("Non QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonQSSDFlag(isChecked());
        // fire the filter event
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for non QSSD parameters
    nonQSSDFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    nonQSSDFilterAction.setChecked(true);
    toolBarManager.add(nonQSSDFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonQSSDFilterAction, nonQSSDFilterAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CompHexWithCdfToolBarFilter
   * @param natTable CustomNATTable
   */
  public void blackListFilterAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters, final CustomNATTable natTable) {
    final Action blackListFilterAction = new Action("Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setBlackListFlag(isChecked());
        // fire the filter event
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for QSSD parameters
    blackListFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    blackListFilterAction.setChecked(true);
    toolBarManager.add(blackListFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(blackListFilterAction, blackListFilterAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CompHexWithCdfToolBarFilter
   * @param natTable CustomNATTable
   */
  public void nonBlackListFilterAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters, final CustomNATTable natTable) {
    final Action nonBlackFilterAction = new Action("Non Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonBlackListFlag(isChecked());
        // fire the filter event
        applyColumnFilter();
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for QSSD parameters
    nonBlackFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    nonBlackFilterAction.setChecked(true);
    toolBarManager.add(nonBlackFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackFilterAction, nonBlackFilterAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void dependantCharAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action dependantCharAction = new Action("Parameters with Dependant Characteristics", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDependantCharParam(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for compliance parameters
    dependantCharAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    dependantCharAction.setChecked(true);
    toolBarManager.add(dependantCharAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(dependantCharAction, dependantCharAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void noDependantCharAction(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action noDependantCharAction = new Action("Parameters without Dependant Characteristics", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoDependantCharParam(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    // Set the image for compliance parameters
    noDependantCharAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    noDependantCharAction.setChecked(true);
    toolBarManager.add(noDependantCharAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noDependantCharAction, noDependantCharAction.isChecked());

  }
  /** method is to filter wp finished parameters
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showWpFinished(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action wpFinished = new Action("show WP finished Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWpFinishedFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    /** Set the image for wp finished parameters*/
    wpFinished.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    wpFinished.setChecked(true);
    toolBarManager.add(wpFinished);

    /**Adding the default state to filters map*/
    this.page.addToToolBarFilterMap(wpFinished, wpFinished.isChecked());

  }
  
  /**
   * method is to filter wp not finished parameters
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showWpNotFinished(final ToolBarManager toolBarManager,
      final CompHexWithCdfToolBarFilter toolBarFilters) {
    final Action wpNotFinished = new Action("show WP not finished Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWpNotFinishedFlag(isChecked());
        // fire the filter event
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CompHexWithCDFToolbarActionSet.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        CompHexWithCDFToolbarActionSet.this.page.getNatTable().redraw();
      }
    };
    /** Set the image for wp not finished parameters*/
    wpNotFinished.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    wpNotFinished.setChecked(true);
    toolBarManager.add(wpNotFinished);

    /** Adding the default state to filters map*/
    this.page.addToToolBarFilterMap(wpNotFinished, wpNotFinished.isChecked());

  }
}
