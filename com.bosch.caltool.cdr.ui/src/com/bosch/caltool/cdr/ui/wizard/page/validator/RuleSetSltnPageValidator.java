/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;


/**
 * @author say8cob
 */
public class RuleSetSltnPageValidator {

  private final CalDataReviewWizard calDataReviewWizard;

  private final RuleSetSltnPage ruleSetSelPage;


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @return the ruleSetSelPage
   */
  public RuleSetSltnPage getRuleSetSelPage() {
    return this.ruleSetSelPage;
  }


  /**
   * Constructor for RuleSetSltnPageValidator
   *
   * @param calDataReviewWizard as input
   */
  public RuleSetSltnPageValidator(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.ruleSetSelPage = calDataReviewWizard.getRuleSetSelPage();
  }


  /**
   * @return true/false based on any rule is selected
   */
  public boolean checkNextEnabled() {
    // If any one selection is made then return true
    if (this.ruleSetSelPage.getPrimaryRuleSetSection().isEnabled() &&
        this.ruleSetSelPage.getPrimaryRuleSetTabViewer().getGrid().isEnabled()) {
      // if the primary rule set section is enabled
      if ((this.ruleSetSelPage.getPrimaryRuleSetTabViewer().getSelection() == null) ||
          this.ruleSetSelPage.getPrimaryRuleSetTabViewer().getSelection().isEmpty()) {
        return false;
      }
    }

    else if ((this.ruleSetSelPage.getSecRuleSetTabViewer().getGrid().getSelection() == null) ||
        this.ruleSetSelPage.getSecRuleSetTabViewer().getSelection().isEmpty()) {
      return false;
    }
    // if an error had occurred

    return !this.ruleSetSelPage.isErrorOccured();


  }


}
