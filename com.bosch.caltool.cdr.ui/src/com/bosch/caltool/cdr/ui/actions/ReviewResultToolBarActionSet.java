/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamDetPage;
import com.bosch.caltool.cdr.ui.table.filters.ReviewResultToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * @author mga1cob
 */
// ICDM-550
public class ReviewResultToolBarActionSet {

  /**
   * constant for import file extn
   */
  protected static final String IMPORT_VALUE_IMPORT_FILE_EXTN = "importValImportImportFileExtn";
  /**
   * constant for import file name
   */
  protected static final String IMPORT_VALUE_IMPORT_FILE_NAME = "importValImportImportFileName";
  /**
   * Defines the status line manager
   */
  private final IStatusLineManager statusLineManager;

  private final ReviewResultParamDetPage page;

  /**
   * @param statusLineManager IStatusLineManager
   */
  public ReviewResultToolBarActionSet(final IStatusLineManager statusLineManager, final ReviewResultParamDetPage page) {
    this.statusLineManager = statusLineManager;
    this.page = page;
  }

  /**
   * This method creates toolbar action for showing only parameters whose results are reviewed
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   * @param viewer GridTableViewer
   */
  public final void showReviewed(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final Viewer viewer) {
    final Action reviewedAction = new Action("Reviewed:Yes", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }
        toolBarFilters.setReviewedFlag(isChecked());
        viewer.refresh();
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
   * @param viewer GridTableViewer
   */
  public final void showNotReviewed(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    final Action notReviewedAction = new Action("Reviewed:No", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNotReviewedFlag(isChecked());


        viewer.refresh();
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
   * This method creates toolbar action for showing only parameters having review result as OK
   *
   * @param toolBarManager ToolbarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   * @param viewer GridTableViewer
   */
  public final void showOk(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final Viewer viewer) {
    final Action okAction = new Action("Result:Ok", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setOkFlag(isChecked());


        viewer.refresh();
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
   * @param viewer GridTableViewer
   */
  public final void showResultNotOk(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    final Action notOkAction = new Action("Result:NotOk", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNotOkFlag(isChecked());

        viewer.refresh();

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
   * @param viewer GridTableViewer
   */
  public final void showResultUndefined(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    final Action undefAction = new Action("Result:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setUndefinedFlag(isChecked());

        viewer.refresh();
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
   * This method creates curve filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public final void curveFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter For curve Value
    final Action curveAction = new Action("Curve", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setCurveFlag(isChecked());

        viewer.refresh();
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
   * @param viewer instance
   */

  public final void mapFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter For Map Value
    final Action mapAction = new Action("Map", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setMapFlag(isChecked());


        viewer.refresh();
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
   * @param viewer instance
   */
  public final void valueFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for Value
    final Action valueAction = new Action("Value", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setValueFlag(isChecked());

        viewer.refresh();
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
   * @param viewer instance
   */
  public final void asciiFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter For ascii
    final Action asciiAction = new Action("Ascii", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setAsciiFlag(isChecked());


        viewer.refresh();
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
   * @param viewer instance
   */
  public final void valueBlockFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter For Value Block
    final Action valueBlockAction = new Action("Value Block", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setValueBlkFlag(isChecked());


        viewer.refresh();
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
   */
  public void complianceFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for compliance parameters
    final Action complianceAction = new Action("Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }


        toolBarFilters.setComplianceFlag(isChecked());


        viewer.refresh();
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
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for non compliance parameters
    final Action nonComplianceAction = new Action("Non Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNonComplianceFlag(isChecked());


        viewer.refresh();
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
   * @param viewer instance
   */
  public final void axisPointFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for Axis Point
    final Action axisPointAction = new Action("Axis Points", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setAxisFlag(isChecked());


        viewer.refresh();
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
   * @param viewer viewer
   */
  public final void screwFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for Screw class
    final Action screwAction = new Action("Class:Screw", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setScrewFlag(isChecked());
        viewer.refresh();
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
   * @param viewer viewer
   */
  public final void nailFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for Nail class
    final Action nailAction = new Action("Class:Nail", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNailFlag(isChecked());

        viewer.refresh();
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
   * @param viewer viewer
   */
  public final void rivetFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for Rivet class
    final Action rivetAction = new Action("Class:Rivet", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setRivetFlag(isChecked());

        viewer.refresh();
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
   * @param viewer viewer
   */
  public final void undefinedClassFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    // Filter for undefined class
    final Action noClassAction = new Action("Class:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNoClassFlag(isChecked());

        viewer.refresh();
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
   * This method creates manual filter action
   *
   * @param toolBarManager toolBarManager
   * @param viewer viewer
   */
  public final void manualFilterAction(final ToolBarManager toolBarManager, final Viewer viewer) {
    // Filter For Ready for series Manual
    final Action manualAction = new Action("Ready for series:NO", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        viewer.refresh();
      }
    };
    // Set the image for Ready for series Manual
    manualAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MANUAL_16X16));
    manualAction.setChecked(true);
    toolBarManager.add(manualAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(manualAction, manualAction.isChecked());

  }

  /**
   * This method creates automatic filter action
   *
   * @param toolBarManager toolBarManager
   * @param viewer viewer
   */
  public final void automaticFilterAction(final ToolBarManager toolBarManager, final Viewer viewer) {
    // Filter For Ready for series Automatic
    final Action automaticAction = new Action("Ready for series:Automatic", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        viewer.refresh();
      }
    };
    // Set the image for Ready for series Automatic
    automaticAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.AUTOMATIC_16X16));
    automaticAction.setChecked(true);
    toolBarManager.add(automaticAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(automaticAction, automaticAction.isChecked());

  }


  /**
   * // Filter For the Undefined - Review Type Icdm-654
   *
   * @param toolBarManager toolBarManager
   * @param viewer viewer
   */
  public void reviewUnDefFilterAction(final ToolBarManager toolBarManager, final Viewer viewer) {
    final Action undefAction = new Action("Ready for series:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }
        viewer.refresh();
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
   * Filter For the parameter with change marker Icdm-807
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer viewer
   */
  public void hasChangeMarkerAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    final Action chngMarkAction = new Action("Delta Review : Changes", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setChangeMarkFlag(isChecked());

        viewer.refresh();


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
   * @param viewer viewer
   */
  public void hasNoChangeMarkerAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final Viewer viewer) {
    final Action noChngMarkAction = new Action("Delta Review :No Changes", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNoChangeMarkFlag(isChecked());

        viewer.refresh();
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
   * @param viewer viewer
   */
  public void hasHistoryAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final GridTableViewer viewer) {
    final Action histAction = new Action("Check Value History:Yes", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setHistoryFlag(isChecked());

        viewer.refresh();
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
   * @param viewer viewer
   */
  public void hasNoHistoryAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final GridTableViewer viewer) {
    final Action noHistAction = new Action("Check Value History:No", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }

        toolBarFilters.setNoHistoryFlag(isChecked());

        viewer.refresh();
      }
    };
    noHistAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_NO_PARAM_HIST_16X16));
    noHistAction.setEnabled(true);
    noHistAction.setChecked(true);
    toolBarManager.add(noHistAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noHistAction, noHistAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param paramTableViewer
   */
  public void blackListFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomGridTableViewer viewer) {
    final Action blackListAction = new Action("Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }


        toolBarFilters.setBlackListFlag(isChecked());


        viewer.refresh();
      }
    };
    // Set the image for compliance parameters
    blackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    blackListAction.setChecked(true);
    toolBarManager.add(blackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(blackListAction, blackListAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param paramTableViewer
   */
  public void nonBlackListFilterAction(final ToolBarManager toolBarManager,
      final ReviewResultToolBarFilters toolBarFilters, final CustomGridTableViewer viewer) {
    // Filter for non compliance parameters
    final Action nonBlackListAction = new Action("Non Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }


        toolBarFilters.setNonBlackListFlag(isChecked());

        viewer.refresh();
      }
    };
    // Set the image for non compliance parameters
    nonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    nonBlackListAction.setChecked(true);
    toolBarManager.add(nonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackListAction, nonBlackListAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewResultToolBarFilters
   * @param viewer CustomGridTableViewer
   */
  public void qSSDFilterAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final CustomGridTableViewer viewer) {
    // Filter for qSSD parameters
    final Action qSSDAction = new Action("QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }


        toolBarFilters.setqSSDFlag(isChecked());


        viewer.refresh();
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
   * @param viewer CustomGridTableViewer
   */
  public void nonQSSDFilterAction(final ToolBarManager toolBarManager, final ReviewResultToolBarFilters toolBarFilters,
      final CustomGridTableViewer viewer) {
    // Filter for non qSSD parameters
    final Action nonQSSDAction = new Action("Non QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        if (viewer instanceof CustomGridTableViewer) {
          final CustomGridTableViewer customTableViewer = viewer;
          customTableViewer.setStatusLineManager(ReviewResultToolBarActionSet.this.statusLineManager);
        }


        toolBarFilters.setNonQSSDFlag(isChecked());

        viewer.refresh();
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
