/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.DataReviewScoreUtil;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bru2cob
 */
public class ReviewScoreContextMenu {

  /**
   * Constant to display cancelled message
   */
  private static final String UPDATE_WAS_CANCELLED = "Update was cancelled";
  /**
   * Constant to display update message
   */
  private static final String UPDATE_PARAMS_MESSAGE = "Updating review parameters. Please wait...";

  /**
   * add all the score options to menu
   *
   * @param selection selected row
   * @param menuManagr menu manager
   * @param cdrResult result object
   * @param page page
   */
  public void setReviewScoreMenu(final IStructuredSelection selection, final MenuManager menuManagr,
      final CDRReviewResult cdrResult, final ReviewResultParamListPage page) {
    IMenuManager subMenu = new MenuManager("Set Score");
    subMenu.setRemoveAllWhenShown(true);
    ReviewResultBO resultBo = ((ReviewResultEditorInput) page.getEditorInput()).getResultData().getResultBo();
    subMenu.addMenuListener(mgr -> {
      // add context menu option to all the scores available
      List<DATA_REVIEW_SCORE> scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(resultBo.getReviewType());
      for (DATA_REVIEW_SCORE score : scoreEnums) {
        mgr.add(createScoreAction(selection, score, page));
      }
    });
    // visible only for users with access rights
    subMenu.setVisible(resultBo.isModifiable());
    menuManagr.add(subMenu);

  }

  /**
   * Create score action
   *
   * @param selection selcted params
   * @param score score
   * @param cdrResult instance
   * @param page page
   * @return action
   */
  private IAction createScoreAction(final IStructuredSelection selection, final DATA_REVIEW_SCORE score,
      final ReviewResultParamListPage page) {
    Action score9Action = new Action() {

      @Override
      public void run() {
        updateScoreValueNew(selection, page, score.getDbType());
      }

    };
    score9Action.setEnabled(true);
    score9Action.setText(DataReviewScoreUtil.getInstance().getScoreDisplayExt(score));
    return score9Action;
  }

  /**
   * Set the score value based on selection
   *
   * @param selection selcted params
   * @param score score
   * @param page page
   */
  public void updateScoreValueNew(final IStructuredSelection selection, final ReviewResultParamListPage page,
      final String score) {
    // ICDM-920
    final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());

    Display.getDefault().syncExec(() -> {
      final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      try {
        dialog.run(true, true, monitor -> setScoreValue(page, score, paramList, monitor));
      }
      catch (InvocationTargetException | InterruptedException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        Thread.currentThread().interrupt();
      }

    });
    // ICDM-995
    ((ReviewResultEditor) (page.getEditor())).openLockMessageDialog();
  }


  /**
   * update the score
   *
   * @param page page
   * @param score score
   * @param paramList selcted params
   * @param monitor monitor
   */
  public void setScoreValue(final ReviewResultParamListPage page, final String score,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList, final IProgressMonitor monitor) {
    ReviewResultClientBO resultData = ((ReviewResultEditorInput) (page.getEditorInput())).getResultData();
    // if result is locked it should be allowed to modify score
    if (resultData.getResultBo().isResultLocked()) {
      CDMLogger.getInstance().errorDialog("Review is Locked and cannot be modified", Activator.PLUGIN_ID);
      return;
    }
    if (null != monitor) {
      monitor.beginTask(UPDATE_PARAMS_MESSAGE, IProgressMonitor.UNKNOWN);
    }

    boolean canChangeScoreToNine = page.getResultData().canChangeScoreToNine(
        DataReviewScoreUtil.getInstance().getScoreDisplayExt(DATA_REVIEW_SCORE.getType(score)), paramList.get(0));
    boolean isCompliFail = page.getResultData().isCompliFail(paramList.get(0));
    boolean isQssdFail = page.getResultData().isQssdFail(paramList.get(0));
    boolean notFulfilledRules = page.getResultData().notFulfilledRules(paramList.get(0));
    // check black list labels
    if ((paramList.size() == 1) && DATA_REVIEW_SCORE.S_9.getDbType().equals(score) &&
        resultData.isBlackList(paramList.get(0))) {
      CommonDataBO dataBo = new CommonDataBO();
      StringBuilder strBuilder = new StringBuilder();
      if (!canChangeScoreToNine) {
        page.getResultData().checkAndAppendFailMsg(isCompliFail, isQssdFail, notFulfilledRules, strBuilder);
      }
      try {
        strBuilder.append(
            dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_INFO_SINLGE, paramList.get(0).getName()));
        CDMLogger.getInstance().errorDialog(strBuilder.toString(), Activator.PLUGIN_ID);
        return;
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(strBuilder.toString(), Activator.PLUGIN_ID);
      }
    }
    if ((paramList.size() == 1) && !canChangeScoreToNine) {
      page.getResultData().checkAndDisplayError(isCompliFail, isQssdFail, notFulfilledRules);
      return;
    }
    List<CDRResultParameter> paramsNotUpdated = new ArrayList<>();
    // score cannot be set for params without check value
    removeParamsWithoutCheckVal(paramList, paramsNotUpdated, resultData);
    // show info is black list param is set to score 9
    checkBlackListLabels(page, score, paramList);
    List<CDRResultParameter> paramsCompli = new ArrayList<>();

    List<CDRResultParameter> compliParamList = new ArrayList<>();
    List<CDRResultParameter> qssdParamList = new ArrayList<>();
    List<CDRResultParameter> notFulfilledRulesList = new ArrayList<>();

    removeParamsWithCompliRes(paramList, paramsCompli, score, resultData, compliParamList, qssdParamList,
        notFulfilledRulesList);
    // call the update command for setting the score
    callServiceToUpdate(page, score, paramList, resultData);
    // Reset the ARC flag if score is changed to 0 or 8,9
    new ReviewResultNATActionSet().resetARCFlagForScoreUpdate(paramList, score);

    if (null != monitor) {
      monitor.done();
    }
    // the params not updated has to be displayed
    displayNotUpdatedParams(paramsNotUpdated, paramsCompli, compliParamList, qssdParamList, notFulfilledRulesList);
    if ((null != monitor) && monitor.isCanceled()) {
      CDMLogger.getInstance().info(UPDATE_WAS_CANCELLED, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param page
   * @param score
   * @param paramList
   * @param resultData
   */
  private void callServiceToUpdate(final ReviewResultParamListPage page, final String score,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList,
      final ReviewResultClientBO resultData) {
    if (!paramList.isEmpty()) {
      CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();

      List<String> oldValues = new ArrayList<>();
      List<CDRResultParameter> cloneParamList = new ArrayList<>();
      for (CDRResultParameter param : paramList) {
        oldValues.add(resultData.getScoreUIType(param));
        CDRResultParameter paramClone = param.clone();
        CommonUtils.shallowCopy(paramClone, param);
        paramClone.setReviewScore(score);
        cloneParamList.add(paramClone);

      }
      try {
        client.update(cloneParamList);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }


      // refresh the table viewer
      Display.getDefault().syncExec(() -> {
        List<Object> valList = new ArrayList<>(oldValues);
        page.refreshColFilters(valList);
      });

    }
  }

  /**
   * @param page
   * @param score
   * @param paramList
   */
  private void checkBlackListLabels(final ReviewResultParamListPage page, final String score,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList) {
    if (DATA_REVIEW_SCORE.S_9.getDbType().equals(score)) {
      displayInfoDialog(paramList, ((ReviewResultEditorInput) page.getEditorInput()).getResultData());
    }
  }

  /**
   * @param paramList
   * @param reviewResultClientBO
   */
  private void displayInfoDialog(final List<CDRResultParameter> paramList,
      final ReviewResultClientBO reviewResultClientBO) {
    StringBuilder labelNames = new StringBuilder();
    for (CDRResultParameter cdrResParam : paramList) {
      if (reviewResultClientBO.isBlackList(cdrResParam)) {
        labelNames.append(cdrResParam.getName()).append(",");
      }
    }
    String names = labelNames.toString();
    if (!names.isEmpty()) {
      CommonDataBO dataBo = new CommonDataBO();
      try {

        CDMLogger.getInstance().infoDialog(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_INFO_MULTIPLE,
            names.substring(0, names.length() - 1)), Activator.PLUGIN_ID);

      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Displays the dialog of not updated parameter names
   *
   * @param paramsNotUpdated
   * @param paramsCompli
   * @param notFulfilledRulesList
   * @param qssdParamList
   * @param compliParamList
   */
  private void displayNotUpdatedParams(final List<CDRResultParameter> paramsNotUpdated,
      final List<CDRResultParameter> paramsCompli, final List<CDRResultParameter> compliParamList,
      final List<CDRResultParameter> qssdParamList, final List<CDRResultParameter> notFulfilledRulesList) {
    StringBuilder params = new StringBuilder();
    if (!paramsNotUpdated.isEmpty()) {

      for (CDRResultParameter cdrResParam : paramsNotUpdated) {
        params.append(cdrResParam.getName()).append("\n");
      }
      CDMLogger.getInstance().infoDialog(
          "The following parameters are not updated as check value is not set :\n" + params.toString().trim(),
          Activator.PLUGIN_ID);
    }

    if (!paramsCompli.isEmpty()) {
      params = new StringBuilder();
      for (CDRResultParameter cdrResParam : paramsCompli) {
        params.append(cdrResParam.getName()).append("\n");
      }
      int paramsCompliSize = paramsCompli.size();
      int compliParamListSize = compliParamList.size();
      int qssdParamListSize = qssdParamList.size();
      int notFulfilledRulesListSize = notFulfilledRulesList.size();
      StringBuilder messageBuilder = new StringBuilder();

      if (CommonUtils.isEqual(paramsCompliSize, compliParamListSize)) {
        messageBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_COMPLI_FAILURE);
      }
      else if (CommonUtils.isEqual(paramsCompliSize, qssdParamListSize)) {
        messageBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_QSSD_FAILURE);
      }
      else if ((CommonUtils.isEqual(paramsCompliSize, notFulfilledRulesListSize)) && compliParamList.isEmpty() &&
          qssdParamList.isEmpty()) {
        messageBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_UNFULFILLED_RULE);
      }
      else {
        messageBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_MULTIPLE_RECORDS);
      }

      String dialogMessage =
          messageBuilder.append("\nThe following parameters are not updated as they do not have comments:\n")
              .append(params.toString().trim()).toString();
      CDMLogger.getInstance().infoDialog(dialogMessage, Activator.PLUGIN_ID);
    }
  }

  /**
   * Params without check value should not be updated
   *
   * @param paramList
   * @param paramsNotUpdated
   * @param score
   * @param unFullfilledRuleList
   * @param qssdParamList
   * @param compliParamList
   */
  private void removeParamsWithCompliRes(final List<CDRResultParameter> paramList,
      final List<CDRResultParameter> paramsCompli, final String score, final ReviewResultClientBO resultData,
      final List<CDRResultParameter> compliParamList, final List<CDRResultParameter> qssdParamList,
      final List<CDRResultParameter> notFulfilledRulesList) {
    for (CDRResultParameter cdrResParam : paramList) {
      if (!resultData.canChangeScoreToNine(score, cdrResParam)) {
        paramsCompli.add(cdrResParam);
      }
      if (resultData.isCompliFail(cdrResParam)) {
        compliParamList.add(cdrResParam);
      }
      if (resultData.isQssdFail(cdrResParam)) {
        qssdParamList.add(cdrResParam);
      }
      if (resultData.notFulfilledRules(cdrResParam)) {
        notFulfilledRulesList.add(cdrResParam);
      }
    }
    paramList.removeAll(paramsCompli);
  }

  /**
   * Params without check value should not be updated
   *
   * @param paramList
   * @param paramsNotUpdated
   * @param score
   */
  private void removeParamsWithoutCheckVal(final List<CDRResultParameter> paramList,
      final List<CDRResultParameter> paramsNotUpdated, final ReviewResultClientBO resultData) {
    for (CDRResultParameter cdrResParam : paramList) {
      if ((resultData.getCheckedValueString(cdrResParam) == null) ||
          resultData.getCheckedValueString(cdrResParam).isEmpty()) {
        paramsNotUpdated.add(cdrResParam);
      }

    }

    paramList.removeAll(paramsNotUpdated);
  }

}
