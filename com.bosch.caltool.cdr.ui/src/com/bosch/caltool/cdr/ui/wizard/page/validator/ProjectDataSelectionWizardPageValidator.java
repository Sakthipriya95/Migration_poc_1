/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import org.eclipse.jface.wizard.WizardPage;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;

/**
 * @author say8cob
 */
public class ProjectDataSelectionWizardPageValidator implements IReviewUIDataValidator {

  private CalDataReviewWizard calDataReviewWizard;
  private ProjectDataSelectionWizardPage projectDataSelectionWizardPage;


  /**
   * @param wizard CalDataReviewWizard
   * @param projectDataSelectionWizardPage ProjectDataSelectionWizardPage
   */
  public ProjectDataSelectionWizardPageValidator(final CalDataReviewWizard wizard,
      final ProjectDataSelectionWizardPage projectDataSelectionWizardPage) {
    this.calDataReviewWizard = wizard;
    this.projectDataSelectionWizardPage = projectDataSelectionWizardPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(final CalDataReviewWizard wizard, final WizardPage page) {
    this.calDataReviewWizard = wizard;
    this.projectDataSelectionWizardPage = (ProjectDataSelectionWizardPage) page;
  }


  /**
   * This method is to enable the next button
   */
  public void checkNextBtnEnable() {
    this.projectDataSelectionWizardPage.setPageComplete(validateFields());

  }

  /**
   * This method validates the mandatory fields
   *
   * @return validation status
   */
  public boolean validateFields() {
    boolean result = false;
    boolean includeOffReview = true;
    boolean includeStartReview = true;
    final String varaintNameCheck = this.projectDataSelectionWizardPage.getVariantName().getText();
    final String auditorNameCheck = this.projectDataSelectionWizardPage.getAuditor().getText();
    final String descriptionCheck = this.projectDataSelectionWizardPage.getDescriptions().getText();
    if (this.projectDataSelectionWizardPage.getOffRevCheckBox() != null) {
      includeOffReview = this.projectDataSelectionWizardPage.getOffRevCheckBox().getSelection();
    }
    if (this.projectDataSelectionWizardPage.getStartRevCheckBox() != null) {
      includeStartReview = this.projectDataSelectionWizardPage.getStartRevCheckBox().getSelection();
    }
    result = validateEmptyFeilds(result, includeOffReview, includeStartReview, varaintNameCheck, auditorNameCheck,
        descriptionCheck, this.calDataReviewWizard.isHasVariant());
    return result;
  }


  /**
   * @param result
   * @param includeOffReview
   * @param includeStartReview
   * @param varaintNameCheck
   * @param auditorNameCheck
   * @param descriptionCheck
   * @param hasVariant
   * @return
   */
  private boolean validateEmptyFeilds(final boolean result, final boolean includeOffReview,
      final boolean includeStartReview, final String varaintNameCheck, final String auditorNameCheck,
      final String descriptionCheck, final boolean hasVariant) {
    boolean resultVal = result;
    boolean isAuditorDescVarNameValid = !"".equals(auditorNameCheck.trim()) && !"".equals(descriptionCheck.trim()) &&
        ((!"".equals(varaintNameCheck.trim())) || (!hasVariant));
    if (isAuditorDescVarNameValid && (includeOffReview || includeStartReview) && validateIsDeltaReview()) {
      resultVal = true;
    }
    return resultVal;
  }


  /**
   * @return
   */
  private boolean validateIsDeltaReview() {
    return (this.calDataReviewWizard.isProjectDataDeltaReview() &&
        (this.calDataReviewWizard.getCdrWizardUIModel().getA2lFileId() != null)) ||
        ((this.calDataReviewWizard.getCdrWizardUIModel().getA2lFileId() != null) &&
            (this.calDataReviewWizard.getCdrWizardUIModel().getPidcA2lId() != null));
  }

}
