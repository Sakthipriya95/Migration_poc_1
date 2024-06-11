/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ReviewCalDataPageResolver implements IReviewUIDataResolver {

  private final CalDataReviewWizard calDataReviewWizard;

  CDRHandler cdrHandler = new CDRHandler();

  /**
   * Contructor for the class
   *
   * @param calDataReviewWizard as object
   */
  public ReviewCalDataPageResolver(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
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
    ReviewOutput reviewOutput = this.calDataReviewWizard.getCdrWizardUIModel().getReviewOutput();
    if (reviewOutput != null) {
      try {
        this.cdrHandler.deleteReviewResult(reviewOutput.getCdrResult());
        this.calDataReviewWizard.getCdrWizardUIModel().setReviewOutput(null);
        this.calDataReviewWizard.getPrgressWizPage().setPageComplete(false);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
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
    // TODO Auto-generated method stub

  }

}
