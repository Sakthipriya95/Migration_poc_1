/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewRuleDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleProviderResolver;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;

/**
 * @author rgo7cob
 */
public class MaturityLevelActions<D extends IParameterAttribute, P extends IParameter, C extends ParamCollection> {

  private final List<ReviewRule> ruleList;

  private final ParamCollection cdrFunction;

  private final AbstractFormPage formPage;

  private final ParamCollectionDataProvider paramColDataProvider;

  /**
   * Ssd success message Code
   */
  private static final int SSD_SUCCESS_CODE = 0;

  /**
   * Icdm-1056 Constant for update of Review Rule
   */
  private static final String UPDATE = "UPDATE Review Rule for the parameter ";
  /**
   * Icdm-1056 Constant for update of Review Rule complete
   */
  private static final String COMPLETED = " completed !";
  /**
   * Icdm-1056 Constant for update of Review Rule failed
   */
  private static final String FAILED = " failed ! ";


  /**
   * @param ruleList
   * @param cdrFunction
   * @param formPage
   */
  public MaturityLevelActions(final List<ReviewRule> ruleList, final ParamCollection cdrFunction,
      final AbstractFormPage formPage, final ParamCollectionDataProvider paramColDataProvider) {
    super();
    this.ruleList = ruleList;
    this.cdrFunction = cdrFunction;
    this.formPage = formPage;
    this.paramColDataProvider = paramColDataProvider;
  }


  // ICDM-1308
  /**
   * {@inheritDoc}
   */
  public void createSetMaturityMenu(final MenuManager menuMgr) {
    // Set maturity level
    IMenuManager subMenu = new MenuManager("Set Maturity Level");
    subMenu.setRemoveAllWhenShown(true);
    subMenu.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager imenumanager) {
        imenumanager.add(createNoneAction());
        imenumanager.add(createPreCalibAction());
        imenumanager.add(createCalibratedAction());
        imenumanager.add(createCheckedAction());
      }
    });
    subMenu.setVisible(true);
    menuMgr.add(subMenu);
  }


  /**
   * {@inheritDoc}
   */
  private Action createNoneAction() {
    Action noneAction = new Action() {

      @Override
      public void run() {
        Set<ReviewRule> ruleSet = new HashSet<>();
        ruleSet.addAll(MaturityLevelActions.this.ruleList);
        for (ReviewRule selRule : ruleSet) {
          boolean setMaturitySuccess = updateRulesMaturityLevel(selRule, RuleMaturityLevel.NONE.getICDMMaturityLevel(),
              MaturityLevelActions.this.cdrFunction);
          if (setMaturitySuccess) {
            refreshOnMaturityChange(MaturityLevelActions.this.formPage);
          }
        }
      }

    };
    noneAction.setEnabled(checkEnabled());
    noneAction.setText(RuleMaturityLevel.NONE.getICDMMaturityLevel());
    return noneAction;
  }

  /**
   * {@inheritDoc}
   */
  private Action createPreCalibAction() {
    Action preCalibAction = new Action() {

      @Override
      public void run() {
        Set<ReviewRule> ruleSet = new HashSet<>();
        ruleSet.addAll(MaturityLevelActions.this.ruleList);
        for (ReviewRule selRule : ruleSet) {
          boolean setMaturitySuccess = updateRulesMaturityLevel(selRule, RuleMaturityLevel.START.getICDMMaturityLevel(),
              MaturityLevelActions.this.cdrFunction);
          if (setMaturitySuccess) {
            refreshOnMaturityChange(MaturityLevelActions.this.formPage);
          }
        }
      }

    };
    preCalibAction.setEnabled(checkEnabled());
    preCalibAction.setText(RuleMaturityLevel.START.getICDMMaturityLevel());
    return preCalibAction;
  }


  /**
   * @return
   */
  private boolean checkEnabled() {
    return (null != this.ruleList) && !this.ruleList.isEmpty() &&
        this.paramColDataProvider.isModifiable(this.cdrFunction);
  }


  /**
   * {@inheritDoc}
   */
  private Action createCalibratedAction() {
    Action calibratedAction = new Action() {

      @Override
      public void run() {
        Set<ReviewRule> ruleSet = new HashSet<>();
        ruleSet.addAll(MaturityLevelActions.this.ruleList);
        for (ReviewRule selRule : ruleSet) {
          boolean setMaturitySuccess = updateRulesMaturityLevel(selRule,
              RuleMaturityLevel.STANDARD.getICDMMaturityLevel(), MaturityLevelActions.this.cdrFunction);
          if (setMaturitySuccess) {
            refreshOnMaturityChange(MaturityLevelActions.this.formPage);
          }
        }
      }

    };
    calibratedAction.setEnabled(checkEnabled());
    calibratedAction.setText(RuleMaturityLevel.STANDARD.getICDMMaturityLevel());
    return calibratedAction;
  }

  /**
   * {@inheritDoc}
   */
  private Action createCheckedAction() {
    Action preCalibAction = new Action() {

      @Override
      public void run() {
        Set<ReviewRule> ruleSet = new HashSet<>();
        ruleSet.addAll(MaturityLevelActions.this.ruleList);
        for (ReviewRule selRule : ruleSet) {
          boolean setMaturitySuccess = updateRulesMaturityLevel(selRule, RuleMaturityLevel.FIXED.getICDMMaturityLevel(),
              MaturityLevelActions.this.cdrFunction);
          if (setMaturitySuccess) {
            refreshOnMaturityChange(MaturityLevelActions.this.formPage);
          }
        }
      }

    };
    preCalibAction.setEnabled(checkEnabled());
    preCalibAction.setText(RuleMaturityLevel.FIXED.getICDMMaturityLevel());
    return preCalibAction;
  }

  /**
   * Refreshs the corresponding pages table when maturity level is set using context menu
   *
   * @param formPage
   */
  private void refreshOnMaturityChange(final AbstractFormPage formPage) {
    if (formPage instanceof ParametersRulePage) {
      ParametersRulePage rulePg = (ParametersRulePage) formPage;
      if (rulePg.getFcTableViewer() != null) {
        rulePg.setTabViewerInput(true);
        rulePg.setActive(false);
      }
    }
    else if (formPage instanceof ListPage) {
      ListPage listPg = (ListPage) formPage;
      if (listPg.getFcTableViewer() != null) {
        listPg.getFcTableViewer().refresh();
      }
    }
  }


  /**
   * @param rule
   * @param maturityLevel
   * @param cdrFunction
   * @return
   */
  public boolean updateRulesMaturityLevel(final ReviewRule rule, final String maturityLevel,
      final ParamCollection cdrFunction) {
    try {
      rule.setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(maturityLevel));

      ReviewRuleDataProvider ruleDataProvider = new RuleProviderResolver().getRuleProvider(cdrFunction);

      ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
      parmCol.setParamCollection(cdrFunction);
      parmCol.setReviewRule(rule);
      SSDMessageWrapper ssdCode = ruleDataProvider.updateCdrRule(parmCol);

      if ((ssdCode != null) && (ssdCode.getCode() == SSD_SUCCESS_CODE)) {
        CDMLogger.getInstance().info(UPDATE + rule.getParameterName() + COMPLETED, Activator.PLUGIN_ID);
        return true;
      }
      CDMLogger.getInstance().errorDialog(UPDATE + rule.getParameterName() + FAILED + "ssd error", Activator.PLUGIN_ID);
      resetRule(rule);
      return false;
    }
    catch (Exception excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep);
      CDMLogger.getInstance().errorDialog(UPDATE + rule.getParameterName() + FAILED + excep.getMessage(),
          Activator.PLUGIN_ID);
      resetRule(rule);
      return false;
    }
  }

  /**
   * Icdm-1056 if any Exception reset the Rule to the previous value.
   *
   * @param cdrRule
   */
  private void resetRule(final ReviewRule reviewRule) {
    ReviewRuleDataProvider<ParamCollection> dataProvider = new RuleProviderResolver().getRuleProvider(this.cdrFunction);
    ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
    parmCol.setParamCollection(this.cdrFunction);
    parmCol.setReviewRule(reviewRule);
    ReviewRuleParamCol<C> readRules = dataProvider.readRules(parmCol);
    ReviewRule oldCdrRule = null;

    for (ReviewRule cdrRuleFromList : readRules.getReviewRuleList()) {
      if (CommonUtils.isEqual(cdrRuleFromList.getRuleId(), reviewRule.getRuleId())) {
        oldCdrRule = cdrRuleFromList;
        break;
      }
    }
    if (oldCdrRule != null) {
      reviewRule.setValueType(reviewRule.getValueType());
      reviewRule.setLowerLimit(oldCdrRule.getLowerLimit());
      reviewRule.setUpperLimit(oldCdrRule.getUpperLimit());

      reviewRule.setReviewMethod(oldCdrRule.getReviewMethod());

      reviewRule.setHint(oldCdrRule.getHint());
      // Icdm-537 Set the Unit Value
      reviewRule.setUnit(oldCdrRule.getUnit());

      // ICDM 597
      reviewRule.setDcm2ssd(oldCdrRule.isDcm2ssd());
      if (reviewRule.getValueType().equals(ParameterType.VALUE.getText())) {
        if (oldCdrRule.getRefValue() == null) {
          reviewRule.setRefValueDcmString(oldCdrRule.getRefValueDcmString());
        }
        else {
          reviewRule.setRefValue(oldCdrRule.getRefValue());
        }
      }
      else {
        reviewRule.setRefValueDcmString(oldCdrRule.getRefValueDcmString());
      }

      reviewRule.setMaturityLevel(
          RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(oldCdrRule.getMaturityLevel()).getSSDMaturityLevel());

    }
  }


}
