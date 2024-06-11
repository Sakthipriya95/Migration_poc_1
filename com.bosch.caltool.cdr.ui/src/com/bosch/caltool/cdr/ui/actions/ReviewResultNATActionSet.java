/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.CommentDialog;
import com.bosch.caltool.cdr.ui.dialogs.ReviewCompleteDialog;
import com.bosch.caltool.cdr.ui.dialogs.SecondaryResultConsDialog;
import com.bosch.caltool.cdr.ui.dialogs.TakeOverCdfxRemarksDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoProviderClient;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.utils.ParamTypeColor;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.ruleseditor.actions.OpenRulesEditorAction;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * ICDM-521 - Code Refactor New Action Set for the Review Params
 *
 * @author rgo7cob
 */
public class ReviewResultNATActionSet {

  /**
   *
   */
  private static final String RVW_CMT_VALIDATION_MSG =
      "The following parameters are not updated as they have result as Compli/Qssd :\n";
  /**
   *
   */
  private static final String ARC_RELEASE_FAILED_MSG =
      "The following parameters are not ARC Release Confirmed as they have either Score 0,8 or 9 :\n";
  /**
   *
   */
  private static final int MAX_COMMENT_LENGTH = 4000;
  /**
   *
   */
  private static final String REVIEW_LOCKED_CANNOT_BE_MODIFIED = "Review is Locked and cannot be modified.";
  /**
   * Constant to display cancelled message
   */
  private static final String UPDATE_WAS_CANCELLED = "Update was cancelled";
  /**
   * Constant to display update message
   */
  private static final String UPDATE_PARAMS_MESSAGE = "Updating review parameters. Please wait...";
  /**
   * list size 1
   */
  private static final int SIZE_ONE = 1;
  /**
   * Return code for Ok pressed
   */
  private static final int CODE_FOR_OK = 0;

  /**
   * string setting to mark checked for rvw parameter
   */
  // Task 236308
  private static final String SET_CHECKED = "Set as Checked in Secondary Result";

  /**
   * string setting to reset for rvw parameter
   */
  // Task 236308
  private static final String RESET_STATE = "Reset the State in Secondary Result";

  // ICDM-1280
  /**
   * This method opens the rules editor along with the create rule dialog filled with rule details for the selected
   * param
   *
   * @param menuManagr MenuManager instance
   * @param selectedObj param selected
   * @param function Function
   * @param fromResultEditor from Result Editor
   * @param ruleSet RuleSet
   */
  public void addCreateRuleAction(final MenuManager menuManagr, final Object selectedObj, final Function function,
      final boolean fromResultEditor, final RuleSet ruleSet) {

    final Action openEditorAction =
        new OpenRulesEditorAction(null, selectedObj, fromResultEditor, function, true, ruleSet);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    openEditorAction.setText("Create Rule using Check Value");
    menuManagr.add(openEditorAction);
  }


  /**
   * @param selection as IStructuredSelection
   * @param menuManagr as MenuManager
   * @param page as ReviewResultParamListPage
   */
  public void setARCReleaseNecessary(final IStructuredSelection selection, final MenuManager menuManagr,
      final ReviewResultParamListPage page) {
    final Action rvwCmntAction = new Action() {

      @Override
      public void run() {
        // check if the result can be modified
        if (page.getResultData().getResultBo().isResultLocked()) {
          CDMLogger.getInstance().errorDialog(REVIEW_LOCKED_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
          return;
        }
        @SuppressWarnings("unchecked")
        final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
        List<CDRResultParameter> failedParamList = new ArrayList<>();
        if (paramList.size() > SIZE_ONE) {
          validateRvwParamForARCRelease(page, paramList, failedParamList);
        }
        CDRResultParameter rvwParam = null;
        CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();

        if (CommonUtils.isEqual(paramList.size(), SIZE_ONE)) {
          rvwParam = paramList.get(0);
        }
        if (CommonUtils.isNotEmpty(paramList)) {
          updateARCForRvwParameter(page, paramList, failedParamList, rvwParam, client);
        }
        else {
          displayErrorMsg(failedParamList, ARC_RELEASE_FAILED_MSG);
        }
      }

      /**
       * @param page
       * @param paramList
       * @param failedParamList
       */
      private void validateRvwParamForARCRelease(final ReviewResultParamListPage page,
          final List<CDRResultParameter> paramList, final List<CDRResultParameter> failedParamList) {
        // remove parameters which are compli/qssd fail
        for (CDRResultParameter cdrResParam : paramList) {
          DATA_REVIEW_SCORE score = page.getResultData().getScore(cdrResParam);
          if (checkIfARCNotApplicable(score)) {
            failedParamList.add(cdrResParam);
          }
        }
        paramList.removeAll(failedParamList);
      }
    };
    rvwCmntAction.setText("ARC Release Confirmed");
    menuManagr.add(rvwCmntAction);
    rvwCmntAction.setEnabled(page.getResultData().getResultBo().isModifiable());
  }

  /**
   * @param page
   * @param paramList
   * @param failedParamList
   * @param rvwParam
   * @param client
   */
  private void updateARCForRvwParameter(final ReviewResultParamListPage page, final List<CDRResultParameter> paramList,
      final List<CDRResultParameter> failedParamList, final CDRResultParameter rvwParam,
      final CDRResultParameterServiceClient client) {
    if (paramList.size() == SIZE_ONE) {
      DATA_REVIEW_SCORE score = page.getResultData().getScore(rvwParam);
      if (checkIfARCNotApplicable(score)) {
        failedParamList.add(rvwParam);
      }
      else {
        try {
          List<CDRResultParameter> clonedParamList = new ArrayList<>();
          CDRResultParameter paramClone = paramList.get(0).clone();
          CommonUtils.shallowCopy(paramClone, paramList.get(0));
          clonedParamList.add(paramClone);
          paramClone.setArcReleasedFlag(true);
          client.update(clonedParamList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
    else {
      multiParamUpdateForARC(paramList, true, page);
    }
    if (CommonUtils.isNotEmpty(failedParamList)) {
      displayErrorMsg(failedParamList, ARC_RELEASE_FAILED_MSG);
    }
  }


  /**
   * @param score
   * @return
   */
  private boolean checkIfARCNotApplicable(final DATA_REVIEW_SCORE score) {
    return CommonUtils.isEqual(DATA_REVIEW_SCORE.S_8.getDbType(), score.getDbType()) ||
        CommonUtils.isEqual(DATA_REVIEW_SCORE.S_9.getDbType(), score.getDbType()) ||
        CommonUtils.isEqual(DATA_REVIEW_SCORE.S_0.getDbType(), score.getDbType());
  }

  /**
   * @param score
   * @return
   */
  private boolean checkIfARCResetRequired(final String score) {
    return CommonUtils.isEqual(DATA_REVIEW_SCORE.S_8.getDbType(), score) ||
        CommonUtils.isEqual(DATA_REVIEW_SCORE.S_9.getDbType(), score) ||
        CommonUtils.isEqual(DATA_REVIEW_SCORE.S_0.getDbType(), score);
  }

  private List<CDRResultParameter> getARCReleasedParams(final List<CDRResultParameter> paramList) {
    List<CDRResultParameter> arcParamList = new ArrayList<>();
    paramList.forEach(param -> {
      if (param.getArcReleasedFlag()) {
        arcParamList.add(param);
      }
    });
    return arcParamList;
  }

  /**
   * ICDM-826 Context menu to set comments
   *
   * @param selection IStructuredSelection
   * @param menuManagr MenuManager
   * @param cdrReviewResult CDRResult
   * @param page nat page
   */
  public void setReviewComments(final IStructuredSelection selection, final MenuManager menuManagr,
      final ReviewResultParamListPage page) {
    final Action rvwCmntAction = rvwCmtAction(selection, page);
    rvwCmntAction.setText(IMessageConstants.SET_REVIEW_COMMENTS);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMMENT_16X16);
    rvwCmntAction.setImageDescriptor(imageDesc);
    menuManagr.add(rvwCmntAction);
    rvwCmntAction.setEnabled(page.getResultData().getResultBo().isModifiable());
  }


  /**
   * @param selection
   * @param page
   * @return
   */
  private Action rvwCmtAction(final IStructuredSelection selection, final ReviewResultParamListPage page) {
    return new Action() {

      @Override
      public void run() {
        // check if the result can be modified
        if (page.getResultData().getResultBo().isResultLocked()) {
          CDMLogger.getInstance().errorDialog(REVIEW_LOCKED_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
          return;
        }
        @SuppressWarnings("unchecked")
        final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
        List<CDRResultParameter> failedParamList = new ArrayList<>();
        if (paramList.size() > SIZE_ONE) {

          // remove parameters which are compli/qssd fail
          for (CDRResultParameter cdrResParam : paramList) {
            if (page.getResultData().isCompliFail(cdrResParam) || page.getResultData().isQssdFail(cdrResParam)) {
              failedParamList.add(cdrResParam);
            }
          }
          paramList.removeAll(failedParamList);
        }
        CDRResultParameter rvwParam = null;
        CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();

        if (paramList.size() == SIZE_ONE) {
          rvwParam = paramList.get(0);
        }
        updatingReviewComment(page, paramList, failedParamList, rvwParam, client);

      }
    };
  }

  /**
   * Reset ARC flag if Review score is set to 0 or 8,9
   *
   * @param paramList review parameter List
   * @param score Review Score
   */
  public void resetARCFlagForScoreUpdate(final List<CDRResultParameter> paramList, final String score) {
    if (checkIfARCResetRequired(score)) {
      List<CDRResultParameter> arcReleasedParams = getARCReleasedParams(paramList);
      if (!arcReleasedParams.isEmpty()) {
        resetARCForReviewParams(arcReleasedParams);
      }
    }
  }

  /**
   * @param paramList paramList
   * @param page ReviewResultParamListPage
   */
  private void resetARCForReviewParams(final List<CDRResultParameter> paramList) {
    List<CDRResultParameter> clonedParamList = new ArrayList<>();
    for (CDRResultParameter param : paramList) {
      CDRResultParameter paramClone = param.clone();
      CommonUtils.shallowCopy(paramClone, param);
      paramClone.setArcReleasedFlag(false);
      clonedParamList.add(paramClone);
    }
    updateRvwParameters(clonedParamList);
  }


  /**
   * @param paramList paramList
   * @param arcFlag arcFlag (true or false)
   * @param page ReviewResultParamListPage
   */
  public void multiParamUpdateForARC(final List<CDRResultParameter> paramList, final boolean arcFlag,
      final ReviewResultParamListPage page) {

    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        if (page.getResultData().getResultBo().isModifiable()) {
          monitor.beginTask(UPDATE_PARAMS_MESSAGE, IProgressMonitor.UNKNOWN);
          List<CDRResultParameter> clonedParamList = new ArrayList<>();

          for (CDRResultParameter param : paramList) {
            CDRResultParameter paramClone = param.clone();
            CommonUtils.shallowCopy(paramClone, param);
            paramClone.setArcReleasedFlag(arcFlag);
            clonedParamList.add(paramClone);
          }

          updateRvwParameters(clonedParamList);

          monitor.done();
          if (monitor.isCanceled()) {
            CDMLogger.getInstance().info(UPDATE_WAS_CANCELLED, Activator.PLUGIN_ID);
          }
        }
        else {
          CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        }

      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * @param page
   * @param paramList
   * @param failedParamList
   * @param rvwParam
   * @param client
   */
  private void updatingReviewComment(final ReviewResultParamListPage page, final List<CDRResultParameter> paramList,
      final List<CDRResultParameter> failedParamList, final CDRResultParameter rvwParam,
      final CDRResultParameterServiceClient client) {
    if (!paramList.isEmpty()) {
      CommentDialog commentDialog = new CommentDialog(Display.getCurrent().getActiveShell(), rvwParam, paramList,
          ((ReviewResultEditorInput) (page.getEditorInput())).getResultData());
      int isOkPressed = commentDialog.open();
      if (isOkPressed == CODE_FOR_OK) {

        if (commentDialog.getReviewComments() != null) {
          final String comments = commentDialog.getReviewComments();
          String oldValue = paramList.get(0).getRvwComment();
          // ICDM-920
          updateSingleAndMultiRvwParam(page, paramList, rvwParam, client, comments, oldValue);
        }
        else {
          importReviewComments(commentDialog, paramList, page);
        }

        displayErrorMsg(failedParamList, RVW_CMT_VALIDATION_MSG);
      }
    }
    else {
      displayErrorMsg(failedParamList, RVW_CMT_VALIDATION_MSG);
    }
  }


  /**
   * @param page
   * @param paramList
   * @param rvwParam
   * @param client
   * @param comments
   * @param oldValue
   */
  private void updateSingleAndMultiRvwParam(final ReviewResultParamListPage page,
      final List<CDRResultParameter> paramList, final CDRResultParameter rvwParam,
      final CDRResultParameterServiceClient client, final String comments, final String oldValue) {
    if (paramList.size() == SIZE_ONE) {
      if (!page.getResultData().canChangeComment(null == comments ? "" : comments, rvwParam)) {
        CDMLogger.getInstance().infoDialog(ApicUiConstants.MSG_CANNOT_CHANGE_COMMENT, Activator.PLUGIN_ID);
      }
      else {
        try {
          List<CDRResultParameter> clonedParamList = new ArrayList<>();
          CDRResultParameter paramClone = paramList.get(0).clone();
          CommonUtils.shallowCopy(paramClone, paramList.get(0));
          clonedParamList.add(paramClone);
          paramClone.setRvwComment(comments);
          client.update(clonedParamList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        List<Object> valList = new ArrayList<>();
        valList.add(oldValue);
        page.refreshColFilters(valList);
      }
    }
    else {
      multiParamUpdate(paramList, comments, page, true);
    }
  }


  /**
   * @param paramList cdr parameter list
   * @param comments Comments
   * @param page
   * @param cdrReviewResult
   * @param resultTabViewer GridTableViewer
   */
  private void multiParamUpdate(final List<CDRResultParameter> paramList, final String comments,
      final ReviewResultParamListPage page, final boolean setComments) {

    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        if (page.getResultData().getResultBo().isModifiable()) {
          monitor.beginTask(UPDATE_PARAMS_MESSAGE, IProgressMonitor.UNKNOWN);
          List<String> oldValues = new ArrayList<>();
          List<CDRResultParameter> clonedParamList = new ArrayList<>();

          for (CDRResultParameter param : paramList) {
            oldValues.add(param.getRvwComment());
            CDRResultParameter paramClone = param.clone();
            CommonUtils.shallowCopy(paramClone, param);
            if (setComments) {
              paramClone.setRvwComment(comments);
            }
            clonedParamList.add(paramClone);
          }

          updateRvwParameters(clonedParamList);

          // refresh the table viewer
          Display.getDefault().syncExec(() -> {
            List<Object> valList = new ArrayList<>(oldValues);
            page.refreshColFilters(valList);
          });

          monitor.done();
          if (monitor.isCanceled()) {
            CDMLogger.getInstance().info(UPDATE_WAS_CANCELLED, Activator.PLUGIN_ID);
          }
        }
        else {
          CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        }

      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
  }


  /**
   * @param clonedParamList
   */
  private void updateRvwParameters(final List<CDRResultParameter> clonedParamList) {
    try {
      new CDRResultParameterServiceClient().update(clonedParamList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  // Start of Template for comments
  /**
   * add all the Comments Template options to menu
   *
   * @param selection selected row
   * @param menuManagr menu manager
   * @param cdrResult result object
   * @param page page
   */
  public void setCommentsTemplateMenu(final IStructuredSelection selection, final MenuManager menuManagr,
      final ReviewResultParamListPage page) {
    IMenuManager subMenu = new MenuManager(IMessageConstants.TEMPLATES_FOR_COMMENTS);
    subMenu.setRemoveAllWhenShown(true);


    ReviewResultBO resultBo = ((ReviewResultEditorInput) page.getEditorInput()).getResultData().getResultBo();
    subMenu.addMenuListener(mgr -> {
      // add context menu option to all the scores available
      try {
        for (RvwCommentTemplate rvwCommentTemplate : new CommonDataBO().getAllRvwCommentTemplate()) {
          mgr.add(createCommentAction(selection, rvwCommentTemplate.getName(), page));
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    });
    // visible only for users with access rights
    subMenu.setVisible(resultBo.isModifiable());
    menuManagr.add(subMenu);

  }

  /**
   * Create comments action
   *
   * @param selection selcted params
   * @param comments
   * @param page page
   * @return action
   */
  private IAction createCommentAction(final IStructuredSelection selection, final String comment,
      final ReviewResultParamListPage page) {
    Action commentTemplateAction = new Action() {

      @Override
      public void run() {
        updateCommentsValueNew(selection, page, comment);
      }

    };
    // to trim comments template length over than 200 characters
    if (comment.length() > 200) {
      commentTemplateAction.setText(comment.substring(0, 200) + "...");
    }
    else {
      commentTemplateAction.setText(comment);
    }
    commentTemplateAction.setEnabled(true);
    return commentTemplateAction;
  }

  /**
   * Set the score value based on selection
   *
   * @param selection selcted params
   * @param score score
   * @param page page
   */
  public void updateCommentsValueNew(final IStructuredSelection selection, final ReviewResultParamListPage page,
      final String comment) {
    // ICDM-920
    final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
    setCommentsValue(page, comment, paramList);
    ((ReviewResultEditor) (page.getEditor())).openLockMessageDialog();
  }


  private void setCommentsValue(final ReviewResultParamListPage page, final String comment,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList) {
    // check if the result can be modified
    if (page.getResultData().getResultBo().isResultLocked()) {
      CDMLogger.getInstance().errorDialog(REVIEW_LOCKED_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
      return;
    }
    List<CDRResultParameter> failedParamList = new ArrayList<>();
    if (paramList.size() > SIZE_ONE) {

      // remove parameters which are compli/qssd fail
      for (CDRResultParameter cdrResParam : paramList) {
        if (page.getResultData().isCompliFail(cdrResParam) || page.getResultData().isQssdFail(cdrResParam)) {
          failedParamList.add(cdrResParam);
        }
      }
      paramList.removeAll(failedParamList);
    }
    CDRResultParameter rvwParam = null;

    if (paramList.size() == SIZE_ONE) {
      rvwParam = paramList.get(0);
    }
    if (!paramList.isEmpty()) {
      updateRvwParamComment(page, comment, paramList, rvwParam);
      displayErrorMsg(failedParamList, RVW_CMT_VALIDATION_MSG);
    }
    else {
      displayErrorMsg(failedParamList,
          "The following parameters cannot be updated as they have result as Compli/Qssd :\n");
    }
  }


  /**
   * @param page
   * @param comment
   * @param paramList
   * @param rvwParam
   */
  private void updateRvwParamComment(final ReviewResultParamListPage page, final String comment,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList, final CDRResultParameter rvwParam) {
    if (comment != null) {
      String oldValue = paramList.get(0).getRvwComment();
      // ICDM-920
      if (paramList.size() == SIZE_ONE) {
        singleParamCommentUpdate(page, comment, paramList, rvwParam, oldValue);
      }
      else {
        multiParamUpdate(paramList, comment, page, true);
      }
    }
  }


  /**
   * @param page
   * @param comment
   * @param paramList
   * @param rvwParam
   * @param oldValue
   */
  private void singleParamCommentUpdate(final ReviewResultParamListPage page, final String comment,
      final List<com.bosch.caltool.icdm.model.cdr.CDRResultParameter> paramList, final CDRResultParameter rvwParam,
      final String oldValue) {
    if (!page.getResultData().canChangeComment(CommonUtils.isNull(comment) ? "" : comment, rvwParam)) {
      CDMLogger.getInstance().infoDialog(ApicUiConstants.MSG_CANNOT_CHANGE_COMMENT, Activator.PLUGIN_ID);
    }
    else {
      try {
        CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
        List<CDRResultParameter> clonedParamList = new ArrayList<>();
        CDRResultParameter paramClone = paramList.get(0).clone();
        CommonUtils.shallowCopy(paramClone, paramList.get(0));
        clonedParamList.add(paramClone);
        paramClone.setRvwComment(comment);
        client.update(clonedParamList);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      List<Object> valList = new ArrayList<>();
      valList.add(oldValue);
      page.refreshColFilters(valList);
    }
  }

  // end of Template for comments

  /**
   * ICdm-1056 add to scratch pad method.
   *
   * @param transferTypes transferTypes
   * @param textField Text
   * @param appendField Fields name to be appended
   * @param ruleInfoSection editRuleDialog
   */
  public void dragToScratchPad(final Transfer[] transferTypes, final StyledText textField, final String appendField,
      final RuleInfoSection ruleInfoSection) {

    final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    final DragSource source = new DragSource(textField, operations);
    source.setTransfer(transferTypes);
    source.addDragListener(new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        final String value = textField.getText();
        if (("").equals(value) || ("n.a.").equalsIgnoreCase(value) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {

        CalData calData;
        // iCDM-2071
        calData = com.bosch.caltool.icdm.common.util.CalDataUtil
            .createCopyWithoutHistory(ruleInfoSection.getRefValCalDataObj(), CDMLogger.getInstance());
        if (null != calData) {
          CalData calDataObject;
          try {
            calDataObject = CalDataUtil.getCalDataHistoryDetails(new CurrentUserBO().getUserName(), calData,
                appendField, ruleInfoSection.getSelectedCdrRule(), null);
            event.data = calDataObject;
            SeriesStatisticsInfo calDataProvider;

            calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
            calDataProvider.setDataSetName(ruleInfoSection.getSelectedCdrRule().getDependenciesForDisplay());
            final StructuredSelection struSelection = new StructuredSelection(calDataProvider);
            LocalSelectionTransfer.getTransfer().setSelection(struSelection);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
          }

        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        ruleInfoSection.getRefValueTxt().setFocus();
      }
    });
  }


  /**
   * ICDM-2304
   *
   * @param menuManagr MenuManager
   * @param firstElement selected obj
   * @param resultEditor ReviewResultEditor
   */
  public void showSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final ReviewResultEditor resultEditor) {
    final Action showGraph = new Action() {

      @Override
      public void run() {
        if (firstElement instanceof CDRResultParameter) {
          // iCDM-1408
          showTableGraphAction((CDRResultParameter) firstElement, resultEditor, true);
        }
      }
    };
    showGraph.setText("Show in Synchronized Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    menuManagr.add(showGraph);
  }


  /**
   * Shows the consolidated secondary review result details
   *
   * @param menuManagr menuMgr
   * @param firstElement selected obj
   * @param resultData Review Result Client BO
   */
  public void showSecondaryResultDetails(final MenuManager menuManagr, final Object firstElement,
      final ReviewResultClientBO resultData) {
    final Action showGraph = new Action() {

      @Override
      public void run() {
        // Task 231287
        if (firstElement instanceof CDRResultParameter) {
          CDRResultParameter cDRResultParameter = (CDRResultParameter) firstElement;

          // Review 238978
          if ((resultData.getSecondaryResParams(cDRResultParameter) == null) ||
              resultData.getSecondaryResParams(cDRResultParameter).isEmpty()) {
            MessageDialogUtils.getWarningMessageDialog("Secondary Result of " + cDRResultParameter.getName(),
                CDRConstants.NO_SECONDARY_RESULT);
          }
          else {
            Map<Long, RvwParametersSecondary> secondaryResParams = resultData.getSecondaryResParams(cDRResultParameter);
            Collection<RvwParametersSecondary> values = secondaryResParams.values();
            SortedSet<RvwParametersSecondary> secondaryParamSet = new TreeSet<>();
            secondaryParamSet.addAll(values);
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            SecondaryResultConsDialog dialog =
                new SecondaryResultConsDialog(shell, secondaryParamSet, cDRResultParameter.getName(), resultData);
            dialog.open();
          }
        }
      }
    };
    showGraph.setText("Show Secondary Result");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.ICDON_SECONDARY_RESULT);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    menuManagr.add(showGraph);
  }

  // Task 236308
  /**
   * To update the single secondary review result
   *
   * @param menuManagr menuMgr
   * @param firstElement selected obj
   * @param page the rvw result page
   */
  public void updateSecondaryResult(final MenuManager menuManagr, final Object firstElement,
      final ReviewResultParamListPage page) {
    if (firstElement instanceof CDRResultParameter) {
      String checkOrResetText;
      Action updateSecResAction;
      ImageKeys imagekeys;
      CDRResultParameter cDRResultParameter = (CDRResultParameter) firstElement;
      boolean canBeResetFlag = RESULT_FLAG.CHECKED == page.getResultData().getSecondaryResStateEnum(cDRResultParameter);
      if (canBeResetFlag) {
        checkOrResetText = RESET_STATE;
        imagekeys = ImageKeys.RESET_16X16;
      }
      else {
        checkOrResetText = SET_CHECKED;
        imagekeys = ImageKeys.CHECKED_16X16;
      }
      updateSecResAction = getActionForCheckedOrReset(cDRResultParameter, page, canBeResetFlag);
      updateSecResAction.setText(checkOrResetText);
      final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(imagekeys);

      // context menu should be enabled, only if the parameter is considered for secondary rule set
      boolean enableCheckResetContextMenu = (page.getResultData().getSecondaryResParams(cDRResultParameter) != null) &&
          !page.getResultData().getSecondaryResParams(cDRResultParameter).isEmpty() &&
          page.getResultData().getResultBo().isModifiable();
      updateSecResAction.setEnabled(enableCheckResetContextMenu);
      updateSecResAction.setImageDescriptor(imageDesc);
      menuManagr.add(updateSecResAction);
    }
  }

  // Task 236308
  /**
   * Action for single secondary update
   *
   * @param cDRResultParameter
   * @param page
   * @param canBeResetFlag
   * @return
   */
  private Action getActionForCheckedOrReset(final CDRResultParameter cDRResultParameter,
      final ReviewResultParamListPage page, final boolean canBeResetFlag) {
    return new Action() {

      @Override
      public void run() {

        if (((ReviewResultEditor) (page.getEditor())).getEditorInput().getResultData().getResultBo().isResultLocked()) {
          CDMLogger.getInstance().errorDialog("Review is Locked and cannot be modified", Activator.PLUGIN_ID);
          return;
        }

        try {
          List<String> oldValues = new ArrayList<>();

          oldValues.add(page.getResultData().getCustomSecondaryResult(cDRResultParameter));
          List<CDRResultParameter> paramList = new ArrayList<>();
          CDRResultParameter paramClone = cDRResultParameter.clone();
          CommonUtils.shallowCopy(paramClone, cDRResultParameter);
          if (canBeResetFlag) {
            paramClone.setSecondaryResultState(RESULT_FLAG.NOT_REVIEWED.getDbType());
          }
          else {
            paramClone.setSecondaryResultState(RESULT_FLAG.CHECKED.getDbType());
          }
          paramList.add(paramClone);
          CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
          client.update(paramList);
          // refresh the table viewer
          Display.getDefault().syncExec(() -> {
            List<Object> valList = new ArrayList<>(oldValues);
            page.refreshColFilters(valList);
          });
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        page.refreshNatTable();
      }
    };
  }

  // Task 236308
  /**
   * Action for multi secondary update
   *
   * @param menuManagr menuManagr
   * @param selection selection
   * @param reviewResultPage reviewResultPage
   */
  public void updateMultiSecdyResAsChecked(final MenuManager menuManagr, final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultPage) {
    final Action actionForMultiSecondaryRes = getActionForMultiSecondaryResUpdate(selection, reviewResultPage, true);
    actionForMultiSecondaryRes.setText(SET_CHECKED);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.CHECKED_16X16);
    actionForMultiSecondaryRes.setImageDescriptor(imageDesc);

    // context menu should be enabled, only if the parameter is considered for secondary rule set
    actionForMultiSecondaryRes.setEnabled(hasSecondaryParam(selection, reviewResultPage) &&
        reviewResultPage.getResultData().getResultBo().isModifiable());
    menuManagr.add(actionForMultiSecondaryRes);
  }

  /**
   * Checks if atleast one cdr result parameter has secondary result parameter
   *
   * @param paramList the selection of multi cdr result parameter
   * @return true if the atleast one secondary param available for given parameter
   */
  private boolean hasSecondaryParam(final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultPage) {
    @SuppressWarnings("unchecked")
    final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());

    for (CDRResultParameter cdrRsltParametr : paramList) {
      ReviewResultClientBO resultData = ((ReviewResultEditorInput) (reviewResultPage.getEditorInput())).getResultData();
      if ((resultData.getSecondaryResParams(cdrRsltParametr) != null) &&
          !resultData.getSecondaryResParams(cdrRsltParametr).isEmpty()) {
        return true;
      }
    }
    return false;
  }

  // Task 236308
  /**
   * Action for multi secondary update
   *
   * @param menuManagr menuManagr
   * @param selection selection
   * @param reviewResultPage reviewResultPage
   */
  public void updateMultiSecdyResAsReset(final MenuManager menuManagr, final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultPage) {

    final Action actionForMultiSecondaryRes = getActionForMultiSecondaryResUpdate(selection, reviewResultPage, false);
    actionForMultiSecondaryRes.setText(RESET_STATE);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RESET_16X16);
    actionForMultiSecondaryRes.setImageDescriptor(imageDesc);

    // context menu should be enabled, only if the parameter is considered for secondary rule set
    actionForMultiSecondaryRes.setEnabled(hasSecondaryParam(selection, reviewResultPage) &&
        reviewResultPage.getResultData().getResultBo().isModifiable());
    menuManagr.add(actionForMultiSecondaryRes);
  }


  // Task 236308
  /**
   * Action for multi secondary update
   *
   * @param selection selection
   * @param reviewResultPage reviewResultPage
   * @return action for multi secondary update
   */
  private Action getActionForMultiSecondaryResUpdate(final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultPage, final boolean canBeChecked) {
    return new Action() {

      @Override
      public void run() {
        @SuppressWarnings("unchecked")
        final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());

        Display.getDefault().syncExec(() -> {
          final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
          try {
            dialog.run(true, true, monitor -> {
              if (((ReviewResultEditor) (reviewResultPage.getEditor())).getEditorInput().getResultData().getResultBo()
                  .isResultLocked()) {
                CDMLogger.getInstance().errorDialog("Review is Locked and cannot be modified", Activator.PLUGIN_ID);
                return;
              }
              monitor.beginTask(UPDATE_PARAMS_MESSAGE, IProgressMonitor.UNKNOWN);
              updateSecResultForRvwParam(reviewResultPage, canBeChecked, paramList);
              monitor.done();
              if (monitor.isCanceled()) {
                CDMLogger.getInstance().info(UPDATE_WAS_CANCELLED, Activator.PLUGIN_ID);
              }

            });
          }
          catch (InvocationTargetException | InterruptedException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            Thread.currentThread().interrupt();
          }

        });
      }
    };
  }


  /**
   * @param reviewResultPage
   * @param canBeChecked
   * @param paramList
   */
  private void updateSecResultForRvwParam(final ReviewResultParamListPage reviewResultPage, final boolean canBeChecked,
      final List<CDRResultParameter> paramList) {
    if (!paramList.isEmpty()) {

      CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();

      List<String> oldValues = new ArrayList<>();
      List<CDRResultParameter> cloneParamList = new ArrayList<>();
      for (CDRResultParameter param : paramList) {
        oldValues.add(reviewResultPage.getResultData().getCustomSecondaryResult(param));
        CDRResultParameter paramClone = param.clone();
        CommonUtils.shallowCopy(paramClone, param);
        if (canBeChecked) {
          paramClone.setSecondaryResultState(RESULT_FLAG.CHECKED.getDbType());
        }
        else {
          paramClone.setSecondaryResultState(RESULT_FLAG.NOT_REVIEWED.getDbType());
        }
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
        reviewResultPage.refreshColFilters(valList);
      });
    }
  }

  /**
   * ICDM-2304
   *
   * @param menuManagr MenuManager
   * @param firstElement selected obj
   * @param resultEditor ReviewResultEditor
   */
  public void showUnSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final ReviewResultEditor resultEditor) {
    final Action showGraph = new Action() {

      @Override
      public void run() {
        if (firstElement instanceof CDRResultParameter) {
          // iCDM-1408
          showTableGraphAction((CDRResultParameter) firstElement, resultEditor, false);
        }
      }
    };
    showGraph.setText("Show in Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    menuManagr.add(showGraph);
  }

  /**
   * @param firstElement CDRResultParameter
   * @param resultEditor ReviewResultEditor
   * @param isSynchDialogNeeded boolean
   */
  // iCDM-1408
  public void showTableGraphAction(final CDRResultParameter firstElement, final ReviewResultEditor resultEditor,
      final boolean isSynchDialogNeeded) {
    CDRResultParameter param = firstElement;
    // ICDM-1320
    ConcurrentMap<String, CalData> calDataMap = new ConcurrentHashMap<>();
    ReviewResultClientBO resultData = resultEditor.getEditorInput().getResultData();
    fillCalDataMap(param, calDataMap, resultData);

    // iCDM-1408
    // case-1 : no dialog had opened, opening thro' context menu
    // case-2 : Dialog already opened position, opening thro' context menu->Table/graph view right mouse click
    // case-3 : Dialog already opened position, opening thro' param selection left mouse click

    // case-4 : if individual dialog needed, then new dialog will be opened
    // iCDM-1408
    CalDataViewerDialog calDataViewerDialog;
    boolean isUnSynchDialogNeeded = !isSynchDialogNeeded;
    if (isUnSynchDialogNeeded || (null == resultEditor.getSynchCalDataViewerDialog()) ||
        (null == resultEditor.getSynchCalDataViewerDialog().getShell())) {
      // case-1 covers
      // case-4 covers
      calDataViewerDialog = new CalDataViewerDialog(Display.getCurrent().getActiveShell(), calDataMap, param.getName(),
          resultEditor.getEditorInput().getName(), isSynchDialogNeeded, true);
      // ICDM-1657
      Map<String, Integer> colorMap = getConstructColorCodeMap();
      calDataViewerDialog.setColorMap(colorMap);

      if (isSynchDialogNeeded) {
        // storing the state of cal_data_viewer_dialog insance in rvwResult editor
        resultEditor.setSynchCalDataViewerDialog(calDataViewerDialog);
      }
      else {
        resultEditor.setUnSynchCalDataViewerDialog(calDataViewerDialog);
      }

      // TO-DO from a2l editor

      /*
       * Following lines is used Temporarily : Instead use the new Model : A2LFile
       */
      PidcA2lFileExt pidcA2lFileExt;
      A2LFileInfo a2lFileContents = null;
      try {
        pidcA2lFileExt =
            new PidcA2lServiceClient().getPidcA2LFileDetails(resultData.getResultBo().getPidcA2l().getId());
        a2lFileContents = new A2LFileInfoProviderClient().fetchA2LFileInfo(pidcA2lFileExt.getA2lFile());
      }
      catch (IcdmException | ApicWebServiceException excp) {
        CDMLogger.getInstance().error("Error in fetching a2l file", excp, Activator.PLUGIN_ID);
      }


      // Task 234466 include T/G viewer V1.9.0
      if (null != a2lFileContents) {
        calDataViewerDialog.setCharacteristicsMap(a2lFileContents.getAllModulesLabels());
      }
      // set the decimal preference for the table/graph 
      resultEditor.setDecimalPref();
      calDataViewerDialog.open();
    }
    else {
      // this condition will be true if the CalDataViewerDialog is already opened for one row(ie, param) in Rvw Result
      // Editor and we are clicking on some other row
      // case-2 & case-3 covers
      if (!resultEditor.getSynchCalDataViewerDialog().getParamName().equals(param.getName())) {
        calDataViewerDialog = resultEditor.getSynchCalDataViewerDialog();
        // iCDM-1408
        calDataViewerDialog.getTableGraphComposite().clearTableGraph();
        calDataViewerDialog.setParamName(param.getName());
        calDataViewerDialog.setCalDataMap(calDataMap);

        // ICDM-1657
        Map<String, Integer> colorMap = getConstructColorCodeMap();
        calDataViewerDialog.setColorMap(colorMap);

        // storing the state of cal_data_viewer_dialog insance in rvwResult editor
        resultEditor.setSynchCalDataViewerDialog(calDataViewerDialog);

        calDataViewerDialog.populateData(calDataViewerDialog.getArea());

      }
    }
  }

  /**
   * @param resultEditor {@link ReviewResultEditor}
   * @param param {@link CDRResultParameter}
   * @param calDataMap {@link CalData} Map
   * @param resultData {@link ReviewResultClientBO}
   */
  private void fillCalDataMap(final CDRResultParameter param, final ConcurrentMap<String, CalData> calDataMap,
      final ReviewResultClientBO resultData) {
    CalData checkedValueObj = resultData.getCheckedValueObj(param);
    CalData refValueObj = resultData.getRefValueObj(param);
    CalData calDatObj = resultData.getImportValueMap().get(param);

    // constructing cal data map
    if (checkedValueObj != null) {
      calDataMap.put(CommonUIConstants.CHECK_VALUE, checkedValueObj);
    }
    if (refValueObj != null) {
      calDataMap.put(CommonUIConstants.REF_VALUE, refValueObj);
    }
    if (null != calDatObj) {
      calDataMap.put("Imported CalData Value", calDatObj);
    }
    // ICDM-1320
    CalData parCheckedValue = resultData.getParentCheckedVal(param);
    if ((null != parCheckedValue) && resultData.isCheckedValueChanged(param)) {
      calDataMap.put(CommonUIConstants.TXT_PARENT_CHECK_VALUE, parCheckedValue);
    }
    CalData parRefValue = resultData.getParentRefVal(param);
    if ((null != parRefValue) && resultData.isRefValChanged(param)) {
      calDataMap.put(CommonUIConstants.TXT_PARENT_REF_VALUE, parRefValue);
    }
  }

  /**
   * @return
   */
  private Map<String, Integer> getConstructColorCodeMap() {
    Map<String, Integer> colorMap = new HashMap<>();
    colorMap.put(CommonUIConstants.REF_VALUE, ParamTypeColor.REF_VALUE.getColorCode());
    colorMap.put(CommonUIConstants.TXT_PARENT_REF_VALUE, ParamTypeColor.PARENT_REF_VALUE.getColorCode());
    colorMap.put(CommonUIConstants.CHECK_VALUE, ParamTypeColor.CHECK_VALUE.getColorCode());
    colorMap.put(CommonUIConstants.TXT_PARENT_CHECK_VALUE, ParamTypeColor.PARENT_CHECK_VALUE.getColorCode());
    return colorMap;
  }

  /**
   * ICDM-1678
   *
   * @param menuManager MenuManager
   * @param resultPage ReviewResultNatPage
   * @return Action
   */
  public Action createCopyAction(final MenuManager menuManager, final ReviewResultParamListPage resultPage) {

    Action copyCommentsAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CopyDataToClipboardCommand copyCommand = new CopyDataToClipboardCommand("\t",
            System.getProperty("line.separator"), resultPage.getNatTable().getConfigRegistry());
        resultPage.getNatTable().doCommand(copyCommand);
      }
    };

    copyCommentsAction.setText(ApicUiConstants.COPY);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_16X16);
    copyCommentsAction.setImageDescriptor(imageDesc);
    menuManager.add(copyCommentsAction);

    return copyCommentsAction;

  }

  /**
   * @param manager manager
   * @param selection IStructuredSelection
   * @param reviewResultNatPage ReviewResultNatPage
   */
  public void pasteCommentsActionNew(final IMenuManager manager, final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultNatPage) {
    Action pasteCommentsAction = new Action() {

      @Override
      public void run() {
        if (((ReviewResultEditor) (reviewResultNatPage.getEditor())).getEditorInput().getResultData().getResultBo()
            .isModifiable()) {
          final ReviewResultNATActionSet reviewActionSet = new ReviewResultNATActionSet();
          reviewActionSet.setCommentsToResult(selection, reviewResultNatPage);
          reviewResultNatPage.getNatTable().redraw();
        }
        else {
          CDMLogger.getInstance().error("Insufficient priviledge to edit !", Activator.PLUGIN_ID);
          return;
        }
      }
    };
    pasteCommentsAction.setText(ApicUiConstants.PASTE);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteCommentsAction.setImageDescriptor(imageDesc);
    manager.add(pasteCommentsAction);
  }


  /**
   * @param selection IStructuredSelection
   * @param reviewResultNatPage ReviewResultNatPage
   * @param cdrReviewResult CDRResult
   */
  public void setCommentsToResult(final IStructuredSelection selection,
      final ReviewResultParamListPage reviewResultNatPage) {
    // return if the result is locked
    if (((ReviewResultEditor) (reviewResultNatPage.getEditor())).getEditorInput().getResultData().getResultBo()
        .isResultLocked()) {
      CDMLogger.getInstance().errorDialog(REVIEW_LOCKED_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
      return;
    }
    int nbrCopiedCells = 0;
    LinkedList<String> commentsList = new LinkedList<>();
    // take contends from the internal clipboard
    ILayerCell[][] copiedCells = reviewResultNatPage.getNatTable().getInternalCellClipboard().getCopiedCells();
    LinkedHashMap<CDRResultParameter, String> mapOfComments = new LinkedHashMap<>();
    @SuppressWarnings("unchecked")
    ArrayList<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
    int nbrPasteCells = paramList.size();
    nbrCopiedCells = getListOfComments(nbrCopiedCells, commentsList, copiedCells);
    if ((nbrCopiedCells != 0) && (nbrPasteCells != 0)) {
      Iterator<String> it1 = commentsList.iterator();
      Iterator<CDRResultParameter> it2 = paramList.iterator();
      if (nbrCopiedCells == nbrPasteCells) {
        setCommentsMap(mapOfComments, it1, it2);
      }
      else if (nbrCopiedCells > nbrPasteCells) {
        setCommentsMap(reviewResultNatPage, nbrCopiedCells, mapOfComments, paramList, it1);
      }
      else {
        setComments(nbrCopiedCells, commentsList, mapOfComments, it1, it2);
      }
      if (!CommonUtils.isNotEmpty(mapOfComments)) {
        CDMLogger.getInstance().errorDialog("No comments to paste !", Activator.PLUGIN_ID);
        return;
      }
      invokeCommandToSaveComments(mapOfComments);

    }
  }


  /**
   * @param nbrCopiedCells
   * @param commentsList
   * @param copiedCells
   * @return
   */
  private int getListOfComments(final int nbrCopiedCells, final LinkedList<String> commentsList,
      final ILayerCell[][] copiedCells) {

    int nbrCopiedCellsRet = nbrCopiedCells;

    if (null != copiedCells) {
      nbrCopiedCellsRet = copiedCells.length;
      for (ILayerCell[] iLayerCells : copiedCells) {
        for (ILayerCell iLayerCell : iLayerCells) {
          if (null != iLayerCell.getDataValue()) {
            commentsList.add(iLayerCell.getDataValue().toString());
          }
        }
      }
    }
    else {
      commentsList.add(getLinkFromClipboard());
      nbrCopiedCellsRet = 1;
    }
    return nbrCopiedCellsRet;
  }

  /**
   * Find iCDM link from clipboard
   *
   * @return iCDM link from clipboard as plain text
   */
  private String getLinkFromClipboard() {
    return CommonUiUtils.getContentsFromClipBoard();
  }


  /**
   * @param reviewResultNatPage
   * @param nbrCopiedCells
   * @param mapOfComments
   * @param paramList
   * @param it1
   */
  private void setCommentsMap(final ReviewResultParamListPage reviewResultNatPage, final int nbrCopiedCells,
      final LinkedHashMap<CDRResultParameter, String> mapOfComments, final ArrayList<CDRResultParameter> paramList,
      final Iterator<String> it1) {
    int iCounter = 1;
    while ((iCounter > 0) && (iCounter <= nbrCopiedCells)) {
      int row = reviewResultNatPage.getSelectedRowPostn() + iCounter;
      CDRResultParameter rowObj =
          (CDRResultParameter) reviewResultNatPage.getCustomFilterGridLayer().getBodyDataProvider().getRowObject(row);
      paramList.add(rowObj);
      iCounter++;
    }
    Iterator<CDRResultParameter> it2Added = paramList.iterator();
    setCommentsMap(mapOfComments, it1, it2Added);
  }

  /**
   * @param mapOfComments
   * @param it1
   * @param it2
   */
  private void setCommentsMap(final LinkedHashMap<CDRResultParameter, String> mapOfComments, final Iterator<String> it1,
      final Iterator<CDRResultParameter> it2) {
    while (it1.hasNext() && it2.hasNext()) {
      mapOfComments.put(it2.next(), it1.next());
    }
  }


  /**
   * @param nbrCopiedCells
   * @param commentsList
   * @param mapOfComments
   * @param it1
   * @param it2
   */
  private void setComments(final int nbrCopiedCells, final LinkedList<String> commentsList,
      final LinkedHashMap<CDRResultParameter, String> mapOfComments, final Iterator<String> it1,
      final Iterator<CDRResultParameter> it2) {
    if (nbrCopiedCells == 1) {
      // if only one cell is copied and pasted to multiple cells, then paste the same content in all cells
      while (it2.hasNext()) {
        mapOfComments.put(it2.next(), commentsList.get(0));
      }
    }
    else {
      setCommentsMap(mapOfComments, it1, it2);
    }
  }

  /**
   * @param reviewResultNatPage
   * @param mapOfComments
   */
  private void invokeCommandToSaveComments(final LinkedHashMap<CDRResultParameter, String> mapOfComments) {
    List<CDRResultParameter> paramList = new ArrayList<>();
    // invoke command to save into db
    for (Entry<CDRResultParameter, String> entry : mapOfComments.entrySet()) {

      CDRResultParameter paramClone = entry.getKey().clone();
      CommonUtils.shallowCopy(paramClone, entry.getKey());
      paramClone.setRvwComment(null == entry.getValue() ? "" : entry.getValue());
      paramList.add(paramClone);
    }
    try {
      CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
      client.update(paramList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * opens the dialog after checking the review status is closed
   *
   * @param shell the shell for display purpose
   * @param cdrResult the given calibration data review result
   * @param isLockStatusValidate if true the method checks if the status is not yet locked and then the lock message
   *          dialog will be shown
   * @param resultData Review Result ClientBO
   */
  // ICDM-2389
  public void openLockMessageDialog(final Shell shell, final boolean isLockStatusValidate,
      final ReviewResultClientBO resultData) {
    if ((CDRConstants.REVIEW_STATUS.CLOSED.getDbType()
        .equals(resultData.getResultBo().getCDRResult().getRvwStatus())) && resultData.getResultBo().isModifiable() &&
        !resultData.getResultBo().isResultLocked()) {
      openActualLockMessageDialog(shell, resultData);
    }
  }

  /**
   * opens the lock message dialog
   *
   * @param shell the shell for display purpose
   * @param resultData the given calibration data review result
   * @param resultData
   */
  // ICDM-2389
  private void openActualLockMessageDialog(final Shell shell, final ReviewResultClientBO resultData) {
    ReviewCompleteDialog dialog = new ReviewCompleteDialog(shell, resultData);
    dialog.open();
  }

  /**
   * @param commentDialog
   * @param paramList
   * @param page
   */
  public void importReviewComments(final CommentDialog commentDialog, final List<CDRResultParameter> paramList,
      final ReviewResultParamListPage page) {
    BusyIndicator.showWhile(Display.getDefault(), () -> {
      List<String> oldValues = new ArrayList<>();
      Map<Long, CDRResultParameter> paramMap = new HashMap<>();
      for (CDRResultParameter param : paramList) {
        paramMap.put(param.getId(), param);
        oldValues.add(param.getRvwComment());
      }
      try {
        CDRResultParameterServiceClient paramServiceClient = new CDRResultParameterServiceClient();
        paramServiceClient.importReviewComment(paramMap, commentDialog.isOverwriteComments(),
            Long.valueOf(commentDialog.getSourceResultLink()), commentDialog.isIncludeScore());
        // refresh the table viewer
        if (page != null) {
          Display.getDefault().syncExec(() -> {

            List<Object> valList = new ArrayList<>(oldValues);
            page.refreshColFilters(valList);
          });
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(
            "Exception while copying the comments from source review!\n" + exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    });


  }

  /**
   * @param failedParamList
   */
  private void displayErrorMsg(final List<CDRResultParameter> failedParamList, final String message) {
    if (!failedParamList.isEmpty()) {
      StringBuilder params = new StringBuilder();
      for (CDRResultParameter cdrResParam : failedParamList) {
        params.append(cdrResParam.getName()).append("\n");
      }
      CDMLogger.getInstance().infoDialog(message + params.toString().trim(), Activator.PLUGIN_ID);
    }
  }

  /**
   * @param selection
   * @param menuManagr
   * @param reviewResultParamListPage
   */
  public void takeOverFromCdfx(final IStructuredSelection selection, final MenuManager menuManagr,
      final ReviewResultParamListPage page) {
    final Action rvwCmntAction = new Action() {

      @Override
      public void run() {
        // check if the result can be modified
        if (page.getResultData().getResultBo().isResultLocked()) {
          CDMLogger.getInstance().errorDialog(REVIEW_LOCKED_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
          return;
        }
        final List<CDRResultParameter> paramList = new ArrayList<>(selection.toList());
        List<CDRResultParameter> failedParamList = new ArrayList<>();

        // remove parameters which are compli/qssd fail
        for (CDRResultParameter cdrResParam : paramList) {
          if (page.getResultData().isCompliFail(cdrResParam) || page.getResultData().isQssdFail(cdrResParam)) {
            failedParamList.add(cdrResParam);
          }
        }
        paramList.removeAll(failedParamList);

        List<CDRResultParameter> outputList = new ArrayList<>();

        if (!paramList.isEmpty()) {
          TakeOverCdfxRemarksDialog takeOverCdfxOptionsDialog =
              new TakeOverCdfxRemarksDialog(Display.getCurrent().getActiveShell());

          int isOkPressed = takeOverCdfxOptionsDialog.open();
          if (isOkPressed == CODE_FOR_OK) {
            executeTakeOverRemarkCmd(page, paramList, outputList, takeOverCdfxOptionsDialog.isIgnoreRemark(),
                takeOverCdfxOptionsDialog.isAppendRemark());
            displayErrorMsg(failedParamList, RVW_CMT_VALIDATION_MSG);
          }

        }
        else {
          displayErrorMsg(failedParamList,
              "The following parameters cannot be updated as they have result as Compli/Qssd :\n");
        }

      }


    };
    rvwCmntAction.setText(IMessageConstants.TAKE_OVER_REMARKS);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    rvwCmntAction.setImageDescriptor(imageDesc);
    menuManagr.add(rvwCmntAction);
    rvwCmntAction.setEnabled(page.getResultData().getResultBo().isModifiable());

  }


  /**
   * @param param result parameter
   * @return Remark() in History block
   */
  public String getHistoryRemark(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils
          .checkNull(getLatestHistory(param).getRemark() == null ? "" : getLatestHistory(param).getRemark().getValue());
    }
    return result;
  }


  /**
   * @param param result parameter
   * @return the latest hitory entry
   */
  public HistoryEntry getLatestHistory(final CDRResultParameter param) {
    CalData calData = getCheckedValueObj(param);
    HistoryEntry latestHistoryEntry = null;
    if (calData != null) {

      CalDataHistory calDataHistory = calData.getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        // Last Element from the HistoryEntryList is considered as the one with latest timestamp
        latestHistoryEntry = calDataHistory.getHistoryEntryList().get(calDataHistory.getHistoryEntryList().size() - 1);
      }
    }
    return latestHistoryEntry;
  }

  /**
   * Method to get CheckedValue object
   *
   * @param param result parameter
   * @return actual review output
   */
  public CalData getCheckedValueObj(final CDRResultParameter param) {

    return com.bosch.caltool.icdm.common.util.CalDataUtil.getCDPObj(param.getCheckedValue());
  }

  /**
   * @param page
   * @param paramList
   * @param failedParamList
   * @param outputList
   * @param nonModifiedList
   */
  private void executeTakeOverRemarkCmd(final ReviewResultParamListPage page, final List<CDRResultParameter> paramList,
      final List<CDRResultParameter> outputList, final boolean ignoreRemark, final boolean appendRemark) {

    List<CDRResultParameter> nonModifiedList = new ArrayList<>();
    List<CDRResultParameter> ignoredList = new ArrayList<>();

    for (CDRResultParameter cdrResultParameter : paramList) {

      String remarks = getHistoryRemark(cdrResultParameter);
      String existingRvwComment =
          (null == cdrResultParameter.getRvwComment()) ? "" : cdrResultParameter.getRvwComment();
      StringBuilder newRvwComment = new StringBuilder();
      if (CommonUtils.isNotEmptyString(remarks)) {
        if (ignoreRemark) {
          if (CommonUtils.isNotEmptyString(existingRvwComment)) {
            // existing comment should not be overwritten
            ignoredList.add(cdrResultParameter);
            continue;
          }
          cdrResultParameter.setRvwComment(truncateComment(remarks));
        }
        else if (appendRemark) {
          // if comment and remarks are same, append should not be done
          if (existingRvwComment.trim().equals(remarks.trim())) {
            nonModifiedList.add(cdrResultParameter);
            continue;
          }
          // remarks should be added to existing review comment
          newRvwComment.append(existingRvwComment);
          newRvwComment.append("\n");
          newRvwComment.append(remarks);

          cdrResultParameter.setRvwComment(truncateComment(newRvwComment.toString()));
        }
        else {
          // case - Replace, existing comment should be overwrtitten
          cdrResultParameter.setRvwComment(truncateComment(remarks));
        }

        outputList.add(cdrResultParameter);
      }
      else {
        nonModifiedList.add(cdrResultParameter);
      }
    }
    if (!outputList.isEmpty()) {
      multiParamUpdate(outputList, null, page, false);
    }
    displayErrorMsg(ignoredList,
        "The following parameter(s) are not updated as they have comments and take over is ignored :\n");

    displayErrorMsg(nonModifiedList,
        "The following parameter(s) are not updated as they either have no Cdfx remarks or have same comments as remarks:\n\n");

  }

  /**
   * Truncate comment length to 4000
   *
   * @param comment the review comment
   */
  private String truncateComment(final String comment) {
    return comment.length() > MAX_COMMENT_LENGTH ? comment.substring(0, MAX_COMMENT_LENGTH) : comment;
  }

}
