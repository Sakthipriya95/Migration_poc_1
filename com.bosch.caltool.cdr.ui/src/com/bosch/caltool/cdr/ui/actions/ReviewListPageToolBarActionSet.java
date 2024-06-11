/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.ReviewListPage;
import com.bosch.caltool.cdr.ui.table.filters.PIDCRwResToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * This class is for the toolbar filters of the PIDC Review results NAT table
 *
 * @author mkl2cob
 */
public class ReviewListPageToolBarActionSet {

  /**
   * CustomFilterGridLayer instance
   */
  private final CustomFilterGridLayer filterGridLayer;

  private final ReviewListPage page;

  /**
   * Constructor
   *
   * @param dataRprtFilterGridLayer CustomFilterGridLayer
   */
  public ReviewListPageToolBarActionSet(final CustomFilterGridLayer dataRprtFilterGridLayer,
      final ReviewListPage page) {
    this.filterGridLayer = dataRprtFilterGridLayer;
    this.page = page;
  }

  /**
   * method for official tool bar action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters PIDCRwResToolBarFilters
   */
  public void showOfficialTypeAction(final ToolBarManager toolBarManager,
      final PIDCRwResToolBarFilters toolBarFilters) {


    final Action officialAction = new Action("Official", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setOfficial(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    officialAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RES_MAIN_16X16));
    // initially let the toggle action be pressed
    officialAction.setChecked(true);
    toolBarManager.add(officialAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(officialAction, officialAction.isChecked());


  }


  /**
   * method for official tool bar action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters PIDCRwResToolBarFilters
   */
  public void showLinkedTypeAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {


    final Action linkedAction = new Action("Linked Result", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setLinked(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    linkedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RES_LINK_VAR_16X16));
    // initially let the toggle action be pressed
    linkedAction.setChecked(true);
    toolBarManager.add(linkedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(linkedAction, linkedAction.isChecked());


  }

  /**
   * method for official tool bar action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters PIDCRwResToolBarFilters
   */
  public void showNormalTypeAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {


    final Action normalRvwAction = new Action("Not Linked Result", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNotLinked(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    normalRvwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RES_CLOSED_16X16));
    // initially let the toggle action be pressed
    normalRvwAction.setChecked(true);
    toolBarManager.add(normalRvwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(normalRvwAction, normalRvwAction.isChecked());


  }

  /**
   * method for test tool bar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showTestTypeAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action testAction = new Action("Test", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setTest(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    testAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RESULT_TEST_16X16));
    // initially let the toggle action be pressed
    testAction.setChecked(true);
    toolBarManager.add(testAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(testAction, testAction.isChecked());

  }

  /**
   * method for start tool bar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showStartTypeAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action startAction = new Action("Start", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setStart(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    startAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RESULT_START_16X16));
    // initially let the toggle action be pressed
    startAction.setChecked(true);
    toolBarManager.add(startAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(startAction, startAction.isChecked());

  }

  /**
   * method for open toolbar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showOpenAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action openAction = new Action("Open", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setOpen(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    openAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RED_BALL_16X16));
    // initially let the toggle action be pressed
    openAction.setChecked(true);
    toolBarManager.add(openAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(openAction, openAction.isChecked());

  }

  /**
   * method for in progress toolbar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showInProgressAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action inProgressAction = new Action("In Progress", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setInProgress(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
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
   * method for closed tool bar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showClosedAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action closedAction = new Action("Closed", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setClose(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    closedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.GREEN_SMILEY_16X16));
    // initially let the toggle action be pressed
    closedAction.setChecked(true);
    toolBarManager.add(closedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(closedAction, closedAction.isChecked());

  }

  /**
   * method for 'Locked' tool bar action
   *
   * @param toolBarManager toolBar Manager
   * @param toolBarFilters toolBar Filters
   */
  // ICDM-2078
  public void showLockedAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action lockedAction = new Action("Locked", SWT.TOGGLE) {

      /**
       * Set the flag locked to toolbar filter
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setLockedRvws(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    lockedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LOCK_16X16));
    // initially let the toggle action be pressed
    lockedAction.setChecked(true);
    toolBarManager.add(lockedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(lockedAction, lockedAction.isChecked());

  }

  /**
   * method for 'Un Locked' tool bar action
   *
   * @param toolBarManager toolBar Manager
   * @param toolBarFilters toolBar Filters
   */
  // ICDM-2078
  public void showUnlockedAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action unlockedAction = new Action("Not locked", SWT.TOGGLE) {

      /**
       * Set the flag not locked to toolbar filter
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setUnlockedRvws(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    unlockedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNLOCK_16X16));
    // initially let the toggle action be pressed
    unlockedAction.setChecked(true);
    toolBarManager.add(unlockedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(unlockedAction, unlockedAction.isChecked());

  }

  /**
   * method for WP tool bar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showWPAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action wrkPckgAction = new Action(CDRConstants.CDR_SOURCE_TYPE.WORK_PACKAGE.getUIType(), SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setWrkPkg(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    wrkPckgAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WP_28X30));
    // initially let the toggle action be pressed
    wrkPckgAction.setChecked(true);
    toolBarManager.add(wrkPckgAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(wrkPckgAction, wrkPckgAction.isChecked());


  }

  /**
   * method for Group tool bar action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showGrpAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action groupAction = new Action(CDR_SOURCE_TYPE.GROUP.getUIType(), SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setGroup(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    groupAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.GROUP_GREEN_28X30));
    // initially let the toggle action be pressed
    groupAction.setChecked(true);
    toolBarManager.add(groupAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(groupAction, groupAction.isChecked());


  }

  /**
   * methd for function action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showFunAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action funAction = new Action(CDRConstants.CDR_SOURCE_TYPE.FUN_FILE.getUIType(), SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setFun(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    funAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FUNCTION_28X30));
    // initially let the toggle action be pressed
    funAction.setChecked(true);
    toolBarManager.add(funAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(funAction, funAction.isChecked());


  }

  /**
   * method for lab action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showLabAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action labAction = new Action(CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getUIType(), SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setLab(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    labAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LAB_16X16));
    // initially let the toggle action be pressed
    labAction.setChecked(true);
    toolBarManager.add(labAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(labAction, labAction.isChecked());


  }


  /**
   * method for MoniCa action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  // ICDM-2138
  public void showMonicaAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action monicaAction = new Action(CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getUIType(), SWT.TOGGLE) {

      /**
       * the action to set the MoniCa file flag
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setMonica(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };
    monicaAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MONICA_REPORT_16X16));
    // initially let the toggle action be pressed
    monicaAction.setChecked(true);
    toolBarManager.add(monicaAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(monicaAction, monicaAction.isChecked());
  }

  /**
   * method for reviewed files action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showRFAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action reviewedFileAction = new Action(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getUIType(), SWT.TOGGLE) {

      /**
       * Set the flag revied files to toolbar filter
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setReviewedFiles(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    reviewedFileAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REVIEW_DATA_16X16));
    // initially let the toggle action be pressed
    reviewedFileAction.setChecked(true);
    toolBarManager.add(reviewedFileAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(reviewedFileAction, reviewedFileAction.isChecked());


  }

  /**
   * method for not defined action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showNotDefAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action notDefinedAction = new Action(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getUIType(), SWT.TOGGLE) {

      /**
       * Set the flag not defined to toolbar filter
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNotDef(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    notDefinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_DEFINED_28X30));
    // initially let the toggle action be pressed
    notDefinedAction.setChecked(true);
    toolBarManager.add(notDefinedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notDefinedAction, notDefinedAction.isChecked());


  }

  // Task 237156
  /**
   * Method for showing compli param action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showCompliAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action compliParamAction = new Action(CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getUIType(), SWT.TOGGLE) {

      /**
       * Set the flag compli to toolbar filter
       * <p>
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setCompliRvws(isChecked());
        // fire the filter event
        CustomFilterGridLayer filterGridLyr = ReviewListPageToolBarActionSet.this.filterGridLayer;
        filterGridLyr.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        filterGridLyr.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(filterGridLyr.getSortableColumnHeaderLayer()));
      }
    };

    compliParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    // initially let the toggle action be pressed
    compliParamAction.setChecked(true);
    toolBarManager.add(compliParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(compliParamAction, compliParamAction.isChecked());
  }

  /**
   * method for latest reviews action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showLatestRvwsAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action latestAction = new Action("Latest Reviews", SWT.TOGGLE) {

      /**
       * Set the flag latest reviews to toolbar filter
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setLatestRvws(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    latestAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ACTIVE_PIDC_16X16));
    // initially let the toggle action be pressed
    latestAction.setChecked(true);
    toolBarManager.add(latestAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(latestAction, latestAction.isChecked());
  }

  /**
   * method for old reviews action
   *
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void showOldRvwsAction(final ToolBarManager toolBarManager, final PIDCRwResToolBarFilters toolBarFilters) {
    final Action oldRvwsAction = new Action("Old Reviews", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setOldRvws(isChecked());
        // fire the filter event
        ReviewListPageToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ReviewListPageToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    };

    oldRvwsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OTHER_VERSIONS_16X16));
    // initially let the toggle action be pressed
    oldRvwsAction.setChecked(true);
    toolBarManager.add(oldRvwsAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(oldRvwsAction, oldRvwsAction.isChecked());

  }


}
