/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.SSDRuleSelectionPage;


/**
 * @author say8cob
 */
public class SSDRuleSelectionPageValidator {

  private final CalDataReviewWizard calDataReviewWizard;
  private final SSDRuleSelectionPage ssdRuleSelectionPageNew;


  /**
   * @param calDataReviewWizard
   * @param ssdRuleSelectionPageNew
   */
  public SSDRuleSelectionPageValidator(final CalDataReviewWizard calDataReviewWizard,
      final SSDRuleSelectionPage ssdRuleSelectionPageNew) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.ssdRuleSelectionPageNew = ssdRuleSelectionPageNew;
  }

  /**
   * @return
   */
  public boolean checkNextEnabled() {
    return (this.ssdRuleSelectionPageNew.getSsdFileTxt().getText().length() > 0) ||
        ((this.ssdRuleSelectionPageNew.getSsdTabViewer().getSelection() != null) &&
            !this.ssdRuleSelectionPageNew.getSsdTabViewer().getSelection().isEmpty());
  }


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @return the ssdRuleSelectionPageNew
   */
  public SSDRuleSelectionPage getSsdRuleSelectionPageNew() {
    return this.ssdRuleSelectionPageNew;
  }


}
