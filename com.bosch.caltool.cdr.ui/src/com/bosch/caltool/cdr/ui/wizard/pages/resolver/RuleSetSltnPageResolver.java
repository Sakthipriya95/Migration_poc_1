/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author say8cob
 */
public class RuleSetSltnPageResolver implements IReviewUIDataResolver {


  private final CalDataReviewWizard calDataReviewWizard;


  /**
   * @param calDataReviewWizard
   */
  public RuleSetSltnPageResolver(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
    final IStructuredSelection selection =
        (IStructuredSelection) this.calDataReviewWizard.getRuleSetSelPage().getSecRuleSetTabViewer().getSelection();
    List list = selection.toList();
    for (Object object : list) {
      boolean addRuleSet = true;
      RuleSet ruleSet = (RuleSet) object;
      for (RuleSet ruleSetData : this.calDataReviewWizard.getRuleSetSelPage().getSecondaryReviewRuleSetList()) {
        if (ruleSetData == ruleSet) {
          addRuleSet = false;
        }
      }
      if (addRuleSet) {
        this.calDataReviewWizard.getRuleSetSelPage().getSecondaryReviewRuleSetList().add(ruleSet);
        this.calDataReviewWizard.getCdrWizardUIModel().getSecondaryRuleSetIds().add(ruleSet.getId());
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processBackPressed() {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard calDataReviewWizard) {
    String ruleSetRestrictredNames = "";
    RuleSetSltnPage ruleSetSelPage = calDataReviewWizard.getRuleSetSelPage();
    CDRWizardUIModel cdrWizardUIModel = calDataReviewWizard.getCdrWizardUIModel();

    ruleSetSelPage.getPrimaryRuleSetTabViewer().getGrid().setEnabled(!cdrWizardUIModel.isCommonRulesPrimary());

    try {
      RuleSet primaryRuleSet = cdrWizardUIModel.getPrimaryRuleSet();
      if (null != primaryRuleSet) {
        ruleSetSelPage.getPrimaryRuleSetTabViewer().setSelection(new StructuredSelection(primaryRuleSet), true);

        if (ruleSetSelPage.isRestricted(primaryRuleSet)) {
          ruleSetRestrictredNames = CommonUtils.concatenate(ruleSetRestrictredNames, primaryRuleSet.getName(), ",");
        }

      }
      if (null != cdrWizardUIModel.getSecondaryRuleSets()) {
        SortedSet<RuleSet> secondaryRuleSets = new TreeSet<>(cdrWizardUIModel.getSecondaryRuleSets());
        List<RuleSet> secondaryRuleList = new ArrayList<>(secondaryRuleSets);
        ruleSetSelPage.getSecondaryResultTabViewer().setSelection(new StructuredSelection(secondaryRuleList), true);
        for (Object ruleSetSelected : secondaryRuleSets) {
          RuleSet ruleSet = (RuleSet) ruleSetSelected;
          if (ruleSetSelPage.isRestricted(ruleSet)) {
            ruleSetRestrictredNames = CommonUtils.concatenate(ruleSetRestrictredNames, ruleSet.getName(), ",");

            if (ruleSetRestrictredNames.length() > 0) {
              ruleSetRestrictredNames = ruleSetRestrictredNames.substring(0, ruleSetRestrictredNames.length() - 1);
              ruleSetSelPage.setErrorMessage("Access restricted to the following Rule Set (" + ruleSetRestrictredNames +
                  "). Please select a different rule set.");
              ruleSetSelPage.setErrorOccured(true);
              ruleSetSelPage.updateContainer();
            }
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }
}
