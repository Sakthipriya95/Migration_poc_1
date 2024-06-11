/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author say8cob
 */
public class ReviewFilesSelectionWizardPageValidator {


  private final CalDataReviewWizard calDataReviewWizard;
  private final ReviewFilesSelectionWizardPage reviewFilesSelectionWizardPageNew;


  /**
   * @param calDataReviewWizard
   * @param reviewFilesSelectionWizardPageNew
   */
  public ReviewFilesSelectionWizardPageValidator(final CalDataReviewWizard calDataReviewWizard,
      final ReviewFilesSelectionWizardPage reviewFilesSelectionWizardPageNew) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.reviewFilesSelectionWizardPageNew = reviewFilesSelectionWizardPageNew;
  }

  /**
  *
  */
  public void checkNextBtnEnable() {
    // ICDM-2355
    TableItem[] items = this.reviewFilesSelectionWizardPageNew.getFilesList().getItems();
    for (TableItem tableItem : items) {
      String filePath = tableItem.getText();
      if (!CommonUtils.isEmptyString(FilenameUtils.getFullPath(filePath)) && !CommonUtils.isFileAvailable(filePath)) {
        this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(true);
        tableItem.setForeground(
            this.reviewFilesSelectionWizardPageNew.getTabComp().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
    }
    this.reviewFilesSelectionWizardPageNew.setPageComplete(isNextBtnEnable());
  }

  /**
   * @return true to enable next button
   */
  public boolean isNextBtnEnable() {
    return null != this.reviewFilesSelectionWizardPageNew.getFilesList() &&
        this.reviewFilesSelectionWizardPageNew.getFilesList().getItemCount() != 0 &&
        !this.calDataReviewWizard.getCdrWizardUIModel().isExceptioninWizard();
  }

  /**
   * @return
   */
  public boolean canFlipToNextPage() {
    return (!this.calDataReviewWizard.getCdrWizardUIModel().isExceptioninWizard()) &&
        (null != this.reviewFilesSelectionWizardPageNew.getFilesList()) &&
        (this.reviewFilesSelectionWizardPageNew.getFilesList().getItemCount() > 0);
  }

  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @return the reviewFilesSelectionWizardPageNew
   */
  public ReviewFilesSelectionWizardPage getReviewFilesSelectionWizardPage() {
    return this.reviewFilesSelectionWizardPageNew;
  }


}
