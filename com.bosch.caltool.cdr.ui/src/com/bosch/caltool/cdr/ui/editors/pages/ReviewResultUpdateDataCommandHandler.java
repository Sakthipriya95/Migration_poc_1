/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewScoreContextMenu;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.icdm.client.bo.cdr.DataReviewScoreUtil;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author dmo5cob
 */
public class ReviewResultUpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   * Check val not present msg - part 2
   */
  private static final String MSG_CHK_VAL_PART2 = "\' does not have a Checked Value, the SCORE can't be set.";

  /**
   * Check val not present msg - part 1
   */
  private static final String MSG_CHK_VAL_PART1 = "The parameter \'";
  
  /**
   * max length of size allowed for comment
   */
  private static final int MAX_LENGTH = 4000;

  /**
   * The {@link DataLayer} on which the data model updates should be executed.
   */
  private final CustomFilterGridLayer<?> gridLayer;

  private final ReviewResultEditor editor;

  // ICDM-2356
  private MessageDialog infoMessageDialog;

  private final REVIEW_TYPE reviewType;
  CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
  ReviewResultClientBO resultData;

  /**
   * @param paramFilterGridLayer The {@link DataLayer} on which the data model updates should be executed.
   * @param editor Review Result Editor
   * @param rvewType review type
   * @param reviewResultNatPage ReviewResultNatPage instance
   */
  public ReviewResultUpdateDataCommandHandler(final CustomFilterGridLayer<?> paramFilterGridLayer,
      final ReviewResultEditor editor, final REVIEW_TYPE rvewType) {
    this.gridLayer = paramFilterGridLayer;
    this.editor = editor;
    this.reviewType = rvewType;
    this.resultData = this.editor.getEditorInput().getResultData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<UpdateDataCommand> getCommandClass() {
    return UpdateDataCommand.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final UpdateDataCommand command) {
    try {

      int rowPosition = command.getRowPosition();

      int columnPosition = command.getColumnPosition();

      if (isCommentOrScoreColumn(columnPosition)) {
        final IDataProvider dataProvider = this.gridLayer.getBodyDataProvider();
        Object oldValue = dataProvider.getDataValue(columnPosition, rowPosition);
        Object newValue = command.getNewValue();

        if (CommonUtils.isNotEmptyString(oldValue.toString()) ? !oldValue.equals(newValue)
            : CommonUtils.isNotEmptyString(newValue.toString())) {
          dataProvider.setDataValue(columnPosition, rowPosition, newValue);
          this.gridLayer.fireLayerEvent(new CellVisualChangeEvent(this.gridLayer, columnPosition, rowPosition));

          Object rowObject = this.gridLayer.getBodyDataProvider().getRowObject(rowPosition);
          if (rowObject instanceof CDRResultParameter) {
            CDRResultParameter selectedParameter = (CDRResultParameter) rowObject;

            if (columnPosition == ReviewResultParamListPage.COMMENT_COL_NUMBER) {
              String cmt = String.valueOf(newValue);
              // check if the comment exceeds the allowed limit
              if (cmt.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > MAX_LENGTH) {
                newValue = truncateTextToMaxLen(cmt);
                CDMLogger.getInstance().info("The Review comment exceeds the max allowed limit of " + MAX_LENGTH +
                    " and thus the extra characters have been truncated", Activator.PLUGIN_ID);
              }
              checkAndUpdateReviewComment(oldValue, newValue, selectedParameter);
            }
            else {
              checkAndUpdateScoreColumn(oldValue, newValue, selectedParameter);
            }
          }
        }
      }
      return true;
    }
    catch (UnsupportedOperationException e) {
      CDMLogger.getInstance().errorDialog("Failed to update value to: " + command.getNewValue(), e,
          Activator.PLUGIN_ID);
      return false;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * check if the provided comment exceeds the max comment limit and truncate the extra characters if exceeding
   *
   * @param newValue
   * @return
   */
  private Object truncateTextToMaxLen(String cmt) {
    while (cmt.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > MAX_LENGTH) {
      cmt = cmt.substring(0, cmt.length() - 1);
    }
    return cmt;
  }

  /**
   * @param oldValue
   * @param newValue
   * @param selectedParameter
   * @throws ApicWebServiceException
   */
  private void checkAndUpdateScoreColumn(final Object oldValue, Object newValue,
      final CDRResultParameter selectedParameter)
      throws ApicWebServiceException {
    boolean canChangeScoreToNine = this.resultData.canChangeScoreToNine(newValue, selectedParameter);
    boolean isCompliFail = ReviewResultUpdateDataCommandHandler.this.resultData.isCompliFail(selectedParameter);
    boolean isQssdFail = ReviewResultUpdateDataCommandHandler.this.resultData.isQssdFail(selectedParameter);
    boolean notFulfilledRules =
        ReviewResultUpdateDataCommandHandler.this.resultData.notFulfilledRules(selectedParameter);
    // convert UI to db type
    newValue = newValue.toString().substring(0, 1);
    // ICDM-2671
    checkForBlackListUpdate(newValue, selectedParameter, canChangeScoreToNine, isCompliFail, isQssdFail,
        notFulfilledRules);
    List<DATA_REVIEW_SCORE> scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(this.reviewType);
    boolean isAllowedScore = false;
    boolean compliScoreChange = false;
    for (DATA_REVIEW_SCORE score : scoreEnums) {
      String dbType = score.getDbType();
      if (dbType.equals(newValue) || DataReviewScoreUtil.getInstance().getScoreDisplayExt(score).equals(newValue)) {
        isAllowedScore = true;
        break;
      }
    }
    if (!canChangeScoreToNine) {
      isAllowedScore = false;
      compliScoreChange = true;
    }
    final IStructuredSelection selection =
        (IStructuredSelection) this.editor.getReviewResultParamListPage().getSelectionProvider().getSelection();
    if ((null != selection) && !selection.isEmpty()) {
      if (isAllowedScore) {
        // updateScore method should be called in case of multi selection, so that all cases are captured
        updateScore(newValue, selectedParameter, selection);
        // Reset the ARC flag if score is changed to 0 or 8,9
        List<CDRResultParameter> paramListToResetARC = new ArrayList<>();
        paramListToResetARC.add(selectedParameter);
        new ReviewResultNATActionSet().resetARCFlagForScoreUpdate(paramListToResetARC, newValue.toString());

        List<Object> valList = new ArrayList<>();
        valList.add(oldValue);
        ReviewResultUpdateDataCommandHandler.this.editor.getReviewResultParamListPage().refreshColFilters(valList);
      }
      else {
        // Added twice since the selection was not cleared properly
        this.editor.getReviewResultParamListPage().getSelectionProvider().setSelection(StructuredSelection.EMPTY);
        this.editor.getReviewResultParamListPage().getSelectionProvider().setSelection(StructuredSelection.EMPTY);
        String infoMsg = CommonUtils.concatenate("Setting score to ", newValue, " is not allowed.");
        if (compliScoreChange) {
          infoMsg = checkAndSetInfoMsg(isCompliFail, isQssdFail, notFulfilledRules, infoMsg);
          createMsgDialogIfNew(infoMsg);
        }
      }
    }
  }

  /**
   * @param isCompliFail
   * @param isQssdFail
   * @param notFulfilledRules
   * @param infoMsg
   * @return
   */
  private String checkAndSetInfoMsg(final boolean isCompliFail, final boolean isQssdFail,
      final boolean notFulfilledRules, final String infoMsg) {
    if (isCompliFail) {
      return ApicConstants.MSG_CANNOT_CHANGE_SCORE_COMPLI_FAILURE;
    }
    else if (isQssdFail) {
      return ApicConstants.MSG_CANNOT_CHANGE_SCORE_QSSD_FAILURE;
    }
    else if (notFulfilledRules) {
      return ApicConstants.MSG_CANNOT_CHANGE_SCORE_UNFULFILLED_RULE;
    }
    return infoMsg;
  }

  /**
   * @param columnPosition
   * @return
   */
  private boolean isCommentOrScoreColumn(final int columnPosition) {
    return (columnPosition == ReviewResultParamListPage.COMMENT_COL_NUMBER) ||
        (columnPosition == ReviewResultParamListPage.SCORE_COL_NUMBER);
  }

  /**
   * @param newValue
   * @param selectedParameter
   * @param canChangeScoreToNine
   * @param notFulfilledRules
   * @param isQssdFail
   * @param isCompliFail
   * @throws ApicWebServiceException
   */
  private void checkForBlackListUpdate(final Object newValue, final CDRResultParameter selectedParameter,
      final boolean canChangeScoreToNine, final boolean isCompliFail, final boolean isQssdFail,
      final boolean notFulfilledRules)
      throws ApicWebServiceException {
    if (DATA_REVIEW_SCORE.S_9.getDbType().equals(newValue) && this.resultData.isBlackList(selectedParameter)) {
      CommonDataBO dataBo = new CommonDataBO();
      StringBuilder strBuilder = new StringBuilder();
      if (!canChangeScoreToNine) {
        ReviewResultUpdateDataCommandHandler.this.resultData.checkAndAppendFailMsg(isCompliFail, isQssdFail,
            notFulfilledRules, strBuilder);
      }
      if ((this.resultData.getCheckedValueString(selectedParameter) == null) ||
          this.resultData.getCheckedValueString(selectedParameter).isEmpty()) {
        strBuilder.append(MSG_CHK_VAL_PART1).append(selectedParameter.getName()).append(MSG_CHK_VAL_PART2);
        strBuilder.append("\n");
      }
      strBuilder.append(
          dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_INFO_SINLGE, selectedParameter.getName()));
      createMsgDialogIfNew(strBuilder.toString());
    }
  }

  /**
   * @param oldValue
   * @param newValue
   * @param selectedParameter
   */
  private void checkAndUpdateReviewComment(final Object oldValue, final Object newValue,
      final CDRResultParameter selectedParameter) {
    if (this.resultData.getResultBo().isResultLocked()) {
      createMsgDialogIfNew("Review is Locked and cannot be modified.");
    }
    else if (!this.resultData.canChangeComment(null == newValue ? "" : newValue.toString(), selectedParameter)) {
      createMsgDialogIfNew(ApicUiConstants.MSG_CANNOT_CHANGE_COMMENT);
    }
    else if (this.resultData.getResultBo().isModifiable()) {
      updateReviewComment(newValue, selectedParameter);
      List<Object> valList = new ArrayList<>();
      valList.add(oldValue);
      ReviewResultUpdateDataCommandHandler.this.editor.getReviewResultParamListPage().refreshColFilters(valList);
    }
    else {
      createMsgDialogIfNew("Insufficient privileges to do this operation!");
    }
  }

  /**
   * @param newValue
   * @param selectedParameter
   */
  private void updateReviewComment(final Object newValue, final CDRResultParameter selectedParameter) {
    try {
      List<CDRResultParameter> paramList = new ArrayList<>();
      CDRResultParameter paramClone = selectedParameter.clone();
      CommonUtils.shallowCopy(paramClone, selectedParameter);
      paramClone.setRvwComment(null == newValue ? "" : newValue.toString());
      paramList.add(paramClone);
      this.client.update(paramList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  // ICDM-2671
  /**
   * Creates message dialog for the first time and return back the thread without creating the dialog for the
   * consecutive times
   *
   * @param msg the error message to be displayed
   */
  private void createMsgDialogIfNew(final String msg) {
    if ((null != this.infoMessageDialog) && (null != this.infoMessageDialog.getShell()) &&
        !this.infoMessageDialog.getShell().isDisposed()) {
      return;
    }
    CDMLogger.getInstance().error(msg, Activator.PLUGIN_ID);
    Shell infoShell = new Shell();
    this.infoMessageDialog =
        new MessageDialog(infoShell, "Information", null, msg, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
    this.infoMessageDialog.open();
  }

  /**
   * @param newValue
   * @param selectedParameter
   * @param resultCmd
   */
  private void updateScore(final Object newValue, final CDRResultParameter selectedParameter,
      final IStructuredSelection selection) {

    String newScore = newValue.toString().substring(ApicUiConstants.COLUMN_INDEX_0, ApicUiConstants.COLUMN_INDEX_1);
    if (selection.size() > 1) {
      ReviewScoreContextMenu contextMenu = new ReviewScoreContextMenu();
      final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
      contextMenu.setScoreValue(this.editor.getReviewResultParamListPage(), newScore, paramList, null);

      // ICDM-2389
      this.editor.openLockMessageDialog();
    }
    else {
      if ((this.resultData.getCheckedValueString(selectedParameter) == null) ||
          this.resultData.getCheckedValueString(selectedParameter).isEmpty()) {

        // ICDM-2356
        // close the dialog if open already
        if ((null != this.infoMessageDialog) && (null != this.infoMessageDialog.getShell()) &&
            !this.infoMessageDialog.getShell().isDisposed()) {
          return;
        }
        String infoMsg = MSG_CHK_VAL_PART1 + selectedParameter.getName() + MSG_CHK_VAL_PART2;
        CDMLogger.getInstance().info(infoMsg, Activator.PLUGIN_ID);
        Shell infoShell = new Shell();
        this.infoMessageDialog = new MessageDialog(infoShell, "Information", null, infoMsg, MessageDialog.INFORMATION,
            new String[] { "ok" }, 0);

        this.infoMessageDialog.open();
      }
      else {
        CDRResultParameter paramClone = selectedParameter.clone();
        CommonUtils.shallowCopy(paramClone, selectedParameter);
        paramClone.setReviewScore(newScore);
        try {
          List<CDRResultParameter> paramList = new ArrayList<>();
          paramList.add(paramClone);
          this.client.update(paramList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        this.editor.openLockMessageDialog();
      }
    }
  }
}
