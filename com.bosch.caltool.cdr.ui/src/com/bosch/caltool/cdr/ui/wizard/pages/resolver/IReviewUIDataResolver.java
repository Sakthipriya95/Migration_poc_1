/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;

/**
 * @author say8cob
 */
public interface IReviewUIDataResolver {


  /**
   * Method to fille the ui fileds of the page and data model
   *
   * @param calDataReviewWizard
   */
  public void setInput(CalDataReviewWizard calDataReviewWizard);

  /**
   * Method to validate current page and call next page
   */
  public void processNextPressed();

  /**
   * Method to call the previous page
   */
  public void processBackPressed();

  /**
   * @return
   */
  public CalDataReviewWizard getInput();

  public void fillUIData(CalDataReviewWizard calDataReviewWizard);

}
