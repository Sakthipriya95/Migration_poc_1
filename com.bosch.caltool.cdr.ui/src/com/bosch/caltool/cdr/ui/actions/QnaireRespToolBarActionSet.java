/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.QnaireAnswerEditDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.cdr.ui.table.filters.QnaireResponseToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author mkl2cob
 */
public class QnaireRespToolBarActionSet {


  /**
   * CustomFilterGridLayer instance
   */
  private final CustomFilterGridLayer filterGridLayer;
  /**
   * QuestionResponseSummaryPage instance
   */
  private final QnaireRespSummaryPage qsRespSummaryPage;

  private Action showHeadingAction = null;

  /**
   * Constructor
   *
   * @param qsRespSummaryPage
   */
  public QnaireRespToolBarActionSet(final QnaireRespSummaryPage qsRespSummaryPage,
      final CustomFilterGridLayer filterGridLayer) {
    this.filterGridLayer = filterGridLayer;
    this.qsRespSummaryPage = qsRespSummaryPage;
  }

  /**
   * Action to show headings only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showHeadingsAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    this.showHeadingAction = new Action("Show Headings", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setHeadingFlag(isChecked());
        applyColumnFilter();
      }
    };

    this.showHeadingAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HEADING_ICON_16X16));
    // initially let the toggle action be pressed
    this.showHeadingAction.setChecked(true);
    toolBarManager.add(this.showHeadingAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(this.showHeadingAction, this.showHeadingAction.isChecked());


  }

  /**
   * Action to show questions only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showQuestionsAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action questionsAction = new Action("Show Questions", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setQuestionsFlag(isChecked());
        applyColumnFilter();
      }
    };

    questionsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUESTION_ICON_16X16));
    // initially let the toggle action be pressed
    questionsAction.setChecked(true);
    toolBarManager.add(questionsAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(questionsAction, questionsAction.isChecked());

  }

  /**
   * Action to show finished results only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showPositiveResultsAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action positiveResultsAction = new Action("Positive Results", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setPostiveResultsFlag(isChecked());
        applyColumnFilter();
      }
    };

    positiveResultsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_16X16));
    // initially let the toggle action be pressed
    positiveResultsAction.setChecked(true);
    toolBarManager.add(positiveResultsAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(positiveResultsAction, positiveResultsAction.isChecked());

  }

  /**
   * Action to show negative results only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showNegativeResultsAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action negativeResultsAction = new Action("Negative Results", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNegativeResultsFlag(isChecked());
        applyColumnFilter();
      }
    };

    negativeResultsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NEGATIVE_ICON_16X16));
    // initially let the toggle action be pressed
    negativeResultsAction.setChecked(true);
    toolBarManager.add(negativeResultsAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(negativeResultsAction, negativeResultsAction.isChecked());

  }

  /**
   * Action to show neutral results only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showNeutralResultsAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action neutralResultsAction = new Action("Neutral Results", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNeutralResultsFlag(isChecked());
        applyColumnFilter();
      }
    };

    neutralResultsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NEUTRAL_ICON_16X16));
    // initially let the toggle action be pressed
    neutralResultsAction.setChecked(true);
    toolBarManager.add(neutralResultsAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(neutralResultsAction, neutralResultsAction.isChecked());

  }

  /**
   * Action to show to be done results only
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showToBeDoneAction(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action toBeDoneAction = new Action("Not Answered", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setNotAnsweredFlag(isChecked());
        // fire the filter event
        applyColumnFilter();
      }
    };

    toBeDoneAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    // initially let the toggle action be pressed
    toBeDoneAction.setChecked(true);
    toolBarManager.add(toBeDoneAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(toBeDoneAction, toBeDoneAction.isChecked());

  }


  /**
   * Action to show Invisible Questions
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void showInvisibleQuestions(final ToolBarManager toolBarManager,
      final QnaireResponseToolBarFilters toolBarFilters) {
    final Action showInvisibleAction = new Action("Invisible Questions", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in filter class
        toolBarFilters.setShowInvisible(isChecked());
        // fire the filter event
        applyColumnFilter();
      }
    };

    showInvisibleAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HIDE_16X16));
    // initially let the toggle action be not pressed
    showInvisibleAction.setChecked(false);
    applyColumnFilter();
    toolBarManager.add(showInvisibleAction);

    // Adding the default state to filters map
    this.qsRespSummaryPage.addToToolBarFilterMap(showInvisibleAction, showInvisibleAction.isChecked());

  }

  /**
   * Creates question edit action
   *
   * @param toolBarManager ToolBarManager
   * @param quesResp ReviewQnaireAnswer
   */
  public void createQuesEditAction(final ToolBarManager toolBarManager, final RvwQnaireAnswer quesResp) {
    final Action quesEditAction = new Action("Edit Question Response") {

      @Override
      public void run() {

        QnaireResponseEditor responseEditor =
            (QnaireResponseEditor) QnaireRespToolBarActionSet.this.qsRespSummaryPage.getEditor();

        QnaireAnswerEditDialog dialog = new QnaireAnswerEditDialog(Display.getCurrent().getActiveShell(), quesResp,
            QnaireRespToolBarActionSet.this.qsRespSummaryPage.getEditorInput().getQnaireRespEditorDataHandler(),
            QnaireRespToolBarActionSet.this.qsRespSummaryPage, responseEditor);
        dialog.open();
      }
    };

    quesEditAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    // initially let the toggle action be pressed
    quesEditAction.setEnabled(this.qsRespSummaryPage.getEditorInput().getQnaireRespEditorDataHandler().isModifiable());
    toolBarManager.add(quesEditAction);

  }

  /**
   * Applies the filter
   */
  private void applyColumnFilter() {
    QnaireRespToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    QnaireRespToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(QnaireRespToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
    QnaireRespToolBarActionSet.this.qsRespSummaryPage.setStatusBarMessage(this.qsRespSummaryPage.getGroupHeaderLayer(),
        false);
  }

  /**
   * @return the showHeadingAction
   */
  public Action getShowHeadingAction() {
    return this.showHeadingAction;
  }
}
