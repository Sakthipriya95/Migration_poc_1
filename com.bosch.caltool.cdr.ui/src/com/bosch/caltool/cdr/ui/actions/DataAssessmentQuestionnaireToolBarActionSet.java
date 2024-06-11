/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentQuestionnaireResultsPage;
import com.bosch.caltool.cdr.ui.table.filters.DataAssessmentQnaireToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author say8cob
 */
public class DataAssessmentQuestionnaireToolBarActionSet {

  /**
   * Data assessment report questionnaire filter gridLayer
   */
  private final CustomFilterGridLayer dataAssessmentQnaireFilterGridLayer;


  /**
   * Data assessment report questionnaire page
   */
  private final DataAssessmentQuestionnaireResultsPage dataAssessmentQuestionnaireResultsPage;

  private Action respTypeRobertBoschAction;

  private Action respTypeCustomerAction;

  private Action respTypeOtherAction;

  private Action qnaireReadyForProdAction;

  private Action qnaireNotReadyForProdAction;

  private Action qnaireBaselinedAction;

  private Action qnaireNotBaselinedAction;

  /**
   * @param dataAssessmentQnaireFilterGridLayer CustomFilterGridLayer
   * @param dataAssessmentQuestionnaireResultsPage DataAssessmentQuestionnaireResultsPage
   */
  public DataAssessmentQuestionnaireToolBarActionSet(final CustomFilterGridLayer dataAssessmentQnaireFilterGridLayer,
      final DataAssessmentQuestionnaireResultsPage dataAssessmentQuestionnaireResultsPage) {
    this.dataAssessmentQnaireFilterGridLayer = dataAssessmentQnaireFilterGridLayer;
    this.dataAssessmentQuestionnaireResultsPage = dataAssessmentQuestionnaireResultsPage;
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireBaselinedFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.qnaireBaselinedAction = new Action(IMessageConstants.QNAIRE_BASELINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setQnaireBaselined(DataAssessmentQuestionnaireToolBarActionSet.this.qnaireBaselinedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.qnaireBaselinedAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.qnaireBaselinedAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.qnaireBaselinedAction.setImageDescriptor(ImageManager.getImageDescriptor(
        ImageManager.getDecoratedImage(ImageKeys.QUESTIONARE_ICON_16X16, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.qnaireBaselinedAction.setChecked(true);
    toolBarManager.add(this.qnaireBaselinedAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.qnaireBaselinedAction,
        this.qnaireBaselinedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireNotBaseLinedFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.qnaireNotBaselinedAction = new Action(IMessageConstants.QNAIRE_NOT_BASELINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQnaireNotBaselined(
            DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotBaselinedAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotBaselinedAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotBaselinedAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.qnaireNotBaselinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageManager
        .getDecoratedImage(ImageKeys.QUESTIONARE_ICON_16X16, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT)));
    this.qnaireNotBaselinedAction.setChecked(true);
    toolBarManager.add(this.qnaireNotBaselinedAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.qnaireNotBaselinedAction,
        this.qnaireNotBaselinedAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireReadyForProdFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.qnaireReadyForProdAction = new Action(IMessageConstants.QNAIRE_READY_FOR_PROD, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQnaireReadyForProdFinished(
            DataAssessmentQuestionnaireToolBarActionSet.this.qnaireReadyForProdAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.qnaireReadyForProdAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.qnaireReadyForProdAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.qnaireReadyForProdAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.USED_16X16));
    this.qnaireReadyForProdAction.setChecked(true);
    toolBarManager.add(this.qnaireReadyForProdAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.qnaireReadyForProdAction,
        this.qnaireReadyForProdAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void qnaireNotReadyForProdFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.qnaireNotReadyForProdAction = new Action(IMessageConstants.QNAIRE_NOT_READY_FOR_PROD, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQnaireNotReadyForProdFinished(
            DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotReadyForProdAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotReadyForProdAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.qnaireNotReadyForProdAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.qnaireNotReadyForProdAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_USED_16X16));
    this.qnaireNotReadyForProdAction.setChecked(true);
    toolBarManager.add(this.qnaireNotReadyForProdAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.qnaireNotReadyForProdAction,
        this.qnaireNotReadyForProdAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeRobertBoschFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create Robert Bosch Resp Type
    this.respTypeRobertBoschAction = new Action(IMessageConstants.RESP_TYPE_ROBERTBOSCH, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRespTypeRobertBosch(
            DataAssessmentQuestionnaireToolBarActionSet.this.respTypeRobertBoschAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as robert bosch to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.respTypeRobertBoschAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.respTypeRobertBoschAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as robert bosch filter action
    this.respTypeRobertBoschAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ROBERT_BOSCH_RESP_16X16));
    this.respTypeRobertBoschAction.setChecked(true);
    toolBarManager.add(this.respTypeRobertBoschAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.respTypeRobertBoschAction,
        this.respTypeRobertBoschAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeCustomerFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create customer Resp type filter action
    this.respTypeCustomerAction = new Action(IMessageConstants.RESP_TYPE_CUSTOMER, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setRespTypeCustomer(DataAssessmentQuestionnaireToolBarActionSet.this.respTypeCustomerAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as customer to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.respTypeCustomerAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.respTypeCustomerAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as customer filter action
    this.respTypeCustomerAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUSTOMER_RESP_16X16));
    this.respTypeCustomerAction.setChecked(true);
    toolBarManager.add(this.respTypeCustomerAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.respTypeCustomerAction,
        this.respTypeCustomerAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ToolBarFilters
   */
  public void respTypeOtherFilerAction(final ToolBarManager toolBarManager,
      final DataAssessmentQnaireToolBarFilters toolBarFilters) {
    // Create other Resp Type filter action
    this.respTypeOtherAction = new Action(IMessageConstants.RESP_TYPE_OTHER, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setRespTypeOther(DataAssessmentQuestionnaireToolBarActionSet.this.respTypeOtherAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be
        // triggered
        // during resp type as other to reviews selections in workpackages page
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage
            .getToolBarFilterStateMap()
            .put(DataAssessmentQuestionnaireToolBarActionSet.this.respTypeOtherAction.getText(),
                DataAssessmentQuestionnaireToolBarActionSet.this.respTypeOtherAction.isChecked());

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
            .getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQnaireFilterGridLayer
                    .getSortableColumnHeaderLayer()));

        DataAssessmentQuestionnaireToolBarActionSet.this.dataAssessmentQuestionnaireResultsPage.getNatTable().refresh();
      }
    };

    // Set the image for resp type as other filter action
    this.respTypeOtherAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OTHER_RESP_16X16));
    this.respTypeOtherAction.setChecked(true);
    toolBarManager.add(this.respTypeOtherAction);

    // Adding the default state to filters map
    this.dataAssessmentQuestionnaireResultsPage.addToToolBarFilterMap(this.respTypeOtherAction,
        this.respTypeOtherAction.isChecked());
  }

}
