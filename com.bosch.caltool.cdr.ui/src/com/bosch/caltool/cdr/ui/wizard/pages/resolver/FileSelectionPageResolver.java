/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.FileSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class FileSelectionPageResolver implements IReviewUIDataResolver {

  private CalDataReviewWizard calDataReviewWizard;

  final StringBuilder progressMessage = new StringBuilder("Fetching Review Result for Delta Review...");

  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {
    FileSelectionWizardPage a2lSelWizPage = this.calDataReviewWizard.getA2lSelWizPage();
    try {
      if (this.calDataReviewWizard.isDeltaReview()) {
        // To load Hasvariant variable
        this.calDataReviewWizard.setHasVaraint(a2lSelWizPage.pidcTreeNode.getReviewResult().getPidcVersionId(), false);
      }
      else {
        // To load Hasvariant variable
        this.calDataReviewWizard.setHasVaraint(a2lSelWizPage.pidcTreeNode.getPidcVersion().getId(), false);
        //to add the OBD Flag for Normal Review
        this.calDataReviewWizard.getCdrWizardUIModel().setPidcDivAppForOBDOpt(
            new CDRHandler().isDivIdAppForOBDOption(a2lSelWizPage.pidcTreeNode.getPidcVersion().getId()));
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    if (this.calDataReviewWizard.isDeltaReview()) {
      try {
        this.calDataReviewWizard.getContainer().run(true, false, new IRunnableWithProgress() {

          @Override
          public void run(final IProgressMonitor monitor) {
            monitor.beginTask(FileSelectionPageResolver.this.progressMessage.toString(), 100);
            monitor.worked(50);
            // To Load the A2l Editor Data Provider
            setCdrWizardUIModel(a2lSelWizPage.getSelectedRvwResId());
            monitor.worked(70);
            monitor.done();
          }
        });
      }
      catch (InvocationTargetException | InterruptedException e1) {
        CDMLogger.getInstance().errorDialog(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
      }

      this.calDataReviewWizard.getProjectSelWizPage().getProjectDataSelectionPageResolver()
          .fillUIData(this.calDataReviewWizard);
    //to add the OBD Flag for Delta Review
      this.calDataReviewWizard.getCdrWizardUIModel().setPidcDivAppForOBDOpt(
          new CDRHandler().isDivIdAppForOBDOption(a2lSelWizPage.pidcTreeNode.getReviewResult().getPidcVersionId()));
    }
    else {

      // Validation For Next Page (Project Data Selection Page)

      this.calDataReviewWizard.getProjectSelWizPage().setVariantDecorator(this.calDataReviewWizard.isHasVariant());
    }

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
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {

    this.calDataReviewWizard = calDataReviewWizard;
    FileSelectionWizardPage selectionWizardPageNew = this.calDataReviewWizard.getA2lSelWizPage();
    if (!this.calDataReviewWizard.isDeltaReview()) {
      // To check Whether the review is normal review
      if (selectionWizardPageNew.pidcTreeNode.getNodeType().getUiType()
          .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_A2L.getUiType()) && (!calDataReviewWizard.isDeltaReview())) {
        // SSD SW Version Id for SSDRuleSelection Page
        Long ssdSoftwareVersionId = selectionWizardPageNew.pidcTreeNode.getPidcA2l().getSsdSoftwareVersionId();
        calDataReviewWizard.getCdrWizardUIModel().setSsdSWVersionId(ssdSoftwareVersionId);

        calDataReviewWizard.getCdrWizardUIModel()
            .setPidcA2lId(selectionWizardPageNew.pidcTreeNode.getPidcA2l().getId());
        calDataReviewWizard.getCdrWizardUIModel()
            .setA2lFileId(selectionWizardPageNew.pidcTreeNode.getPidcA2l().getA2lFileId());
        calDataReviewWizard.getCdrWizardUIModel()
            .setSelectedPidcVerId(selectionWizardPageNew.pidcTreeNode.getPidcVersion().getId());


        // Start Setting Next Page (Project Data Selection Page) Data
        this.calDataReviewWizard.getProjectSelWizPage()
            .setProjectName(selectionWizardPageNew.pidcTreeNode.getPidcVersion().getName());
        if (!this.calDataReviewWizard.getProjectSelWizPage().getStartRevRadio().getSelection() &&
            !this.calDataReviewWizard.getProjectSelWizPage().getTestRevRadio().getSelection() &&
            !this.calDataReviewWizard.getProjectSelWizPage().getOffRevRadio().getSelection()) {
          this.calDataReviewWizard.getProjectSelWizPage().getStartRevRadio().setSelection(true);
        }
        this.calDataReviewWizard.setA2lEditiorDataHandler(null);
        // Setting PidcA2lBO
        PidcA2LBO pidcA2LBO = new PidcA2LBO(selectionWizardPageNew.pidcTreeNode.getPidcA2l().getId(), null);
        calDataReviewWizard.setPidcA2LBO(pidcA2LBO);

        // End of Setting Next Page (Project Data Selection Page) Data

      }

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    return this.calDataReviewWizard;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard calDataReviewWizard) {
    // TODO Auto-generated method stub

  }

  /**
  *
  */
  private void setCdrWizardUIModel(final Long resultID) {
    PidcTreeNodeHandler pidcTreeNodeHandler = new PidcTreeNodeHandler(true);

    if (resultID != null) {
      CDRWizardUIModel reviewResultForDeltaReview;
      try {
        reviewResultForDeltaReview = new CDRHandler().getReviewResultForDeltaReview(resultID);
        if (reviewResultForDeltaReview != null) {
          this.calDataReviewWizard.setCdrWizardUIModel(reviewResultForDeltaReview);
          this.calDataReviewWizard.setParentCDRResultId(resultID);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    if (this.calDataReviewWizard.getCdrWizardUIModel().getPidcA2lId() != null) {
      PidcA2LBO pidcA2LBO = new PidcA2LBO(this.calDataReviewWizard.getCdrWizardUIModel().getPidcA2lId(), null);
      this.calDataReviewWizard.setPidcA2LBO(pidcA2LBO);
    }
    this.calDataReviewWizard.setPidcTreeNodeHandler(pidcTreeNodeHandler);

  }


}
