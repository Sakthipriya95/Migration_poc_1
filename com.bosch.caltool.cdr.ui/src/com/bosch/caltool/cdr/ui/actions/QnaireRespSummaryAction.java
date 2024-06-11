/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.QnaireAnswerEditDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CommentsDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;

/**
 * @author say8cob
 */
public class QnaireRespSummaryAction {


  /**
   *
   */
  private static final String EDIT_RESPONSE = "Edit Response";


  /**
   * Context menu opions to edit response action
   *
   * @param menuManagr for menu management
   * @param quesResp input answer
   * @param dataHandler data handler
   * @param qnaireRespSummaryPage summary page
   */
  public void setEditResponseAction(final IMenuManager menuManagr, final RvwQnaireAnswer quesResp,
      final QnaireRespEditorDataHandler dataHandler, final QnaireRespSummaryPage qnaireRespSummaryPage) {
    Action editAction = new Action() {

      @Override
      public void run() {
        QnaireResponseEditor qnaireResponseEditor = (QnaireResponseEditor) qnaireRespSummaryPage.getEditor();
        // open the edit dialog
        QnaireAnswerEditDialog dialog = new QnaireAnswerEditDialog(Display.getCurrent().getActiveShell(), quesResp,
            dataHandler, qnaireRespSummaryPage, qnaireResponseEditor);
        dialog.open();
        qnaireRespSummaryPage.refreshNatFilters();
      }
    };
    editAction.setText(EDIT_RESPONSE);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    boolean enable = dataHandler.isModifiable();
    if (dataHandler.checkHeading(quesResp)) {
      enable = false;
    }
    editAction.setEnabled(enable);
    editAction.setImageDescriptor(imageDesc);
    menuManagr.add(editAction);
  }

  /**
   * Context menu opions for Link response action
   *
   * @param menuManagr for menu management
   * @param selection question answer list
   * @param dataHandler data handler
   * @param qnaireRespSummaryPage summary page
   */
  public void setLinkContextMenuAction(final IMenuManager menuManagr, final IStructuredSelection selection,
      final QnaireRespEditorDataHandler dataHandler, final QnaireRespSummaryPage qnaireRespSummaryPage) {
    Action linkAction = new Action() {

      @Override
      public void run() {
        SortedSet<LinkData> linkDatas = new TreeSet<>();
        AddLinkDialog addLinkDialog = new AddLinkDialog(Display.getCurrent().getActiveShell(), null, false);
        addLinkDialog.open();
        linkDatas.add(addLinkDialog.getLinkData());
        updateLink(selection, linkDatas, qnaireRespSummaryPage);
      }
    };
    linkAction.setText("Add Link");
    boolean enable = dataHandler.isModifiable();
    RvwQnaireAnswer firstElement = (RvwQnaireAnswer) selection.getFirstElement();
    if (dataHandler.checkHeading(firstElement) || !dataHandler.showLinks(firstElement.getQuestionId())) {
      enable = false;
    }

    linkAction.setEnabled(enable);
    menuManagr.add(linkAction);
  }

  private void updateLink(final IStructuredSelection selection, final SortedSet<LinkData> linkDatas,
      final QnaireRespSummaryPage qnaireRespSummaryPage) {
    List<RvwQnaireAnswer> rvwQnaireAnswersList = selection.toList();
    for (RvwQnaireAnswer rvwQnaireAnswer : rvwQnaireAnswersList) {
      // Update link
      if (CommonUtils.isNotEmpty(linkDatas) && CommonUtils.isNotNull(rvwQnaireAnswer)) {
        CommonUiUtils.getInstance().createMultipleLinkService(linkDatas, rvwQnaireAnswer.getId(),
            MODEL_TYPE.RVW_QNAIRE_ANS);
      }
    }
  }

  /**
   * Context menu opions for Comment response action
   *
   * @param menuManagr for menu management
   * @param selection question answer list
   * @param dataHandler data handler
   * @param qnaireRespSummaryPage summary page
   */
  public void setCommentContextMenuAction(final IMenuManager menuManagr, final IStructuredSelection selection,
      final QnaireRespEditorDataHandler dataHandler, final QnaireRespSummaryPage qnaireRespSummaryPage) {
    Action commentAction = new Action() {

      @Override
      public void run() {
        CommentsDialog remarksDialog = new CommentsDialog(Display.getCurrent().getActiveShell(), 4000, null);
        remarksDialog.setTitleMessageText("Enter the remarks");
        remarksDialog.setRemarkLableText("Remark : ");
        remarksDialog.open();
        // 493574 - Validate Save should not occur if cancel is pressed in "add comment" context menu in response editor
        if (CommonUtils.isNotEmptyString(remarksDialog.getComments())) {
          updateComments(selection, dataHandler, qnaireRespSummaryPage, remarksDialog.getComments());
        }
      }
    };
    commentAction.setText("Add Comment");
    boolean enable = dataHandler.isModifiable();
    RvwQnaireAnswer firstElement = (RvwQnaireAnswer) selection.getFirstElement();
    if (dataHandler.checkHeading(firstElement) || !dataHandler.showRemarks(firstElement.getQuestionId())) {
      enable = false;
    }
    commentAction.setEnabled(enable);
    menuManagr.add(commentAction);
  }


  private void updateComments(final IStructuredSelection selection, final QnaireRespEditorDataHandler dataHandler,
      final QnaireRespSummaryPage qnaireRespSummaryPage, final String comments) {
    List<RvwQnaireAnswer> rvwQnaireAnswersList = selection.toList();
    for (RvwQnaireAnswer rvwQnaireAnswer : rvwQnaireAnswersList) {
      rvwQnaireAnswer.setRemark(comments);
      dataHandler.updateRvwQnaireAns(rvwQnaireAnswer);
    }
    dataHandler.updateQnaireRespVersStatus();
  }


  /**
   * To ADD Flags actions in
   *
   * @param menuManagr for menu management
   * @param selection question answer list
   * @param dataHandler data handler
   * @param qnaireRespSummaryPage summary page
   */
  public void setFlagMenuAction(final IMenuManager menuManagr, final IStructuredSelection selection,
      final QnaireRespEditorDataHandler dataHandler, final QnaireRespSummaryPage qnaireRespSummaryPage) {
    Action yesAction = new Action() {

      @Override
      public void run() {
        updateQuestions(selection, dataHandler, qnaireRespSummaryPage, QUESTION_RESP_SERIES_MEASURE.YES.getUiType());
      }
    };
    yesAction.setText(QUESTION_RESP_SERIES_MEASURE.YES.getUiType());
    boolean enable = dataHandler.isModifiable();
    RvwQnaireAnswer firstElement = (RvwQnaireAnswer) selection.getFirstElement();
    if (dataHandler.checkHeading(firstElement) || !dataHandler.showMeasurement(firstElement.getQuestionId()) ||
        !dataHandler.showSeriesMaturity(firstElement.getQuestionId())) {
      enable = false;
    }

    yesAction.setEnabled(enable);
    menuManagr.add(yesAction);

    Action noAction = new Action() {

      @Override
      public void run() {
        updateQuestions(selection, dataHandler, qnaireRespSummaryPage, QUESTION_RESP_SERIES_MEASURE.NO.getUiType());
      }
    };
    noAction.setText(QUESTION_RESP_SERIES_MEASURE.NO.getUiType());
    noAction.setEnabled(enable);
    menuManagr.add(noAction);

    Action notDefAction = new Action() {

      @Override
      public void run() {
        updateQuestions(selection, dataHandler, qnaireRespSummaryPage,
            QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType());
      }
    };
    notDefAction.setText(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType());
    notDefAction.setEnabled(enable);
    menuManagr.add(notDefAction);
  }

  private void updateQuestions(final IStructuredSelection selection, final QnaireRespEditorDataHandler dataHandler,
      final QnaireRespSummaryPage qnaireRespSummaryPage, final String actionText) {
    List<RvwQnaireAnswer> rvwQnaireAnswersList = selection.toList();
    for (RvwQnaireAnswer rvwQnaireAnswer : rvwQnaireAnswersList) {
      if (CommonUIConstants.COLUMN_INDEX_3 == qnaireRespSummaryPage.getSelectedCol()) {
        setSeriesData(actionText, rvwQnaireAnswer);
      }
      else if (CommonUIConstants.COLUMN_INDEX_4 == qnaireRespSummaryPage.getSelectedCol()) {
        setMeasurementData(actionText, rvwQnaireAnswer);
      }
      dataHandler.updateRvwQnaireAns(rvwQnaireAnswer);
    }
    dataHandler.updateQnaireRespVersStatus();
  }


  /**
   * @param action
   * @param rvwQnaireAnswer
   */
  private void setSeriesData(final String actionText, final RvwQnaireAnswer rvwQnaireAnswer) {
    if (QUESTION_RESP_SERIES_MEASURE.YES.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setSeries(QUESTION_RESP_SERIES_MEASURE.YES.getDbType());
    }
    else if (QUESTION_RESP_SERIES_MEASURE.NO.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setSeries(QUESTION_RESP_SERIES_MEASURE.NO.getDbType());
    }
    else if (QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setSeries(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType());
    }
  }

  /**
   * @param action
   * @param rvwQnaireAnswer
   */
  private void setMeasurementData(final String actionText, final RvwQnaireAnswer rvwQnaireAnswer) {
    if (QUESTION_RESP_SERIES_MEASURE.YES.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setMeasurement(QUESTION_RESP_SERIES_MEASURE.YES.getDbType());
    }
    else if (QUESTION_RESP_SERIES_MEASURE.NO.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setMeasurement(QUESTION_RESP_SERIES_MEASURE.NO.getDbType());
    }
    else if (QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType().equals(actionText)) {
      rvwQnaireAnswer.setMeasurement(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType());
    }
  }

}
