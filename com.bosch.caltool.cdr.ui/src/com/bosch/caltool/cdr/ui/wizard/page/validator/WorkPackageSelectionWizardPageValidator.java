/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;

/**
 * @author say8cob
 */
public class WorkPackageSelectionWizardPageValidator {


  private final CalDataReviewWizard calDataReviewWizard;
  private final WorkpackageSelectionWizardPage wpSelWizPage;


  /**
   * @param calDataReviewWizard
   * @param workpackageSelectionWizardPage
   * @param projectDataSelectionWizardPage
   */
  public WorkPackageSelectionWizardPageValidator(final CalDataReviewWizard calDataReviewWizard,
      final WorkpackageSelectionWizardPage workpackageSelectionWizardPage) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.wpSelWizPage = workpackageSelectionWizardPage;
  }


  /**
   * true/false based on UI selection
   */
  public void checkNextBtnEnable() {
    // Icdm -729 if label file selected or Source type is Review file then enable Next Button
    this.wpSelWizPage.setPageComplete(isNextBtnEnabled());
  }


  /**
   * @return true if label file selected or Source type is Review file
   */
  public boolean isNextBtnEnabled() {
    return (!this.wpSelWizPage.getReviewFuncsSet().isEmpty() || checkForCompliAndReview()) &&
        !this.calDataReviewWizard.getCdrWizardUIModel().isExceptioninWizard();
  }

  private boolean checkForCompliAndReview() {
    return this.calDataReviewWizard.getCdrWizardUIModel().getSourceType()
        .equals(CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType()) ||
        this.calDataReviewWizard.getCdrWizardUIModel().getSourceType().equals(CDR_SOURCE_TYPE.REVIEW_FILE.getDbType());
  }
 }
