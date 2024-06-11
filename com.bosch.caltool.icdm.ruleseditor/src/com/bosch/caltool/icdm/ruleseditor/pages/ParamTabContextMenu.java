/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleEditInput;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.actions.AddNewConfigAction;
import com.bosch.caltool.icdm.ruleseditor.actions.CopyRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.DeleteRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.EditRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.EditRuleWithDepAction;
import com.bosch.caltool.icdm.ruleseditor.actions.MaturityLevelActions;
import com.bosch.caltool.icdm.ruleseditor.actions.PasteRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.RuleParamContextMenuActions;


/**
 * @author bru2cob
 */
public class ParamTabContextMenu<D extends IParameterAttribute, P extends IParameter> {

  private final ParamTableSection paramTableSection;
  private ParameterDataProvider<D, P> parameterDataProvider;
  private final ParamCollectionDataProvider paramCollDataProvider;

  /**
   * @param paramTableSection
   * @param paramCollectionDataProvider
   */
  public ParamTabContextMenu(final ParamTableSection paramTableSection,
      final ParameterDataProvider<D, P> parameterDataProvider,
      final ParamCollectionDataProvider paramCollDataProvider) {
    this.paramTableSection = paramTableSection;
    this.parameterDataProvider = parameterDataProvider;
    this.paramCollDataProvider = paramCollDataProvider;
  }


  /**
   * Right click context menu for parameters
   */
  public void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {

      final IStructuredSelection selection = (IStructuredSelection) ParamTabContextMenu.this.paramTableSection
          .getParamTab().getSelectionProvider().getSelection();
      if (selection.size() > 1) {// ICDM-2351

        contextMenuForMultiSelection(mgr, selection);

      }
      else {
        contextMenuForSingleSelection(menuMgr, mgr, selection);
      }
    });

    final Menu menu = menuMgr.createContextMenu(ParamTabContextMenu.this.paramTableSection.getNatTable().getShell());
    ParamTabContextMenu.this.paramTableSection.getNatTable().setMenu(menu);
    // Register menu for extension.
    ParamTabContextMenu.this.paramTableSection.getListPage().getSite().registerContextMenu(menuMgr,
        ParamTabContextMenu.this.paramTableSection.getParamTab().getSelectionProvider());

  }


  /**
   * @param mgr
   * @param selection
   */
  private void contextMenuForMultiSelection(final IMenuManager mgr, final IStructuredSelection selection) {
    List<ReviewRule> ruleList = new ArrayList<>();
    // for multi select

    for (Object obj : selection.toList()) {
      if (obj instanceof ReviewRule) {
        ruleList.add((ReviewRule) obj);
      }
      else if (obj instanceof IParameter) {
        IParameter param = (IParameter) obj;
        List<ReviewRule> reviewRules = ParamTabContextMenu.this.parameterDataProvider.getRuleList(param);

        if (reviewRules != null) {
          ruleList.addAll(reviewRules);
        }
      }
      else if (obj instanceof DefaultRuleDefinition) {
        ruleList.add(((DefaultRuleDefinition) obj).getReviewRule());
      }
    }
    DeleteRuleAction action = new DeleteRuleAction<>(ruleList, false,
        ParamTabContextMenu.this.paramTableSection.getListPage().getCdrFunction(),
        ParamTabContextMenu.this.paramTableSection.getListPage(), selection.toList(), true,
        ParamTabContextMenu.this.parameterDataProvider, ParamTabContextMenu.this.paramCollDataProvider);

    mgr.add(action);
  }


  /**
   * @param menuMgr
   * @param mgr
   * @param selection
   */
  private void contextMenuForSingleSelection(final MenuManager menuMgr, final IMenuManager mgr,
      final IStructuredSelection selection) {
    // for single select
    final Object firstElement = selection.getFirstElement();
    if ((firstElement != null) && (!selection.isEmpty())) {
      // Icdm-521
      // Using the CDM common Action Set available in common UI
      final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
      List<Object> selectedObj = new ArrayList<>();
      selectedObj.add(firstElement);
      // Add Show Series statistics menu action
      final CommonActionSet pidcActionSet = new CommonActionSet();
      ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet(ParamTabContextMenu.this.parameterDataProvider);
      // ICDM-1085
      if (firstElement instanceof IParameter) {
        final RuleParamContextMenuActions commonContextMenuAction = new RuleParamContextMenuActions(menuMgr);
        commonContextMenuAction.paramRightClick(ParamTabContextMenu.this.paramTableSection.getListPage(), menuMgr,
            firstElement, selectedObj);
      }
      // ICDM-1086
      if (firstElement instanceof ReviewRule) {
        ruleRightClick(menuMgr, mgr, firstElement, cdmActionSet, selectedObj, pidcActionSet, paramActionSet);

      }
      if (firstElement instanceof DefaultRuleDefinition) {
        defaultRuleRightClick(menuMgr, mgr, firstElement, cdmActionSet, selectedObj, pidcActionSet, paramActionSet);
      }
    }
  }

  /**
   * @param menuMgr
   * @param mgr
   * @param firstElement
   * @param cdmActionSet
   * @param selectedObj
   * @param commonActionSet
   * @param paramActionSet
   */
  private void ruleRightClick(final MenuManager menuMgr, final IMenuManager mgr, final Object firstElement,
      final CDMCommonActionSet cdmActionSet, final List<Object> selectedObj, final CommonActionSet commonActionSet,
      final ReviewRuleActionSet paramActionSet) {
    ReviewRule rule = (ReviewRule) firstElement;
    List<ReviewRule> ruleList = new ArrayList<>();
    ListPage listPage = ParamTabContextMenu.this.paramTableSection.getListPage();
    ParamCollectionDataProvider dataProvider = listPage.getEditor().getEditorInput().getDataProvider();
    this.parameterDataProvider = listPage.getEditor().getEditorInput().getParamDataProvider();
    ParamCollection paramColln = listPage.getCdrFunction();
    ruleList.add(rule);
    IParameter cdrFuncParam =
        this.parameterDataProvider.getParamRulesOutput().getParamMap().get(rule.getParameterName());

    CommonActionSet actionSet = new CommonActionSet();
    ReviewRule reviewRule = ruleList.get(0);
    // add copy/send rulelink action for parameters
    addRuleLinkForParamAction(menuMgr, listPage, cdrFuncParam, actionSet, reviewRule);
    mgr.add(new Separator());

    menuMgr.add(new Separator());


    CopyRuleAction copyRuleAction =
        new CopyRuleAction(firstElement, dataProvider.isModifiable(paramColln), this.parameterDataProvider);


    PasteRuleAction pasteAction = new PasteRuleAction(firstElement, paramColln, listPage.getEditor(),
        dataProvider.isModifiable(paramColln), cdrFuncParam, this.paramCollDataProvider, this.parameterDataProvider);


    if (this.parameterDataProvider.hasDependency(cdrFuncParam) &&
        ((null == rule.getDependencyList()) || rule.getDependencyList().isEmpty())) {

      RuleEditInput input = new RuleEditInput<>(paramColln, firstElement, null, null, null,
          !(this.paramCollDataProvider.isRulesModifiable(paramColln) &&
              this.paramCollDataProvider.isModifiable(paramColln)),
          this.parameterDataProvider, this.paramCollDataProvider, null);
      EditRuleAction editRuleAction = new EditRuleAction(input, listPage.getEditor());


      // Icdm-1056-new Context menu item for edit rules
      menuMgr.add(editRuleAction);

      // Icdm-1181

    }
    else {
      EditRuleWithDepAction action = new EditRuleWithDepAction(this.parameterDataProvider, this.paramCollDataProvider,
          ruleList, cdrFuncParam, paramColln, listPage, !(this.paramCollDataProvider.isRulesModifiable(paramColln) &&
              this.paramCollDataProvider.isModifiable(paramColln)));

      menuMgr.add(action);


    }
    menuMgr.add(new Separator());
    menuMgr.add(copyRuleAction);
    menuMgr.add(pasteAction);
    // add copy rule action

    Action newConfigAction = new AddNewConfigAction(this.parameterDataProvider.getRuleList(cdrFuncParam), cdrFuncParam,
        paramColln, listPage, this.parameterDataProvider, this.paramCollDataProvider);
    menuMgr.add(newConfigAction);
    mgr.add(new Separator());


    addRuleContextMenuOpts(menuMgr, mgr, cdmActionSet, selectedObj, paramActionSet, rule, ruleList, commonActionSet,
        firstElement);


  }


  /**
   * @param menuMgr
   * @param listPage
   * @param cdrFuncParam
   * @param actionSet
   * @param reviewRule
   */
  private void addRuleLinkForParamAction(final MenuManager menuMgr, final ListPage listPage,
      final IParameter cdrFuncParam, final CommonActionSet actionSet, final ReviewRule reviewRule) {
    if (listPage.getEditor().getEditorInput().getCdrObject() instanceof RuleSet) {
      actionSet.copyRuleLinkOfParametertoClipBoard(menuMgr,
          (RuleSet) listPage.getEditor().getEditorInput().getCdrObject(), reviewRule, cdrFuncParam);

      Map<String, String> additionalDetails = new HashMap<>();
      additionalDetails.put(ApicConstants.RULE_ID, reviewRule.getRuleId().toString());
      additionalDetails.put(ApicConstants.RULESET_PARAM_ID, cdrFuncParam.getId().toString());

      // Rule of a parameter Link in outlook Action
      final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_LINK_OF_PARAMETER,
          listPage.getEditor().getEditorInput().getCdrObject(), additionalDetails);
      linkAction.setEnabled(true);
      menuMgr.add(linkAction);
    }
  }

  /**
   * @param menuMgr MenuManager
   * @param mgr IMenuManager
   * @param firstElement Object
   * @param cdmActionSet CDMCommonActionSet
   * @param selectedObj List<Object>
   * @param commonActionSet CommonActionSet
   * @param paramActionSet ReviewRuleActionSet
   */
  private void defaultRuleRightClick(final MenuManager menuMgr, final IMenuManager mgr, final Object firstElement,
      final CDMCommonActionSet cdmActionSet, final List<Object> selectedObj, final CommonActionSet commonActionSet,
      final ReviewRuleActionSet paramActionSet) {
    DefaultRuleDefinition rule = (DefaultRuleDefinition) firstElement;
    List<ReviewRule> selectedRule = new ArrayList<>();
    selectedRule.add(rule.getReviewRule());

    ListPage listPage = ParamTabContextMenu.this.paramTableSection.getListPage();
    ParamCollectionDataProvider dataProvider = listPage.getEditor().getEditorInput().getDataProvider();
    ParamCollection paramColln = listPage.getCdrFunction();


    RuleEditInput input = new RuleEditInput<>(paramColln, firstElement, null, null, null,
        !(this.paramCollDataProvider.isRulesModifiable(paramColln) &&
            this.paramCollDataProvider.isModifiable(paramColln)),
        this.parameterDataProvider, this.paramCollDataProvider, null);
    IParameter cdrFuncParam =
        this.parameterDataProvider.getParamRulesOutput().getParamMap().get(rule.getReviewRule().getParameterName());

    CommonActionSet actionSet = new CommonActionSet();
    ReviewRule reviewRule = selectedRule.get(0);
    // add copy/send rulelink action for parameters
    addRuleLinkForParamAction(menuMgr, listPage, cdrFuncParam, actionSet, reviewRule);
    mgr.add(new Separator());

    EditRuleAction editRuleAction =
        new EditRuleAction(input, ParamTabContextMenu.this.paramTableSection.getListPage().getEditor());
    // Icdm-1056-new Context menu item for edit rules
    menuMgr.add(editRuleAction);
    menuMgr.add(new Separator());
    CopyRuleAction copyRuleAction =
        new CopyRuleAction(firstElement, dataProvider.isModifiable(paramColln), this.parameterDataProvider);


    PasteRuleAction pasteAction = new PasteRuleAction(firstElement, paramColln, listPage.getEditor(),
        dataProvider.isModifiable(paramColln), cdrFuncParam, this.paramCollDataProvider, this.parameterDataProvider);
    menuMgr.add(copyRuleAction);
    menuMgr.add(pasteAction);
    menuMgr.add(new Separator());
    addRuleContextMenuOpts(menuMgr, mgr, cdmActionSet, selectedObj, paramActionSet, rule.getReviewRule(), selectedRule,
        commonActionSet, firstElement);
  }


  private void addRuleContextMenuOpts(final MenuManager menuMgr, final IMenuManager mgr,
      final CDMCommonActionSet cdmActionSet, final List<Object> selectedObj, final ReviewRuleActionSet paramActionSet,
      final ReviewRule rule, final List<ReviewRule> ruleList, final CommonActionSet cmnActionSet,
      final Object firstElement) {
    ListPage listPage = ParamTabContextMenu.this.paramTableSection.getListPage();
    ParamCollectionDataProvider dataProvider = listPage.getEditor().getEditorInput().getDataProvider();
    ParamCollection paramColln = listPage.getCdrFunction();
    IParameter cdrFuncParam =
        this.parameterDataProvider.getParamRulesOutput().getParamMap().get(rule.getParameterName());
    CommonActionSet actionSet = new CommonActionSet();
    ReviewRule reviewRule = ruleList.get(0);

    // ICDM-1749
    menuMgr.add(new Separator());
    cmnActionSet.copyParamNameToClipboardAction(menuMgr, rule.getParameterName());

    if (dataProvider.isRulesModifiable(paramColln)) {
      menuMgr.add(new Separator());
      MaturityLevelActions actions = new MaturityLevelActions(this.parameterDataProvider.getRuleList(cdrFuncParam),
          paramColln, listPage, this.paramCollDataProvider);
      actions.createSetMaturityMenu(menuMgr);
    }
    mgr.add(new Separator());

    cdmActionSet.addShowSeriesStatisticsMenuAction(menuMgr, selectedObj, true /* To enable action */);

    paramActionSet.addShowRuleHistory(menuMgr, paramColln, firstElement);

    // Show review Data
    if (paramColln instanceof Function) {
      cdmActionSet.addShowReviewDataMenuAction(menuMgr, rule, true, this.parameterDataProvider);
    }
    mgr.add(new Separator());

    // Delete Rule
    if (dataProvider.isRulesModifiable(paramColln)) {
      DeleteRuleAction deleteRuleAction = new DeleteRuleAction(ruleList, false, paramColln, listPage, selectedObj,
          false, this.parameterDataProvider, this.paramCollDataProvider);

      menuMgr.add(deleteRuleAction);
    }

    // Context menu to Open Links in default browser created for ruleset rules and common Rules
    actionSet.openRuleLinkAction(menuMgr, reviewRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule());
  }
}
