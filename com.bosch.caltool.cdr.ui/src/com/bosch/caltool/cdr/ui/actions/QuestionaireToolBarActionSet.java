/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.QuestionDialog;
import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireDetailsDialog;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionDetailsPage;
import com.bosch.caltool.cdr.ui.table.filters.QuestionaireEditorToolBarFilters;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Action set class for questionniare
 *
 * @author dmo5cob
 */

public class QuestionaireToolBarActionSet {

  /**
   * Instance of question details page
   */
  private final QuestionDetailsPage page;


  /**
   * Instance of grid layer
   */
  private final CustomFilterGridLayer<Question> filterGridLayer;


  private Action addAction;

  /**
   * @param page
   * @param filterGridLayer
   */
  public QuestionaireToolBarActionSet(final QuestionDetailsPage page,
      final CustomFilterGridLayer<Question> filterGridLayer) {
    this.page = page;
    this.filterGridLayer = filterGridLayer;
  }

  /**
   * This method creates add question action
   *
   * @param toolBarManager instance
   */
  public void createAddQuestionAction(final ToolBarManager toolBarManager) {

    this.addAction = new Action(CdrUIConstants.ADD_QUESTION, SWT.BUTTON1) {


      @Override
      public void run() {
        // Only a working set can be edited
        if (!(QuestionaireToolBarActionSet.this.page.getQnaireDefBo().isWorkingSet())) {
          CDMLogger.getInstance().warnDialog(CdrUIConstants.WORKING_SET_MSG, Activator.PLUGIN_ID);
        }
        else {
          // check for access rights to modify the questionniare
          if (QuestionaireToolBarActionSet.this.page.getQnaireDefBo().isModifiable()) {
            Question selectedQues = QuestionaireToolBarActionSet.this.page.getSelectedQues();
            // open add question dialog
            QuestionDialog dialog = new QuestionDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(),
                selectedQues == null ? null : selectedQues, QuestionaireToolBarActionSet.this.page.getQnaireDefBo(),
                false);
            // can create questions upto level 2

            if ((null != selectedQues) && (QuestionaireToolBarActionSet.this.page.getQnaireDefBo()
                .getQuestionLevel(selectedQues.getId()) == QuestionaireToolBarActionSet.this.page.getQnaireDefBo()
                    .getMaxLevelsAllowed())) {
              CDMLogger.getInstance().infoDialog("Cannot create questions for Level 3. Maximum allowed level reached !",
                  Activator.PLUGIN_ID);
            }
            else {
              dialog.open();
            }
            QuestionaireToolBarActionSet.this.page.refreshOutlineViewer();
          }
          else {
            CDMLogger.getInstance().warnDialog(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
          }
        }
      }
    };
    // Set the image for pidc history action
    this.addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.addAction);

  }

  /**
   * To Enable/Disable Add Question action if the question is either deleted/not deleted
   */
  public void enableOrDisableAddAction() {
    this.addAction.setEnabled(!QuestionaireToolBarActionSet.this.page.getSelectedQues().getDeletedFlag());
  }

  /**
   * This method creates edit question action
   *
   * @param toolBarManager instance
   */
  public void createEditQuestionAction(final ToolBarManager toolBarManager) {

    Action editAction = new Action(CdrUIConstants.EDIT_QUESTION, SWT.BUTTON1) {


      @Override
      public void run() {
        // check for access rights to modify the questionniare
        if (QuestionaireToolBarActionSet.this.page.getQnaireDefBo().isModifiable()) {
          if (null == QuestionaireToolBarActionSet.this.page.getSelectedQues()) {
            MessageDialogUtils.getErrorMessageDialog("Updation", "Please select a question to update");
          }
          else {
            // open edit question dialog
            QuestionDialog dialog = new QuestionDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(),
                QuestionaireToolBarActionSet.this.page.getSelectedQues(),
                QuestionaireToolBarActionSet.this.page.getQnaireDefBo(), true);
            dialog.open();
          }
          QuestionaireToolBarActionSet.this.page.refreshOutlineViewer();
        }
        else {
          CDMLogger.getInstance().warnDialog(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
        }
      }
    };
    // Set the image for pidc history action
    editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarManager.add(editAction);

  }

  /**
   * This method creates add questionaire action
   *
   * @param toolBarManager instance
   * @param qnaireDefBO the questionare version
   */
  public void createEditQuestionaireAction(final ToolBarManager toolBarManager, final QnaireDefBO qnaireDefBO) {

    Action editQuestionaireAction = new Action(CdrUIConstants.EDIT_QUESTIONAIRE, SWT.BUTTON1) {


      @Override
      public void run() {
        // Only a working set can be edited
        if (!QuestionaireToolBarActionSet.this.page.getQnaireDefBo().isWorkingSet()) {
          CDMLogger.getInstance().warnDialog(CdrUIConstants.WORKING_SET_MSG, Activator.PLUGIN_ID);
        }
        else {
          // check for access rights to modify the questionniare
          if (QuestionaireToolBarActionSet.this.page.getQnaireDefBo().isModifiable()) {
            // iCDM-1968
            // open qusetion edit dialog
            Questionnaire qnaire = QuestionaireToolBarActionSet.this.page.getQnaireDefBo().getQuestionnaire();
            QuestionnaireDetailsDialog detailsDialog = new QuestionnaireDetailsDialog(
                CommonUiUtils.getInstance().getDisplay().getActiveShell(), qnaire,
                QuestionaireToolBarActionSet.this.page.getQnaireDefBo().getWorkingPackageDetails(qnaire.getWpDivId()),
                qnaireDefBO.getQnaireVersion());
            detailsDialog.open();
          }
          else {
            CDMLogger.getInstance().warnDialog(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
          }
        }
      }
    };
    // Set the image for pidc history action
    editQuestionaireAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.COPY_16X16));
    toolBarManager.add(editQuestionaireAction);

  }

  /**
   * This method creates export questionaire action
   *
   * @param toolBarManager instance
   */
  public void createExportQuestionaireAction(final ToolBarManager toolBarManager) {

    Action editQuestionaireAction = new Action(CdrUIConstants.EXPORT_QUESTIONAIRE, SWT.BUTTON1) {


      @Override
      public void run() {
        // TO-DO
      }
    };
    // Set the image for pidc history action
    editQuestionaireAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16));
    toolBarManager.add(editQuestionaireAction);

  }

  /**
   * This method creates predefined filter action to show all headings
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void showAllHeadingsAction(final ToolBarManager toolBarManager,
      final QuestionaireEditorToolBarFilters toolBarFilters) {

    final Action showHeadingsAction = new Action("Headings", SWT.TOGGLE) {

      @Override
      public void run() {
        // if action is selected , set flag true
        toolBarFilters.setHeadingFlag(isChecked());
        // apply column filter
        applyColumnFilter();
      }
    };

    showHeadingsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HEADING_ICON_16X16));
    showHeadingsAction.setChecked(true);
    // add action to toolbar
    toolBarManager.add(showHeadingsAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(showHeadingsAction, showHeadingsAction.isChecked());
  }

  /**
   * This method creates predefined filter action to show all headings
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public final void showAllQuestionsAction(final ToolBarManager toolBarManager,
      final QuestionaireEditorToolBarFilters toolBarFilters) {

    final Action showHeadingsAction = new Action("Questions", SWT.TOGGLE) {

      @Override
      public void run() {
        // if action is selected , set flag true
        toolBarFilters.setQuestionsFlag(isChecked());
        // apply column filter
        applyColumnFilter();
      }
    };

    showHeadingsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUESTION_ICON_16X16));
    showHeadingsAction.setChecked(true);
    // add action to toolbar
    toolBarManager.add(showHeadingsAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(showHeadingsAction, showHeadingsAction.isChecked());
  }

  /**
  *
  */
  private void applyColumnFilter() {
    this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.filterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
    this.page.setStatusBarMessage(this.page.getGroupByHeaderLayer(), false);
  }

  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void showAllDeletedAction(final ToolBarManager toolBarManager,
      final QuestionaireEditorToolBarFilters toolBarFilters) {

    final Action showDeletedction = new Action("Deleted Headings and Questions", SWT.TOGGLE) {

      @Override
      public void run() {
        // if action is selected , set flag true
        toolBarFilters.setDeletedFlag(isChecked());
        // apply column filter
        applyColumnFilter();
      }
    };
    showDeletedction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETED_ITEMS_ICON_16X16));
    showDeletedction.setChecked(true);
    // add action to toolbar
    toolBarManager.add(showDeletedction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(showDeletedction, showDeletedction.isChecked());
  }

  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void showAllNotDeletedAction(final ToolBarManager toolBarManager,
      final QuestionaireEditorToolBarFilters toolBarFilters) {

    final Action showNonDeletedAction = new Action("Non-deleted Headings and Questions", SWT.TOGGLE) {

      @Override
      public void run() {
        // if action is selected , set flag true
        toolBarFilters.setNondeletedFlag(isChecked());
        // apply column filter
        applyColumnFilter();
      }
    };

    showNonDeletedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ACTIVE_ITEMS_ICON_16X16));
    showNonDeletedAction.setChecked(true);
    // add action to toolbar
    toolBarManager.add(showNonDeletedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(showNonDeletedAction, showNonDeletedAction.isChecked());
  }
}
