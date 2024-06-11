/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.CdrReportListPage;
import com.bosch.caltool.cdr.ui.table.filters.CdrReportToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * @author mkl2cob
 */
public class CdrReportToolBarActionSet {

  // ICDM-2439
  private Action complianceParamAction;
  private Action nonComplianceParamAction;
  /**
   * filter grid layer
   */
  private final CustomFilterGridLayer dataRprtFilterGridLayer;
  protected Action noLatestFunAction;
  private Action latestFunAction;
  private final CdrReportListPage page;

  private Action showFulFilledRulesAction;
  private Action showNotFulFilledRulesAction;
  private Action showUnDefinedRulesAction;
  
  /** Actions for WP finished and WP not finished*/
  
  private Action showWPFinsihedAction;
  private Action showWPNotFinsihedAction;

  /**
   * Constructor
   *
   * @param dataRprtFilterGridLayer CustomFilterGridLayer
   */
  public CdrReportToolBarActionSet(final CustomFilterGridLayer dataRprtFilterGridLayer, final CdrReportListPage page) {
    this.dataRprtFilterGridLayer = dataRprtFilterGridLayer;
    this.page = page;
  }

  // ICDM-2439
  /**
   * action for compliance parameters
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   * @param natTable NAT Table
   */
  public void showComplianceParamsAction(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters, final CustomNATTable natTable) {

    this.complianceParamAction = new Action("Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setComplianceParam(CdrReportToolBarActionSet.this.complianceParamAction.isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    this.complianceParamAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    this.complianceParamAction.setChecked(true);
    toolBarManager.add(this.complianceParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.complianceParamAction, this.complianceParamAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void readOnlyAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action readOnlyAction = new Action("READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyParam(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    readOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.READ_ONLY_16X16));
    readOnlyAction.setChecked(true);
    toolBarManager.add(readOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(readOnlyAction, readOnlyAction.isChecked());
  }

  // ICDM-2439
  /**
   * action for non compliance parameters
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   * @param natTable NAT Table
   */
  public void showNonComplianceParamsAction(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters, final CustomNATTable natTable) {


    this.nonComplianceParamAction = new Action("Non Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNonComplianceParam(CdrReportToolBarActionSet.this.nonComplianceParamAction.isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    this.nonComplianceParamAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));

    this.nonComplianceParamAction.setChecked(true);
    toolBarManager.add(this.nonComplianceParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.nonComplianceParamAction, this.nonComplianceParamAction.isChecked());

  }


  /**
   * Action for bosch responsibility
   *
   * @param toolBarManager ToolBarManager
   */
  public void showBoschRespAction(final ToolBarManager toolBarManager) {

    Action boschRespAction = new Action("Bosch Responsibility", SWT.TOGGLE) {

      @Override
      public void run() {
        // Not Implemneted
      }
    };

    boschRespAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BOSCH_RESPONSIBLE_ICON_16X16));

    boschRespAction.setChecked(true);
    toolBarManager.add(boschRespAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(boschRespAction, boschRespAction.isChecked());

  }


  /**
   * Action for non-Bosch responsibility
   *
   * @param toolBarManager ToolBarManager
   */
  public void showBoschNotRespAction(final ToolBarManager toolBarManager) {

    Action boschNotRespAction = new Action("Non Bosch Responsibility", SWT.TOGGLE) {

      @Override
      public void run() {
        // Not Implemneted
      }
    };

    boschNotRespAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUST_RESPONSIBLE_ICON_16X16));

    boschNotRespAction.setChecked(true);
    toolBarManager.add(boschNotRespAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(boschNotRespAction, boschNotRespAction.isChecked());

  }

  /**
   * action for FullFilled Rules Action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  public void showFullFilledRulesAction(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters) {

    this.showFulFilledRulesAction = new Action("Show labels with fulfilled rules", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setShowFullFilledRules(CdrReportToolBarActionSet.this.showFulFilledRulesAction.isChecked());
        fireFilterEvent();
      }


    };

    this.showFulFilledRulesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OK_16X16));

    this.showFulFilledRulesAction.setChecked(true);
    toolBarManager.add(this.showFulFilledRulesAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.showFulFilledRulesAction, this.showFulFilledRulesAction.isChecked());

  }

  /**
   * action for Not FullFilled Rules Action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  public void showNotFullFilledRulesAction(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters) {


    this.showNotFulFilledRulesAction = new Action("Show labels with not fulfilled rules", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters
            .setShowNotFullFilledRules(CdrReportToolBarActionSet.this.showNotFulFilledRulesAction.isChecked());
        fireFilterEvent();
      }
    };

    this.showNotFulFilledRulesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_OK_16X16));

    this.showNotFulFilledRulesAction.setChecked(true);
    toolBarManager.add(this.showNotFulFilledRulesAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.showNotFulFilledRulesAction, this.showNotFulFilledRulesAction.isChecked());

  }

  /**
   * action for undefined Rules Action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  public void showUnDefinedRulesAction(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters) {


    this.showUnDefinedRulesAction = new Action("Show labels with undefined rules", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setShowUnDefinedRules(CdrReportToolBarActionSet.this.showUnDefinedRulesAction.isChecked());
        fireFilterEvent();
      }
    };

    this.showUnDefinedRulesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));

    this.showUnDefinedRulesAction.setChecked(true);
    toolBarManager.add(this.showUnDefinedRulesAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.showUnDefinedRulesAction, this.showUnDefinedRulesAction.isChecked());

  }

  /**
   *
   */
  private void fireFilterEvent() {
    // fire the filter event
    CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
  }

  // ICDM-2045
  /**
   * Action for Parameter with Function Version equal to latest Review Function Version
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  public void showMatchLatestFuncVer(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters) {

    this.latestFunAction =
        new Action("Parameter with Function Version equal to latest Review Function Version", SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in filter class
            toolBarFilters.setParamLatestFunc(CdrReportToolBarActionSet.this.latestFunAction.isChecked());
            // fire the filter event
            CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
          }
        };

    this.latestFunAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_CIRCLE_16X16));

    this.latestFunAction.setChecked(true);
    toolBarManager.add(this.latestFunAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.latestFunAction, this.latestFunAction.isChecked());
  }

  /**
   * Action for Parameter with Function Version not equal to latest Review Function Version
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  public void showNotMatchLatestFuncVer(final ToolBarManager toolBarManager,
      final CdrReportToolBarFilters toolBarFilters) {
    this.noLatestFunAction =
        new Action("Parameter with Function Version not equal to latest Review Function Version", SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in filter class
            toolBarFilters.setParamNoLatestFunc(CdrReportToolBarActionSet.this.noLatestFunAction.isChecked());
            // fire the filter event
            CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
          }
        };

    this.noLatestFunAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NONE_16X16));

    this.noLatestFunAction.setChecked(true);
    toolBarManager.add(this.noLatestFunAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.noLatestFunAction, this.noLatestFunAction.isChecked());

  }

  /**
   * Action for showing locked latest Review
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  public void showLockedRvws(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action lockedLatestRvwAction = new Action("Latest Review having Locked Status", SWT.TOGGLE) {

      /**
       * Method if the filter checked, then the action start to show the locked latest review
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setLockedLatestRvw(isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    lockedLatestRvwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LOCK_16X16));
    lockedLatestRvwAction.setChecked(true);
    toolBarManager.add(lockedLatestRvwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(lockedLatestRvwAction, lockedLatestRvwAction.isChecked());
  }

  /**
   * Action for showing un-locked latest Review
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  public void showUnLockedRvws(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action unLockedLatestRvwAction = new Action("Latest Review having un-locked Status", SWT.TOGGLE) {

      /**
       * Method if the filter checked, then the action start to show the un-locked latest review
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setUnLockedLatestRvw(isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    unLockedLatestRvwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNLOCK_16X16));
    unLockedLatestRvwAction.setChecked(true);
    toolBarManager.add(unLockedLatestRvwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(unLockedLatestRvwAction, unLockedLatestRvwAction.isChecked());
  }


  /**
   * Action for showing latest Review having start review type
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  public void showStartTypeRvw(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action startLatestRvwAction = new Action("Showing Latest Review of Start Review Type", SWT.TOGGLE) {

      /**
       * Method if the filter checked, then the action start to show the start type latest review
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setStartTypeLatestRvw(isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    startLatestRvwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.START_CDRREVIEW_16X16));
    startLatestRvwAction.setChecked(true);
    toolBarManager.add(startLatestRvwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(startLatestRvwAction, startLatestRvwAction.isChecked());
  }

  /**
   * Action for showing latest Review having official review type
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  public void showOfficialTypeRvw(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action officialLatestRvwAction = new Action("Showing Latest Review of Official Review Type", SWT.TOGGLE) {

      /**
       * Method if the filter checked, then the action start to show the official type latest review
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setOfficialTypeLatestRvw(isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };

    officialLatestRvwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OFFICIAL_ICON_16X16));
    officialLatestRvwAction.setChecked(true);
    toolBarManager.add(officialLatestRvwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(officialLatestRvwAction, officialLatestRvwAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notReadOnlyAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {
    Action notReadOnlyaction = new Action("Not READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReadOnlyParam(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    notReadOnlyaction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_READ_ONLY_16X16));
    notReadOnlyaction.setChecked(true);
    toolBarManager.add(notReadOnlyaction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReadOnlyaction, notReadOnlyaction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CdrReportToolBarFilters
   * @param natTable CustomNATTable
   */
  public void showQSSDParamsAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {
    Action qSSDAction = new Action("QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setqSSDParams(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for QSSD filter
    qSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    qSSDAction.setChecked(true);
    toolBarManager.add(qSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(qSSDAction, qSSDAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CdrReportToolBarFilters
   * @param natTable CustomNATTable
   */
  public void showNonQSSDParamsAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {
    Action nonQSSDAction = new Action("Non QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonQSSDParams(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for non QSSD filter
    nonQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    nonQSSDAction.setChecked(true);
    toolBarManager.add(nonQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonQSSDAction, nonQSSDAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void dependantCharAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action dependantCharAction = new Action("Parameters with Dependant Characteristics", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDependantCharParam(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    dependantCharAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    dependantCharAction.setChecked(true);
    toolBarManager.add(dependantCharAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(dependantCharAction, dependantCharAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void noDependantCharAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    Action noDependantCharAction = new Action("Parameters without Dependant Characteristics", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoDependantCharParam(isChecked());
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    noDependantCharAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    noDependantCharAction.setChecked(true);
    toolBarManager.add(noDependantCharAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noDependantCharAction, noDependantCharAction.isChecked());
  }
  
  /**
   * action for WP finished parameters
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   * @param natTable NAT Table
   */
  public void showWPFinishedAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    this.showWPFinsihedAction = new Action("Show WP Finished parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setWpFinished(CdrReportToolBarActionSet.this.showWPFinsihedAction.isChecked());
        // fire the filter event
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
        natTable.redraw();
      }
    };
        
    this.showWPFinsihedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.showWPFinsihedAction.setChecked(true);
    toolBarManager.add(this.showWPFinsihedAction);
    this.page.addToToolBarFilterMap(this.showWPFinsihedAction, this.showWPFinsihedAction.isChecked());
  }
  
  /**
   * action for WP not finished parameters
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters CDRRvwReportToolBarFilters
   * @param natTable NAT Table
   */
  public void showWPNotFinishedAction(final ToolBarManager toolBarManager, final CdrReportToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    this.showWPNotFinsihedAction = new Action("Show WP not Finished parameters", SWT.TOGGLE) {

      @Override
      public void run() {
          // set the boolean in filter class
        toolBarFilters.setWpNotFinished(CdrReportToolBarActionSet.this.showWPNotFinsihedAction.isChecked());
          // fire the filter event
          CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getFilterStrategy()
              .applyToolBarFilterInAllColumns(false);
          CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()
              .fireLayerEvent(new FilterAppliedEvent(
                  CdrReportToolBarActionSet.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
          natTable.redraw();
        }
    };
    
    this.showWPNotFinsihedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.showWPNotFinsihedAction.setChecked(true);
    toolBarManager.add(this.showWPNotFinsihedAction);
    this.page.addToToolBarFilterMap(this.showWPNotFinsihedAction, this.showWPNotFinsihedAction.isChecked());
  }

}
