/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditor;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.cdr.ui.wizards.CdrReportGenerationWizard;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.A2lWPDefnVersionFetchChildJob;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.rcputils.jobs.ChildJobFamily;


/**
 * Review report generation job.
 * <p>
 * Steps: <br>
 * a) download a2l File<br>
 * b) fetch additional information(e.g. Fc2WP) related to a2l file from iCDM system<br>
 * c) call cdr report web service to get review related details of the a2l parameters<br>
 * d) create a2lParameter objects<br>
 * e) open CDR Report editor<br>
 *
 * @author mkl2cob
 */
public class CdrReportGenerationJob extends Job {


  /**
   * wizard instance
   */
  private final CdrReportGenerationWizard generateRvwReportWizard;
  /**
   * pidc Variant
   */
  private final PidcVariant pidcVar;

  private final A2LEditorDataProvider a2lEditorDP;
  private final Long selA2lRespId;
  private final Long selA2lWpId;


  /**
   * @param name Job name
   * @param generateRvwReportWizard GenerateRvwReportWizard
   * @param pidcVar as input
   * @param a2lEditorDP as input
   * @param a2lWpId A2L WP Id
   * @param a2lRespId A2L Resp Id
   * @param isRprtGenFrmA2LStructView is Rprt Generated From A2L Structure View
   */
  public CdrReportGenerationJob(final String name, final CdrReportGenerationWizard generateRvwReportWizard,
      final PidcVariant pidcVar, final A2LEditorDataProvider a2lEditorDP, final Long a2lRespId, final Long a2lWpId) {
    super(name);
    this.generateRvwReportWizard = generateRvwReportWizard;
    this.pidcVar = pidcVar;
    this.a2lEditorDP = a2lEditorDP;
    this.selA2lRespId = a2lRespId;
    this.selA2lWpId = a2lWpId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {

    CDMLogger.getInstance().debug("Opening Calibration Data Review Report ...");

    PidcA2l pidcA2l = this.generateRvwReportWizard.getPidcA2l();


    ChildJobFamily subJobFamily = new ChildJobFamily(this);

    // A2L WP Definition fetch job
    A2lWPDefnVersionFetchChildJob wpDefnFetchJob = new A2lWPDefnVersionFetchChildJob(this.a2lEditorDP, false);
    subJobFamily.add(wpDefnFetchJob);

    // Review report fetch job
    PidcVariant pidcVariant = this.pidcVar;
    int maxReviews = this.generateRvwReportWizard.getWizardData().getNumOfReviews();
    boolean fetchCheckVal = this.generateRvwReportWizard.getWizardData().isFetchCheckVal();
    RvwReportDataFetchChildJob propFetchJob = new RvwReportDataFetchChildJob(pidcA2l, pidcVariant, maxReviews,
        fetchCheckVal, this.selA2lRespId, this.selA2lWpId);
    subJobFamily.add(propFetchJob);

    // questionnaire status constructing jobs
    WPRespQnaireRespeVersStatusJob qnaireRespeVersStatusJob =
        new WPRespQnaireRespeVersStatusJob(pidcA2l.getPidcVersId(),
            CommonUtils.isNull(pidcVariant) || CommonUtils.isEqual(pidcVariant.getId(), ApicConstants.NO_VARIANT_ID)
                ? null : pidcVariant.getId(),
            pidcA2l.getName());
    subJobFamily.add(qnaireRespeVersStatusJob);

    // Execute Jobs and wait until all of them are completed
    try {
      subJobFamily.execute(monitor);
    }
    catch (OperationCanceledException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
      return Status.CANCEL_STATUS;
    }

    if (!subJobFamily.isResultOK()) {
      return Status.CANCEL_STATUS;
    }

    final CdrReport parmRvwObject = propFetchJob.getParmRvwObject();

    // Set fetch Param Prop map
    this.a2lEditorDP.getA2lFileInfoBO().setParamProps(parmRvwObject.getParamPropsMap());

    // ICDM-2605 - A2lDataProvider , ApicDataProvider added as arguments
    parmRvwObject.setPidcA2l(pidcA2l);
    parmRvwObject.setPidcVersion(this.generateRvwReportWizard.getWizardData().getPidcVers());

    CdrReportDataHandler reportData = new CdrReportDataHandler(this.a2lEditorDP, pidcVariant, maxReviews, parmRvwObject,
        fetchCheckVal, this.selA2lRespId, this.selA2lWpId);

    // filling CdrReportQnaireRespWrapper
    reportData.setCdrReportQnaireRespWrapper(qnaireRespeVersStatusJob.getCdrReportQnaireRespWrapper());

    final CdrReportEditorInput cdrReportInput = new CdrReportEditorInput();
    cdrReportInput.setCDRRportData(reportData);

    // Calculate WP Finished Status for Data Review Report,Compare Hex and update A2lResponsibily with latest status in
    // DB
    reportData.calWpFinStatusAndFillWPFinStatusMap();
    openReportEditor(cdrReportInput);

    return Status.OK_STATUS;
  }

  /**
   * Open the report editor with the created Editor input
   *
   * @param cdrReportInput CDRDataReportEditorInput
   */
  private void openReportEditor(final CdrReportEditorInput cdrReportInput) {
    Display.getDefault().asyncExec(() -> {

      try {
        CDMLogger.getInstance().debug("Opening the data review report editor with the collected inputs...");
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(cdrReportInput, CdrReportEditor.EDITOR_ID);
        if (openEditor instanceof CdrReportEditor) {
          CdrReportEditor cdrReportEditor = (CdrReportEditor) openEditor;

          // set focus to the editor opened
          cdrReportEditor.setFocus();
          CDMLogger.getInstance().debug("Calibration Data Review Report opened in editor");
        }

      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    });
  }

}
