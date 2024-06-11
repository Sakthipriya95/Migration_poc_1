/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleEditInput;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;

/**
 * @author and4cob
 */
public class RuleParamContextMenuActions {

  private final MenuManager menuMgr;


  /**
   * @param menuMgr menuMgr
   */
  public RuleParamContextMenuActions(final MenuManager menuMgr) {
    this.menuMgr = menuMgr;
  }


  /**
   * @param page
   * @param mgr
   * @param firstElement
   * @param selectedObj
   */
  public void paramRightClick(final AbstractFormPage page, final IMenuManager mgr, final Object firstElement,
      final List<Object> selectedObj) {

    CommonActionSet commonActionset = new CommonActionSet();
    IParameter param = (IParameter) firstElement;
    ReviewParamEditor rvwParamEditor = null;
    ParamCollection paramColln = null;

    if (page instanceof ListPage) {
      ListPage listPage = (ListPage) page;
      rvwParamEditor = listPage.getEditor();
      paramColln = listPage.getCdrFunction();
    }
    else {
      // instance of Details Page
      DetailsPage detailsPage = (DetailsPage) page;
      rvwParamEditor = detailsPage.getEditor();
      paramColln = detailsPage.getCdrFunction();
    }
    // Adding RuleSet Context Menu Action
    addRuleSetContextMenuAction(mgr, commonActionset, rvwParamEditor, param);

    ParamCollectionDataProvider dataProvider = rvwParamEditor.getEditorInput().getDataProvider();
    ParameterDataProvider parameterDataProvider = rvwParamEditor.getEditorInput().getParamDataProvider();

    PasteRuleAction pasteAction = new PasteRuleAction(firstElement, paramColln, rvwParamEditor,
        dataProvider.isModifiable(paramColln), param, dataProvider, parameterDataProvider);

    if (parameterDataProvider.hasDependency(param)) {
      this.menuMgr.add(pasteAction);
      mgr.add(new Separator());
      // Copy 'Copy Parameter Name to Clipboard' context menu
      commonActionset.copyParamNameToClipboardAction(this.menuMgr, param.getName());
      mgr.add(new Separator());
    }
    else {
      RuleEditInput input = new RuleEditInput<>(paramColln, firstElement, null, null, null,
          !(dataProvider.isRulesModifiable(paramColln) && dataProvider.isModifiable(paramColln)), parameterDataProvider,
          dataProvider, null);
      // Add 'Show/Edit rule details' context menu
      EditRuleAction editRuleAction = new EditRuleAction(input, rvwParamEditor);

      this.menuMgr.add(editRuleAction);
      this.menuMgr.add(new Separator());
      CopyRuleAction copyRuleAction =
          new CopyRuleAction(firstElement, dataProvider.isModifiable(paramColln), parameterDataProvider);

      this.menuMgr.add(copyRuleAction);
      this.menuMgr.add(pasteAction);

      boolean rulesModifiable = dataProvider.isRulesModifiable(paramColln);
      if (rulesModifiable) {
        // map to new configuration
        Action newConfigAction = new AddNewConfigAction(parameterDataProvider.getRuleList(param), param, paramColln,
            page, parameterDataProvider, dataProvider);

        this.menuMgr.add(newConfigAction);
        mgr.add(new Separator());
      }
      this.menuMgr.add(new Separator());
      commonActionset.copyParamNameToClipboardAction(this.menuMgr, param.getName());
      if (rulesModifiable) {
        mgr.add(new Separator());
        // Add 'Set Maturity Level' context menu
        MaturityLevelActions actions = new MaturityLevelActions(parameterDataProvider.getRuleList(param), paramColln,
            rvwParamEditor.getListPage(), dataProvider);
        actions.createSetMaturityMenu(this.menuMgr);

      }
      mgr.add(new Separator());
    }

    final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
    // Add 'Show Series Statistics' context menu option
    cdmActionSet.addShowSeriesStatisticsMenuAction(this.menuMgr, selectedObj, true /* To enable action */);
    if (!parameterDataProvider.hasDependency(param)) {
      new ReviewRuleActionSet(parameterDataProvider).addShowRuleHistory(this.menuMgr, paramColln, firstElement);
    }
    // Add 'Show Review Data' context menu
    if ((param instanceof Parameter) && dataProvider.isRulesModifiable(paramColln)) {
      cdmActionSet.addShowReviewDataMenuAction(this.menuMgr, firstElement, true, parameterDataProvider);
    }

    // get All rules of Parameter
    List<ReviewRule> cdrRuleList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(parameterDataProvider.getRuleList(param))) {
      cdrRuleList.addAll(parameterDataProvider.getRuleList(param));
    }

    if (dataProvider.isRulesModifiable(paramColln)) {
      mgr.add(new Separator());

      // Delete Rule
      DeleteRuleAction deleteRuleAction = new DeleteRuleAction(cdrRuleList, false, paramColln,
          rvwParamEditor.getListPage(), selectedObj, false, parameterDataProvider, dataProvider);
      this.menuMgr.add(deleteRuleAction);
    }

    // Context menu to Open Links in default browser created for ruleset rules and common Rules
    List<RuleLinks> ruleLinksList = new ArrayList<>();
    for (ReviewRule rule : cdrRuleList) {
      ruleLinksList.addAll(rule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule());
    }
    commonActionset.openRuleLinkAction(this.menuMgr, ruleLinksList);
  }


  /**
   * @param mgr
   * @param cdrActionSet
   * @param rvwParamEditor
   * @param parameter
   */
  private void addRuleSetContextMenuAction(final IMenuManager mgr, final CommonActionSet cdrActionSet,
      final ReviewParamEditor rvwParamEditor, final IParameter parameter) {
    if (rvwParamEditor.getEditorInput().getCdrObject() instanceof RuleSet) {
      RuleSetParameter param = (RuleSetParameter) parameter;
      ReviewRule reviewRule =  rvwParamEditor.getEditorInput().getParamDataProvider().getReviewRule(param);
      if (CommonUtils.isNull(reviewRule)) {
        // copy RuleSet Link
        cdrActionSet.copyRuleSetLinktoClipBoard(mgr, (RuleSet) rvwParamEditor.getEditorInput().getCdrObject());
        // RuleSet Link in outlook Action
        final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_SET_LINK,
            rvwParamEditor.getEditorInput().getCdrObject());
        linkAction.setEnabled(true);
        mgr.add(linkAction);
      }
      else {
        //to display rule link for simple rule in RuleSetParameter Level
        cdrActionSet.copyRuleLinkOfParametertoClipBoard(mgr, (RuleSet) rvwParamEditor.getEditorInput().getCdrObject(),
            reviewRule, parameter);
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put(ApicConstants.RULE_ID, reviewRule.getRuleId().toString());
        additionalDetails.put(ApicConstants.RULESET_PARAM_ID, param.getId().toString());
        // Rule of a parameter Link in outlook Action
        final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_LINK_OF_PARAMETER,
            rvwParamEditor.getEditorInput().getCdrObject(), additionalDetails);
        linkAction.setEnabled(true);
        mgr.add(linkAction);
      }
      mgr.add(new Separator());


    }
  }

}
