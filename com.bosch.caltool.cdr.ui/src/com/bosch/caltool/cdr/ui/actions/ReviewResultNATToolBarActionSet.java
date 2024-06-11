/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.table.filters.ReviewResultToolBarFilters;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * CDR Result page - tool bar actions
 */
public class ReviewResultNATToolBarActionSet {

  /**
   * constant for import file extn
   */
  protected static final String IMPORT_VALUE_IMPORT_FILE_EXTN = "importValImportImportFileExtn";
  /**
   * constant for import file name
   */
  protected static final String IMPORT_VALUE_IMPORT_FILE_NAME = "importValImportImportFileName";
  /**
   * Grid layer
   */
  private final CustomFilterGridLayer<T> filterGridLayer;
  /**
   * CDR Result nat page
   */
  ReviewResultParamListPage page;

  // Toolbar lock action
  private Action lockAction;

  /**
   * @return the lockAction
   */
  public Action getLockAction() {
    return this.lockAction;
  }

  // Toolbar unlock action
  private Action unlockAction;
  ReviewResultClientBO resultData;

  /**
   * @return the unlockAction
   */
  public Action getUnlockAction() {
    return this.unlockAction;
  }

  /**
   * Constructor
   *
   * @param page page
   * @param filterGridLayer layer
   */
  public ReviewResultNATToolBarActionSet(final ReviewResultParamListPage page,
      final CustomFilterGridLayer<T> filterGridLayer) {
    this.page = page;
    this.filterGridLayer = filterGridLayer;
    this.resultData = ((ReviewResultEditor) page.getEditor()).getEditorInput().getResultData();
  }

  /**
   *
   */
  private void applyColumnFilter() {
    // Toolbar filter for all Columns
    ReviewResultNATToolBarActionSet.this.filterGridLayer.getComboGlazedListsFilterStrategy()
        .applyToolBarFilterInAllColumns(false);
    ReviewResultNATToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(ReviewResultNATToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
    ReviewResultNATToolBarActionSet.this.page.setStatusBarMessage(this.page.getGroupByHeaderLayer(), false);
  }

  /**
   * This method creates toolbar action for showing only parameters whose results are reviewed
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public final void showReviewed(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for reviewed parameters
    final Action reviewedAction = new Action("Reviewed:Yes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReviewedFlag(isChecked());
        applyColumnFilter();
      }

    };
    reviewedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REVIEWED_28X30));
    reviewedAction.setEnabled(true);
    reviewedAction.setChecked(true);
    toolBarManager.add(reviewedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(reviewedAction, reviewedAction.isChecked());
  }

  /**
   * This method creates toolbar action for showing only parameters whose results are not reviewed
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public final void showNotReviewed(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action notReviewedAction = new Action("Reviewed:No", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReviewedFlag(isChecked());
        applyColumnFilter();
      }
    };
    notReviewedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_REVIEWED_28X30));
    notReviewedAction.setEnabled(true);
    notReviewedAction.setChecked(true);
    toolBarManager.add(notReviewedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReviewedAction, notReviewedAction.isChecked());
  }

  /**
   * This method creates toolbar action for showing only parameters having review result as COMPLI
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public void showCompli(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action okAction = new Action("Result:COMPLI/SHAPE", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setResultCompliFlag(isChecked());
        applyColumnFilter();
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
  public final void showOk(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action okAction = new Action("Result:Ok", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setOkFlag(isChecked());
        applyColumnFilter();
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
  public final void showResultNotOk(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action notOkAction = new Action("Result:NotOk", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotOkFlag(isChecked());
        applyColumnFilter();

      }
    };
    notOkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_OK_16X16));
    notOkAction.setEnabled(true);
    notOkAction.setChecked(true);
    toolBarManager.add(notOkAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notOkAction, notOkAction.isChecked());
  }


  /**
   * This method creates toolbar action for showing only parameters having review result undefined
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   */
  public final void showResultUndefined(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action undefAction = new Action("Result:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setUndefinedFlag(isChecked());
        applyColumnFilter();
      }
    };
    undefAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    undefAction.setEnabled(true);
    undefAction.setChecked(true);
    toolBarManager.add(undefAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(undefAction, undefAction.isChecked());
  }


  /**
   * This method filters the secondary result Ok in Rvw Result Editor
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  // Task 236307
  public final void showSecResOk(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action actionForSecResOk = new Action("Secondary Result : Ok", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setSecResultOkFlag(isChecked());
        applyColumnFilter();
      }
    };
    actionForSecResOk.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICDON_SECONDARY_OK_RESULT));
    actionForSecResOk.setEnabled(true);
    actionForSecResOk.setChecked(true);
    toolBarManager.add(actionForSecResOk);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(actionForSecResOk, actionForSecResOk.isChecked());
  }

  /**
   * This method filters the secondary result Not Ok in Rvw Result Editor
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  // Task 236307
  public final void showSecResNoOk(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action actionForSecResOk = new Action("Secondary Result : Not Ok", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setSecResultNotOkFlag(isChecked());
        applyColumnFilter();
      }
    };
    actionForSecResOk.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICDON_SECONDARY_NOT_OK_RESULT));
    actionForSecResOk.setEnabled(true);
    actionForSecResOk.setChecked(true);
    toolBarManager.add(actionForSecResOk);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(actionForSecResOk, actionForSecResOk.isChecked());
  }

  /**
   * This method filters the secondary result Not applicable in Rvw Result Editor
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  // Task 236307
  public final void showSecResNA(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action actionForSecResOk = new Action("Secondary Result : N/A", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setSecResultNAFlag(isChecked());
        applyColumnFilter();
      }
    };
    actionForSecResOk.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICDON_NOT_APPLICABLE_16X16));
    actionForSecResOk.setEnabled(true);
    actionForSecResOk.setChecked(true);
    toolBarManager.add(actionForSecResOk);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(actionForSecResOk, actionForSecResOk.isChecked());
  }


  /**
   * This method filters the secondary result Checked in Rvw Result Editor
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  // Task 236307
  public final void showSecResChecked(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action actionForSecResOk = new Action("Secondary Result : Checked", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setSecResultCheckedFlag(isChecked());
        applyColumnFilter();
      }
    };
    actionForSecResOk.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKED_16X16));
    actionForSecResOk.setEnabled(true);
    actionForSecResOk.setChecked(true);
    toolBarManager.add(actionForSecResOk);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(actionForSecResOk, actionForSecResOk.isChecked());
  }

  /**
   * This method creates curve filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void curveFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter For curve Value
    final Action curveAction = new Action("Curve", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setCurveFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Curve Filter
    curveAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CURVE_16X16));

    curveAction.setChecked(true);
    toolBarManager.add(curveAction);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(curveAction, curveAction.isChecked());
  }

  /**
   * This method creates map filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */

  public final void mapFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter For Map Value
    final Action mapAction = new Action("Map", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setMapFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Map Filter
    mapAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MAP_16X16));

    mapAction.setChecked(true);
    toolBarManager.add(mapAction);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(mapAction, mapAction.isChecked());
  }

  /**
   * This method creates value type filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void valueFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for Value
    final Action valueAction = new Action("Value", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setValueFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Value Type
    valueAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_16X16));

    valueAction.setChecked(true);
    toolBarManager.add(valueAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(valueAction, valueAction.isChecked());
  }

  /**
   * This method creates ascii type filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void asciiFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter For ascii
    final Action asciiAction = new Action("Ascii", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAsciiFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Ascii
    asciiAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ASCII_16X16));

    asciiAction.setChecked(true);
    toolBarManager.add(asciiAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(asciiAction, asciiAction.isChecked());
  }

  /**
   * This method creates value block type filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void valueBlockFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter For Value Block
    final Action valueBlockAction = new Action("Value Block", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setValueBlkFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Value Block
    valueBlockAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALBLK_16X16));
    valueBlockAction.setChecked(true);
    toolBarManager.add(valueBlockAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(valueBlockAction, valueBlockAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void complianceFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    // Filter for compliance parameters
    final Action complianceAction = new Action("Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setComplianceFlag(isChecked());
        applyColumnFilter();
        natTable.redraw();
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
   * @param natTable NAT table
   */
  public void nonComplianceFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    // Filter for non compliance parameters
    final Action nonComplianceAction = new Action("Non Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonComplianceFlag(isChecked());
        applyColumnFilter();
        natTable.redraw();
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
   * This method creates axis point filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void axisPointFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for Axis Point
    final Action axisPointAction = new Action("Axis Points", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAxisFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Axis Point
    axisPointAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.AXIS_16X16));
    axisPointAction.setChecked(true);
    toolBarManager.add(axisPointAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(axisPointAction, axisPointAction.isChecked());
  }

  /**
   * This method creates screw filter action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public final void screwFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for Screw class
    final Action screwAction = new Action("Class:Screw", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setScrewFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Screw Class
    screwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SCREW_28X30));
    screwAction.setChecked(true);
    toolBarManager.add(screwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(screwAction, screwAction.isChecked());

  }

  /**
   * This method creates nail filter action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public final void nailFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for Nail class
    final Action nailAction = new Action("Class:Nail", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNailFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for Nail Class
    nailAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NAIL_28X30));
    nailAction.setChecked(true);
    toolBarManager.add(nailAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nailAction, nailAction.isChecked());

  }

  /**
   * This method creates rivet filter action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public final void rivetFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for Rivet class
    final Action rivetAction = new Action("Class:Rivet", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRivetFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for rivet class
    rivetAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RIVET_28X30));
    rivetAction.setChecked(true);
    toolBarManager.add(rivetAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(rivetAction, rivetAction.isChecked());

  }

  /**
   * This method creates undefined class filter action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public final void undefinedClassFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    // Filter for undefined class
    final Action noClassAction = new Action("Class:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoClassFlag(isChecked());
        applyColumnFilter();
      }
    };
    // Set the image for rivet class
    noClassAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    noClassAction.setChecked(true);
    toolBarManager.add(noClassAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noClassAction, noClassAction.isChecked());

  }


  /**
   * Filter For the parameter with change marker Icdm-807
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void hasChangeMarkerAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action chngMarkAction = new Action("Delta Review : Changes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setChangeMarkFlag(isChecked());
        applyColumnFilter();


      }
    };
    chngMarkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHANGE_MARKER_16X16));
    chngMarkAction.setChecked(true);
    toolBarManager.add(chngMarkAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(chngMarkAction, chngMarkAction.isChecked());

  }

  /**
   * Filter For the parameter with no change marker Icdm-807
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void hasNoChangeMarkerAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters) {
    final Action noChngMarkAction = new Action("Delta Review :No Changes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoChangeMarkFlag(isChecked());
        applyColumnFilter();
      }
    };
    noChngMarkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_CHANGE_MARKER_16X16));
    noChngMarkAction.setChecked(true);
    toolBarManager.add(noChngMarkAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noChngMarkAction, noChngMarkAction.isChecked());

  }

  // ICDM-1197
  /**
   * Filter For the parameter with history info
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void hasHistoryAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action histAction = new Action("Check Value History:Yes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setHistoryFlag(isChecked());
        applyColumnFilter();
      }
    };
    histAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_PARAM_HIST_16X16));
    histAction.setEnabled(true);
    histAction.setChecked(true);
    toolBarManager.add(histAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(histAction, histAction.isChecked());

  }

  // ICDM-1197
  /**
   * Filter For the parameter with no history
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void hasNoHistoryAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters) {
    final Action noHistAction = new Action("Check Value History:No", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoHistoryFlag(isChecked());
        applyColumnFilter();
      }
    };
    noHistAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_NO_PARAM_HIST_16X16));
    noHistAction.setEnabled(true);
    noHistAction.setChecked(true);
    toolBarManager.add(noHistAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noHistAction, noHistAction.isChecked());

  }


  // ICDM-1257
  /**
   * Load own data to cal data review results
   *
   * @param toolBarManager toolBarManager
   * @param page Review Result Param List Page
   */
  public void importValueFromFileAction(final ToolBarManager toolBarManager, final ReviewResultParamListPage page) {
    final Action importDataFromFileAction = new Action("Load values from File") {

      @Override
      public void run() {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Import from File");

        String[] fileNames = new String[] {
            "DCM Files (*.DCM)",
            "PaCo Files (*.XML)",
            "CDFx Files (*.CDFX)",
            "All Calibration Data Files (*.DCM, *.XML, *.CDFX)" };
        String[] fileExtns = new String[] { "*.DCM", "*.xml", "*.CDFx", "*.DCM;*.xml;*.CDFx" };
        // Get the eclipse preference store
        IPreferenceStore preference = PlatformUI.getPreferenceStore();
        // Retrieve the last used file extension from the preference store, and set it in the file dialog
        CommonUtils.swapArrayElement(fileExtns, preference.getString(IMPORT_VALUE_IMPORT_FILE_EXTN), 0);
        CommonUtils.swapArrayElement(fileNames, preference.getString(IMPORT_VALUE_IMPORT_FILE_NAME), 0);

        fileDialog.setFilterNames(fileNames);
        fileDialog.setFilterExtensions(fileExtns);

        String selectedFile = fileDialog.open();

        // Store the selected preference ICDM-1187
        preference.setValue(IMPORT_VALUE_IMPORT_FILE_EXTN, fileExtns[fileDialog.getFilterIndex()]);
        preference.setValue(IMPORT_VALUE_IMPORT_FILE_NAME, fileNames[fileDialog.getFilterIndex()]);

        if (selectedFile != null) {
          parseFileAndSetValuesToPage(page, selectedFile);
        }
      }
    };

    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.IMPORT_PARAM_16X16);
    importDataFromFileAction.setImageDescriptor(imageDesc);
    toolBarManager.add(importDataFromFileAction);

  }

  /**
   * @param page
   * @param selectedFile
   */
  private void parseFileAndSetValuesToPage(final ReviewResultParamListPage page, final String selectedFile) {
    try {
      // Parsing the input files selected
      CaldataFileParserHandler parserHandler = new CaldataFileParserHandler(ParserLogger.getInstance(), null);
      Map<String, CalData> calDataMap = parserHandler.getCalDataObjects(selectedFile);

      for (Entry<String, CalData> calDataEntry : calDataMap.entrySet()) {

        for (CDRResultParameter param : ReviewResultNATToolBarActionSet.this.resultData.getResultBo().getParameters()) {
          if (param.getName().equals(calDataEntry.getKey())) {
            CDRResultParameter rparam = param;
            // Set CalData retrieved from file
            page.getResultData().getImportValueMap().put(rparam, calDataEntry.getValue());
          }

        }
      }
      page.refreshNatTable();
      // ICDM-2056
      showImportedValueColumn();
    }
    catch (IcdmException exp) {
      CDMLogger.getInstance().errorDialog("Error occured while parsing the files to be imported !" + exp.getMessage(),
          exp, Activator.PLUGIN_ID);
    }
    if (page.getResultData().getImportValueMap().isEmpty()) {
      CDMLogger.getInstance().info("No matching parameters in the file !", Activator.PLUGIN_ID);
    }
    else {
      CDMLogger.getInstance().info("Data from file loaded successfully", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param page
   */
  private void showImportedValueColumn() {
    ColumnHideShowLayer columnHideShowLayer =
        this.page.getCustomFilterGridLayer().getBodyLayer().getColumnHideShowLayer();
    Integer colToShow = ReviewResultParamListPage.IMP_VALUE_COL_NUMBER;
    if (columnHideShowLayer.isColumnIndexHidden(colToShow)) {
      List<Integer> colsToShow = new ArrayList<>();
      colsToShow.add(colToShow);
      columnHideShowLayer.showColumnIndexes(colsToShow);
    }
  }

  // ICDM-1257
  /**
   * clear the imported value
   *
   * @param toolBarManager toolBarManager
   * @param page ReviewResultPage instance
   */
  public void clearImportedValueAction(final ToolBarManager toolBarManager, final ReviewResultParamListPage page) {
    final Action clearImportedValuAction = new Action("Clear all imported values") {

      @Override
      public void run() {
        page.getResultData().getImportValueMap().clear();
        page.refreshNatTable();
        page.refreshColFilters(null);
      }
    };

    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.CLEAR_PARAM_16X16);
    clearImportedValuAction.setImageDescriptor(imageDesc);
    toolBarManager.add(clearImportedValuAction);

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void readOnlyFilterAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {

    final Action readOnlyParamAction = new Action("READ only Params", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for read only parameters
    readOnlyParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.READ_ONLY_16X16));
    readOnlyParamAction.setChecked(true);
    toolBarManager.add(readOnlyParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(readOnlyParamAction, readOnlyParamAction.isChecked());


  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notReadOnlyFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    final Action notReadOnlyParamAction = new Action("Not READ only Params", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReadOnlyFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for not read only parameters
    notReadOnlyParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_READ_ONLY_16X16));
    notReadOnlyParamAction.setChecked(true);
    toolBarManager.add(notReadOnlyParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReadOnlyParamAction, notReadOnlyParamAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void dependentFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {

    final Action dependentParamAction = new Action("Parameter with dependencies", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDependentFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for dependent parameters
    dependentParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    dependentParamAction.setChecked(true);
    toolBarManager.add(dependentParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(dependentParamAction, dependentParamAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notDependentFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    final Action notDependentParamAction = new Action("Parameter without dependencies", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDependentFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for not dependent parameters
    notDependentParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    notDependentParamAction.setChecked(true);
    toolBarManager.add(notDependentParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notDependentParamAction, notDependentParamAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void blackListFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    // Filter for blacklist parameters
    final Action blackListAction = new Action("Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setBlackListFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for blacklist parameters
    blackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    blackListAction.setChecked(true);
    toolBarManager.add(blackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(blackListAction, blackListAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void nonBlackListFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomNATTable natTable) {
    // Filter for non blacklist parameters
    final Action nonBlackListAction = new Action("Non Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonBlackListFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for non blacklist parameters
    nonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    nonBlackListAction.setChecked(true);
    toolBarManager.add(nonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackListAction, nonBlackListAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   * @param natTable CustomNATTable
   */
  public void qSSDFilterAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {
    // Filter for QSSD parameters
    final Action qSSDAction = new Action("QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setqSSDFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for QSSD parameters
    qSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    qSSDAction.setChecked(true);
    toolBarManager.add(qSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(qSSDAction, qSSDAction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   * @param natTable Nat Table
   */
  public void nonQSSDFilterAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final CustomNATTable natTable) {
    // Filter for non QSSD parameters
    final Action nonQSSDAction = new Action("Non QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonQSSDFlag(isChecked());
        applyColumnFilter();
        natTable.refresh();
      }
    };
    // Set the image for non QSSD parameters
    nonQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    nonQSSDAction.setChecked(true);
    toolBarManager.add(nonQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonQSSDAction, nonQSSDAction.isChecked());

  }
}
