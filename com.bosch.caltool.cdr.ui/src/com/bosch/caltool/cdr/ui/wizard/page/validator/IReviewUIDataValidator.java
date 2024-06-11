/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.page.validator;

import org.eclipse.jface.wizard.WizardPage;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;

/**
 * @author say8cob
 */
public interface IReviewUIDataValidator {

  /**
   * @return
   */
  public void validate(final CalDataReviewWizard calDataReviewWizard, final WizardPage page);

}
