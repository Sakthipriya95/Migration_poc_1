/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentWorkPackagesPage;
import com.bosch.caltool.cdr.ui.table.filters.DataAssessmentWPToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author AJK2COB
 */
public class DataAssessmentWPPageToolbarActionSet {

  /**
   * Data assessment report work package filter gridLayer
   */
  private final CustomFilterGridLayer dataAssessmentWPFilterGridLayer;
  /**
   * Data assessment report work package page
   */
  private final DataAssessmentWorkPackagesPage dataAssessmentWorkPackagesPage;
  /**
   * Overall work package ready for production filter - YES
   */
  private Action overallWPProdReadyAction;
  /**
   * Overall work package ready for production filter - NO
   */
  private Action overallWPNotProdReadyAction;
  /**
   * WP finished filter - YES
   */
  private Action wpFinishedAction;
  /**
   * WP finished filter - NO
   */
  private Action wpNotFinishedAction;
  /**
   * Qnaire answered and baselined filter - YES
   */
  private Action qnaireAnsweredAndBaselinedAction;
  /**
   * Qnaire answered and baselined filter - NO
   */
  private Action qnaireNotAnsweredAndBaselinedAction;
  /**
   * Parameter reviewed filter - YES
   */
  private Action parameterReviewedAction;
  /**
   * Parameter reviewed filter - NO
   */
  private Action parameterNotReviewedAction;
  /**
   * HEX data reviews equal filter - YES
   */
  private Action hexDataReviewsEqualAction;
  /**
   * HEX data reviews equal filter - NO
   */
  private Action hexDataReviewsNotEqualAction;

  private Action respTypeRobertBoschAction;

  private Action respTypeCustomerAction;

  private Action respTypeOtherAction;

  /**
   * @param dataAssessmentWPFilterGridLayer DataAssessmentWPFilterGridLayer
   * @param dataAssessmentWorkPackagesPage DataAssessmentWorkPackagesPage
   */
  public DataAssessmentWPPageToolbarActionSet(final CustomFilterGridLayer dataAssessmentWPFilterGridLayer,
      final DataAssessmentWorkPackagesPage dataAssessmentWorkPackagesPage) {
    this.dataAssessmentWPFilterGridLayer = dataAssessmentWPFilterGridLayer;
    this.dataAssessmentWorkPackagesPage = dataAssessmentWorkPackagesPage;
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void overallWPProdReadyFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create wp ready for production filter action
    this.overallWPProdReadyAction = new Action(IMessageConstants.OVERALL_WP_READY_FOR_PRODUCTION, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setOverallWPProdReady(DataAssessmentWPPageToolbarActionSet.this.overallWPProdReadyAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during work package is ready for production selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.overallWPProdReadyAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.overallWPProdReadyAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };
    // Set the image for wp ready for production filter action
    this.overallWPProdReadyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.USED_16X16));
    this.overallWPProdReadyAction.setChecked(true);
    toolBarManager.add(this.overallWPProdReadyAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.overallWPProdReadyAction,
        this.overallWPProdReadyAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void overallWPNotProdReadyFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create wp not ready for production filter action
    this.overallWPNotProdReadyAction = new Action(IMessageConstants.OVERALL_WP_NOT_READY_FOR_PRODUCTION, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setOverallWPNotProdReady(
            DataAssessmentWPPageToolbarActionSet.this.overallWPNotProdReadyAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during work package is not ready for production selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.overallWPNotProdReadyAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.overallWPNotProdReadyAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for wp not ready for production filter action
    this.overallWPNotProdReadyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_USED_16X16));
    this.overallWPNotProdReadyAction.setChecked(true);
    toolBarManager.add(this.overallWPNotProdReadyAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.overallWPNotProdReadyAction,
        this.overallWPNotProdReadyAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void wpFinishedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create wp ready for production filter action
    this.wpFinishedAction = new Action(IMessageConstants.WP_FINISHED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWPFinished(DataAssessmentWPPageToolbarActionSet.this.wpFinishedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during work package is finished selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.wpFinishedAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.wpFinishedAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };
    // Set the image for wp ready for production filter action
    this.wpFinishedAction.setImageDescriptor(ImageManager.getImageDescriptor(
        ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.wpFinishedAction.setChecked(true);
    toolBarManager.add(this.wpFinishedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.wpFinishedAction, this.wpFinishedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void wpNotFinishedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create wp not ready for production filter action
    this.wpNotFinishedAction = new Action(IMessageConstants.WP_NOT_FINISHED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWPNotFinished(DataAssessmentWPPageToolbarActionSet.this.wpNotFinishedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during work package is not finished selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.wpNotFinishedAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.wpNotFinishedAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for wp not ready for production filter action
    this.wpNotFinishedAction.setImageDescriptor(ImageManager.getImageDescriptor(
        ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.wpNotFinishedAction.setChecked(true);
    toolBarManager.add(this.wpNotFinishedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.wpNotFinishedAction,
        this.wpNotFinishedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireAnsweredAndBaselinedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create qnaire answered and baselined filter action
    this.qnaireAnsweredAndBaselinedAction = new Action(IMessageConstants.QNAIRE_ANSWERED_AND_BASELINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQnaireAnsweredAndBaselined(
            DataAssessmentWPPageToolbarActionSet.this.qnaireAnsweredAndBaselinedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during qnaire answered and baselined selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.qnaireAnsweredAndBaselinedAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.qnaireAnsweredAndBaselinedAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };
    // Set the image for qnaire answered and baselined filter action
    this.qnaireAnsweredAndBaselinedAction.setImageDescriptor(ImageManager.getImageDescriptor(
        ImageManager.getDecoratedImage(ImageKeys.QUESTIONARE_ICON_16X16, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.qnaireAnsweredAndBaselinedAction.setChecked(true);
    toolBarManager.add(this.qnaireAnsweredAndBaselinedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.qnaireAnsweredAndBaselinedAction,
        this.qnaireAnsweredAndBaselinedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireNotAnsweredAndBaselinedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create qnaire not answered and baselined filter action
    this.qnaireNotAnsweredAndBaselinedAction =
        new Action(IMessageConstants.QNAIRE_NOT_ANSWERED_AND_BASELINED, SWT.TOGGLE) {

          @Override
          public void run() {
            toolBarFilters.setQnaireNotAnsweredAndBaselined(
                DataAssessmentWPPageToolbarActionSet.this.qnaireNotAnsweredAndBaselinedAction.isChecked());

            // add action text and its state to map, Map is used to maintain all the filter states. This will be
            // triggered
            // during qnaire not answered and baselined selections in workpackages page
            DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
                DataAssessmentWPPageToolbarActionSet.this.qnaireNotAnsweredAndBaselinedAction.getText(),
                DataAssessmentWPPageToolbarActionSet.this.qnaireNotAnsweredAndBaselinedAction.isChecked());

            DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(
                    new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                        .getSortableColumnHeaderLayer()));

            DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
          }
        };

    // Set the image for qnaire not answered and baselined filter action
    this.qnaireNotAnsweredAndBaselinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager
        .getDecoratedImage(ImageKeys.QUESTIONARE_ICON_16X16, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.qnaireNotAnsweredAndBaselinedAction.setChecked(true);
    toolBarManager.add(this.qnaireNotAnsweredAndBaselinedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.qnaireNotAnsweredAndBaselinedAction,
        this.qnaireNotAnsweredAndBaselinedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void parameterReviewedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create parameter reviewed filter action
    this.parameterReviewedAction = new Action(IMessageConstants.PARAMETER_REVIEWED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setParameterReviewed(DataAssessmentWPPageToolbarActionSet.this.parameterReviewedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during parameter reviewed selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.parameterReviewedAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.parameterReviewedAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };
    // Set the image for parameter reviewed filter action
    this.parameterReviewedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REVIEWED_28X30));
    this.parameterReviewedAction.setChecked(true);
    toolBarManager.add(this.parameterReviewedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.parameterReviewedAction,
        this.parameterReviewedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void parameterNotReviewedFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create parameter not reviewed filter action
    this.parameterNotReviewedAction = new Action(IMessageConstants.PARAMETER_NOT_REVIEWED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setParameterNotReviewed(DataAssessmentWPPageToolbarActionSet.this.parameterNotReviewedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during parameter not reviewed selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.parameterNotReviewedAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.parameterNotReviewedAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for parameter not reviewed filter action
    this.parameterNotReviewedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_REVIEWED_28X30));
    this.parameterNotReviewedAction.setChecked(true);
    toolBarManager.add(this.parameterNotReviewedAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.parameterNotReviewedAction,
        this.parameterNotReviewedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void hexDataReviewsEqualFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create hex data reviews equal filter action
    this.hexDataReviewsEqualAction = new Action(IMessageConstants.HEX_DATA_REVIEW_EQUAL, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setHEXDataReviewsEqual(DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsEqualAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during hex data equal to reviews selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsEqualAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsEqualAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };
    // Set the image for hex data reviews equal filter action
    this.hexDataReviewsEqualAction.setImageDescriptor(ImageManager.getImageDescriptor(
        ImageManager.getDecoratedImage(ImageKeys.COMPARE_EDITOR_16X16, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.hexDataReviewsEqualAction.setChecked(true);
    toolBarManager.add(this.hexDataReviewsEqualAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.hexDataReviewsEqualAction,
        this.hexDataReviewsEqualAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void hexDataReviewsNotEqualFilterAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create hex data reviews not equal filter action
    this.hexDataReviewsNotEqualAction = new Action(IMessageConstants.HEX_DATA_REVIEW_NOT_EQUAL, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setHEXDataReviewsNotEqual(
            DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsNotEqualAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during hex data not equal to reviews selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsNotEqualAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.hexDataReviewsNotEqualAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for hex data reviews not equal filter action
    this.hexDataReviewsNotEqualAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager
        .getDecoratedImage(ImageKeys.COMPARE_EDITOR_16X16, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.hexDataReviewsNotEqualAction.setChecked(true);
    toolBarManager.add(this.hexDataReviewsNotEqualAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.hexDataReviewsNotEqualAction,
        this.hexDataReviewsNotEqualAction.isChecked());
  }


  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeRobertBoschFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.respTypeRobertBoschAction = new Action(IMessageConstants.RESP_TYPE_ROBERTBOSCH, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setRespTypeRobertBosch(DataAssessmentWPPageToolbarActionSet.this.respTypeRobertBoschAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.respTypeRobertBoschAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.respTypeRobertBoschAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.respTypeRobertBoschAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ROBERT_BOSCH_RESP_16X16));
    this.respTypeRobertBoschAction.setChecked(true);
    toolBarManager.add(this.respTypeRobertBoschAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.respTypeRobertBoschAction,
        this.respTypeRobertBoschAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeCustomerFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create customer Resp type filter action
    this.respTypeCustomerAction = new Action(IMessageConstants.RESP_TYPE_CUSTOMER, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setRespTypeCustomer(DataAssessmentWPPageToolbarActionSet.this.respTypeCustomerAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as customer to reviews selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.respTypeCustomerAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.respTypeCustomerAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as customer filter action
    this.respTypeCustomerAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUSTOMER_RESP_16X16));
    this.respTypeCustomerAction.setChecked(true);
    toolBarManager.add(this.respTypeCustomerAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.respTypeCustomerAction,
        this.respTypeCustomerAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeOtherFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentWPToolBarFilters toolBarFilters) {
    // Create other Resp Type filter action
    this.respTypeOtherAction = new Action(IMessageConstants.RESP_TYPE_OTHER, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRespTypeOther(DataAssessmentWPPageToolbarActionSet.this.respTypeOtherAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as other to reviews selections in workpackages page
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getToolBarFilterStateMap().put(
            DataAssessmentWPPageToolbarActionSet.this.respTypeOtherAction.getText(),
            DataAssessmentWPPageToolbarActionSet.this.respTypeOtherAction.isChecked());

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(
                new FilterAppliedEvent(DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWPFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentWPPageToolbarActionSet.this.dataAssessmentWorkPackagesPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as other filter action
    this.respTypeOtherAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OTHER_RESP_16X16));
    this.respTypeOtherAction.setChecked(true);
    toolBarManager.add(this.respTypeOtherAction);

    // Adding the default state to filters map
    this.dataAssessmentWorkPackagesPage.addToToolBarFilterMap(this.respTypeOtherAction,
        this.respTypeOtherAction.isChecked());
  }

}
