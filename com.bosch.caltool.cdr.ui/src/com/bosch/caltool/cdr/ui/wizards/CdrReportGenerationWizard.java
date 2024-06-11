/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.jobs.CdrReportA2LFileValidationJob;
import com.bosch.caltool.cdr.ui.jobs.CdrReportGenerationJob;
import com.bosch.caltool.cdr.ui.wizards.pages.CdrReportGenerationInputWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * This class is to create wizard for generating review report
 *
 * @author mkl2cob
 */
public class CdrReportGenerationWizard extends Wizard {


  /**
   * GenerateRvwReportWizardData
   */
  private final CdrReportGenerationWizardData wizardData;
  /**
   * Selected Pidc Variant will not be null if the action is called to generate data review report for Resp/WP Node
   */
  private final PidcVariant selPidcVar;
  /**
   * A2L Resp will not be null if the action is called to generate data review report for Resp/WP Node
   */
  private final A2lResponsibility a2lResp;
  /**
   * A2L WP will not be null if the action is called to generate data review report for WP Node
   */
  private final A2lWorkPackage a2lWp;

  /**
   * @param pidcA2l PIDC A2L file
   * @param pidcVers Pidc Version
   * @param selPidcVar selected pidc
   * @param a2lWp will not be null if the action is called to generate data review report for WP Node
   * @param a2lResp will not be null if the action is called to generate data review report for WP/Resp Node
   */
  public CdrReportGenerationWizard(final PidcA2l pidcA2l, final PidcVersion pidcVers, final PidcVariant selPidcVar,
      final A2lResponsibility a2lResp, final A2lWorkPackage a2lWp) {
    this.wizardData = new CdrReportGenerationWizardData();
    this.wizardData.setPidcA2l(pidcA2l);
    this.wizardData.setPidcVers(pidcVers);
    this.selPidcVar = selPidcVar;
    this.a2lResp = a2lResp;
    this.a2lWp = a2lWp;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {

    // schedule the review report generation job for each variant
    A2LEditorDataProvider a2lEditorDP;
    try {
      a2lEditorDP = loadA2l();
    }
    catch (IcdmException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return false;
    }

    CdrReportA2LFileValidationJob a2lFileValidationJob =
        new CdrReportA2LFileValidationJob("A2l File Validation Job - " + getPidcA2l().getName(), a2lEditorDP);
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    a2lFileValidationJob.schedule();
    Long a2lRespId = CommonUtils.isNotNull(this.a2lResp) ? this.a2lResp.getId() : null;
    Long a2lWpId = CommonUtils.isNotNull(this.a2lWp) ? this.a2lWp.getId() : null;
    if (this.wizardData.getPidcVaraints() != null) {
      for (PidcVariant pidcVar : this.wizardData.getPidcVaraints()) {
        CdrReportGenerationJob rvwReportJob =
            new CdrReportGenerationJob("Generating Data Review Report - " + getPidcA2l().getName(),
                CdrReportGenerationWizard.this, pidcVar, a2lEditorDP, a2lRespId, a2lWpId);
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        rvwReportJob.schedule();
      }
    }
    else {
      CdrReportGenerationJob rvwReportJob =
          new CdrReportGenerationJob("Generating Data Review Report - " + getPidcA2l().getName(),
              CdrReportGenerationWizard.this, null, a2lEditorDP, a2lRespId, a2lWpId);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      rvwReportJob.schedule();
    }

    return true;
  }

  /**
   * @return A2LEditorDataProvider
   * @throws IcdmException
   */
  private A2LEditorDataProvider loadA2l() throws IcdmException {
    // load a2l data provider
    return new A2LEditorDataProvider(getPidcA2l().getId(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    // validate the fields before finish
    return validateFields();
  }

  /**
   * @return true if 'Generate' button can be enabled
   */
  private boolean validateFields() {
    boolean canEnblGnrtBtn = true;
    if ((this.wizardData.variantsAvailable() && CommonUtils.isNullOrEmpty(this.wizardData.getPidcVaraints())) ||
        (!this.wizardData.isLastReview() && (this.wizardData.getNumOfReviews() < 1))) {
      canEnblGnrtBtn = false;
    }
    return canEnblGnrtBtn;
  }

  /**
   * @return the a2lFile
   */
  public PidcA2l getPidcA2l() {
    return this.wizardData.getPidcA2l();
  }


  @Override
  public void addPages() {
    // Set tile
    setWindowTitle("Calibration Data Review Report");

    StringBuilder pageName = new StringBuilder("Enter the details -");
    pageName.append(getPidcA2l().getName());

    boolean isToCreateRprtForWPRespNode = false;
    if (CommonUtils.isNotNull(this.selPidcVar)) {
      pageName.append("\nVariant Name : ").append(this.selPidcVar.getName());
    }

    if (CommonUtils.isNotNull(this.a2lResp)) {
      isToCreateRprtForWPRespNode = true;
      pageName.append("\n\nResponsibility Name : ").append(this.a2lResp.getName());
    }
    if (CommonUtils.isNotNull(this.a2lWp)) {
      pageName.append("\nWorkPackage Name : ").append(this.a2lWp.getName());
    }

    // add the review report page
    CdrReportGenerationInputWizardPage inputWizardPage =
        new CdrReportGenerationInputWizardPage(pageName.toString(), this.selPidcVar, isToCreateRprtForWPRespNode);

    addPage(inputWizardPage);
  }

  /**
   * getter of wizard data
   *
   * @return GenerateRvwReportWizardData
   */
  public CdrReportGenerationWizardData getWizardData() {
    return this.wizardData;
  }


}
