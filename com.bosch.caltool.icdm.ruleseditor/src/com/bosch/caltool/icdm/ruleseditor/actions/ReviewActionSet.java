/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author rgo7cob Icdm-422 Review Result Editor Changes
 */
public class ReviewActionSet {

  /**
   * List page num 0
   */
  private static final int LIST_PAGE_NUM = 0;

  /**
   * Detail page num 1
   */
  private static final int DETAIL_PAGE_NUM = 1;
  /**
   * Rules page num 2
   */
  private static final int RULES_PAGE_NUM = 2;


  /**
   * Open the edtor and set the input
   *
   * @param input ReviewParamEditorInput
   * @param ruleSetParam IParameter<?>
   * @throws PartInitException
   */
  public void openRulesEditor(final ReviewParamEditorInput input,
      final com.bosch.caltool.icdm.model.cdr.IParameter ruleSetParam)
      throws PartInitException {
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
      IEditorPart rulesEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
      if (rulesEditor == null) {
        IEditorPart openEditor =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, input.getEditorID());
        ((ReviewParamEditorInput) openEditor.getEditorInput()).setCdrFuncParam(ruleSetParam);
        ReviewParamEditor parameditor = (ReviewParamEditor) openEditor;
        parameditor.getListPage().setRowSelection();
      }
      else {
        ReviewParamEditor reviewParamEditor = (ReviewParamEditor) rulesEditor;
        ReviewParamEditorInput alreadyOpenEditorInput = reviewParamEditor.getEditorInput();
        alreadyOpenEditorInput.setEditorAlreadyOpened(true);
        alreadyOpenEditorInput.setCdrFuncParam(ruleSetParam);
        alreadyOpenEditorInput.setRuleId(input.getRuleId());
        if (reviewParamEditor.getActivePage() == LIST_PAGE_NUM) {
          reviewParamEditor.getListPage().setActive(true);
        }
        else if (reviewParamEditor.getActivePage() == DETAIL_PAGE_NUM) {
          reviewParamEditor.getDtlsPage().setActive(true);
        }
        else if (reviewParamEditor.getActivePage() == RULES_PAGE_NUM) {
          reviewParamEditor.getParamRulesPage().setActive(true);
        }

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(alreadyOpenEditorInput,
            alreadyOpenEditorInput.getEditorID());
      }
    }
  }

  /**
   * Actions for cdr result
   *
   * @param menuManagr menu mgr
   * @param firstElement element
   * @param reviewResult
   * @param reviewEditorActionSet actionset
   */
  public void addActionForRulesEditor(final MenuManager menuManagr, final Object firstElement,
      final com.bosch.caltool.icdm.model.a2l.Function function, final RuleSet ruleSet) {
    ReviewActionSet reviewEditorActionSet = new ReviewActionSet();
    if (firstElement instanceof com.bosch.caltool.icdm.model.cdr.CDRResultParameter) {
      com.bosch.caltool.icdm.model.cdr.CDRResultParameter resultParam =
          (com.bosch.caltool.icdm.model.cdr.CDRResultParameter) firstElement;
      if (ruleSet == null) {
        reviewEditorActionSet.addReviewParamEditor(menuManagr, firstElement, null, function, true);
        menuManagr.add(new Separator());
      }
      else {
        reviewEditorActionSet.addRuleSetParamEditor(menuManagr, ruleSet, resultParam);
      }
    }

  }

  /**
   * o This method adds the right click option to open ReviewParamEditor
   *
   * @param menuMgr MenuManager instance
   * @param firstElement Function that is being selected
   * @param functionList Function list from the editor input
   * @param fromResultEditor from Result Editor
   */
  public void addReviewParamEditor(final MenuManager menuMgr, final Object firstElement,
      final SortedSet<Function> functionList, final com.bosch.caltool.icdm.model.a2l.Function function,
      final boolean fromResultEditor) {
    final Action openEditorAction =
        new OpenRulesEditorAction(functionList, firstElement, fromResultEditor, function, false, null);
    if (firstElement instanceof CDRResultFunction) {
      openEditorAction.setText(IMessageConstants.OPEN_REVIEW_RULES_LABEL);
    }
    else {
      openEditorAction.setText(IMessageConstants.OPEN_REVIEW_RULE_LABEL);
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FUNCTION_EDIT_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    menuMgr.add(openEditorAction);

  }

  /**
   * This method adds the right click option to open ReviewParamEditor
   *
   * @param menuMgr MenuManager instance
   * @param firstElement Function that is being selected
   * @param functionList Function list from the editor input
   * @param function
   * @param fromResultEditor from Result Editor
   */
  public void addReviewParamEditorNew(final MenuManager menuMgr, final Object firstElement,
      final SortedSet<Function> functionList, final com.bosch.caltool.icdm.model.a2l.Function function,
      final boolean fromResultEditor) {
    final Action openEditorAction =
        new OpenRulesEditorAction(functionList, firstElement, fromResultEditor, function, false, null);
    if (firstElement instanceof com.bosch.caltool.icdm.model.cdr.CDRResultFunction) {
      openEditorAction.setText(IMessageConstants.OPEN_REVIEW_RULES_LABEL);
    }
    else {
      openEditorAction.setText(IMessageConstants.OPEN_REVIEW_RULE_LABEL);
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FUNCTION_EDIT_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    menuMgr.add(openEditorAction);

  }

  /**
   * @param menuManager menuManager
   * @param ruleSet ruleSet
   * @param resultParam resultParam
   */
  public void addRuleSetParamEditor(final MenuManager menuManager,
      final com.bosch.caltool.icdm.model.cdr.RuleSet ruleSet,
      final com.bosch.caltool.icdm.model.cdr.CDRResultParameter resultParam) {
    final Action openEditorAction = new Action() {

      @Override
      public void run() {
        try {
          ParamCollectionDataProvider dataProvider = new ParamCollectionDataProvider();
          Map<String, com.bosch.caltool.icdm.model.cdr.RuleSetParameter> paramMap =
              dataProvider.getRulesOutput(ruleSet.getId()).getParamMap();


          com.bosch.caltool.icdm.model.cdr.IParameter ruleSetParam = paramMap.get(resultParam.getName());
          if ((ruleSetParam == null) && ApicUtil.isVariantCoded(resultParam.getName())) {
            ruleSetParam = paramMap.get(ApicUtil.getBaseParamName(resultParam.getName()));
          }
          // ICDM-1426 Show message dialog when rule set is deleted
          if (com.bosch.caltool.icdm.common.util.CommonUtils.isNull(ruleSetParam)) {
            MessageDialogUtils.getInfoMessageDialog("Parameter no longer exists",
                "The parameter no longer exists in the rule set!");
          }
          ReviewActionSet.this.openRulesEditor(new ReviewParamEditorInput(ruleSet), ruleSetParam);
        }
        catch (WorkbenchException | ApicWebServiceException excep) {
          CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }
      }

    };

    openEditorAction.setText(IMessageConstants.OPEN_REVIEW_RULE_LABEL);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_SET_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    menuManager.add(openEditorAction);
  }


}
