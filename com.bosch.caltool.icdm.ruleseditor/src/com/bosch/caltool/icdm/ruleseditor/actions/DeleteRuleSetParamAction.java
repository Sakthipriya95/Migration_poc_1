/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author dmr1cob
 */
public class DeleteRuleSetParamAction extends Action {


  /**
   *
   */
  private static final int TASK_TOTAL_PERCENTAGE = 100;

  private static final int MAX_RULE_SET_PARAM_FOR_DELETE = 100;

  /**
   * Constant to denote size one
   */
  private static final int SIZE_ONE = 1;
  private final ReviewParamEditor editor;
  private final ParamCollectionDataProvider paramCollectionDataProvider;
  private final AbstractFormPage formPage;


  /**
   * @param cdrFunction cdrFunction
   * @param editor editor
   * @param paramCollectionDataProvider paramCollectionDataProvider
   * @param formPage formpage
   */
  public DeleteRuleSetParamAction(final ParamCollection cdrFunction, final ReviewParamEditor editor,
      final ParamCollectionDataProvider paramCollectionDataProvider, final AbstractFormPage formPage) {
    super("Delete parameter");
    this.editor = editor;
    this.paramCollectionDataProvider = paramCollectionDataProvider;
    this.formPage = formPage;

    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    setEnabled(this.paramCollectionDataProvider.isModifiable(cdrFunction));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    IStructuredSelection selection = null;
    if (this.formPage instanceof ListPage) {
      selection = (IStructuredSelection) this.editor.getListPage().getParamTabSec().getParamTab().getSelectionProvider()
          .getSelection();
    }
    else if (this.formPage instanceof DetailsPage) {
      selection = (IStructuredSelection) this.editor.getDetailsPage().getFcTableViewer().getSelection();
    }
    if ((selection != null) && (selection.getFirstElement() != null) &&
        (selection.getFirstElement() instanceof RuleSetParameter)) {

      // checking if selection is not null or empty
      boolean setSelectionToFirstElement = false;

      if (selection.size() > SIZE_ONE) {
        Set<RuleSetParameter> ruleSetParamSet = new HashSet<>();
        ruleSetParamSet.addAll(selection.toList());
        boolean confirm = MessageDialogUtils.getConfirmMessageDialog("Delete Parameter Dependencies",
            "The selected parameters will be deleted along with dependencies and rules.Do you still want to delete it?");

        setSelectionToFirstElement = deleteMultiRuleSetParams(ruleSetParamSet, confirm);
      }
      else {
        RuleSetParameter ruleSetParam = (RuleSetParameter) selection.getFirstElement();
        ParameterDataProvider ParameterDataProvider =
            new ParameterDataProvider(this.editor.getEditorInput().getParamRulesOutput());

        boolean confirm = true;

        if ((ParameterDataProvider.getParamAttrs(this.editor.getEditorInput().getCdrFuncParam()) != null) &&
            !ParameterDataProvider.getParamAttrs(this.editor.getEditorInput().getCdrFuncParam()).isEmpty()) {
          if ((ParameterDataProvider.getRuleList(this.editor.getEditorInput().getCdrFuncParam()) != null) &&
              !ParameterDataProvider.getRuleList(this.editor.getEditorInput().getCdrFuncParam()).isEmpty()) {

            // rules are present
            confirm = MessageDialogUtils.getConfirmMessageDialog("Delete Parameter Dependencies and Rules",
                "The selected parameter has dependencies and rules.Do you still want to delete it?");
          }
          else {
            // rules are not present
            confirm = MessageDialogUtils.getConfirmMessageDialog("Delete Parameter Dependencies",
                "The selected parameter has dependency.Do you still want to delete it?");
          }
        }
        if (confirm) {
          RuleSetParameterServiceClient ruleSetParamServiceClient = new RuleSetParameterServiceClient();
          try {
            ruleSetParamServiceClient.delete(ruleSetParam);
            removeDeletedParam(ruleSetParam);
            setSelectionToFirstElement = true;
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().warnDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
      if (setSelectionToFirstElement) {
        // after successful deletion, set the selection to first element
        // setting the inserted rule set parameter
        this.editor.getEditorInput().setCdrFuncParam(null);
        refreshPage(this.formPage);
      }
    }
  }

  /**
   * @param setSelectionToFirstElement
   * @param ruleSetParamSet
   * @param confirm
   * @return
   */
  private boolean deleteMultiRuleSetParams(final Set<RuleSetParameter> ruleSetParamSet, final boolean confirm) {
    boolean setSelectionToFirstElement = false;


    if (confirm) {

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      Set<RuleSetParameter> deletedRuleSetParams = new HashSet<>();
      try {
        dialog.run(true, true, monitor -> {
          int totalDone = 0;

          monitor.beginTask("Deleting Rule set Parmeters", TASK_TOTAL_PERCENTAGE);
          for (Set<RuleSetParameter> childSet : CommonUtils.splitSet(ruleSetParamSet, MAX_RULE_SET_PARAM_FOR_DELETE)) {
            DeleteMultiRuleSetParamProcessor deleteProcessor = new DeleteMultiRuleSetParamProcessor(childSet);
            boolean hasErrors = deleteProcessor.deleteRuleSetParams();
            totalDone = totalDone + MAX_RULE_SET_PARAM_FOR_DELETE;
            float progress = (float) totalDone / ruleSetParamSet.size();
            progress = progress * 100;
            monitor.worked((int) progress);
            if (!hasErrors) {
              deletedRuleSetParams.addAll(childSet);
            }
            else {
              break;
            }
          }
          monitor.worked(TASK_TOTAL_PERCENTAGE);
          monitor.done();

        });
      }
      catch (InvocationTargetException | InterruptedException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        return false;
      }


      // Refresh the UI.
      deletedRuleSetParams.forEach(this::removeDeletedParam);
      setSelectionToFirstElement = true;
    }


    return setSelectionToFirstElement;

  }

  /**
   * @param ruleSetParam
   */
  private void removeDeletedParam(final RuleSetParameter ruleSetParam) {
    IParamRuleResponse paramRulesOutput = this.editor.getEditorInput().getParamDataProvider().getParamRulesOutput();
    paramRulesOutput.getReviewRuleMap().remove(ruleSetParam.getName());
    paramRulesOutput.getAttrMap().remove(ruleSetParam.getName());
    paramRulesOutput.getParamMap().remove(ruleSetParam.getName());
  }

  /**
   * @param abstractFormPage
   */
  private void refreshPage(final AbstractFormPage abstractFormPage) {
    if (abstractFormPage instanceof ListPage) {
      ListPage page = (ListPage) abstractFormPage;
      page.refreshListPage();
      page.getParamTabSec().getParamTab().updateStatusBar(false);
    }
    if (abstractFormPage instanceof DetailsPage) {
      DetailsPage page = (DetailsPage) abstractFormPage;
      page.setTabViewerInput(false);
      page.setStatusBarMessage(page.getFcTableViewer());
      ListPage listPage = page.getEditor().getListPage();
      listPage.refreshListPage();
    }
  }
}
