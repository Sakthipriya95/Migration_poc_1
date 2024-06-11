/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.A2lWpVariantGrpSelectionDialog;
import com.bosch.caltool.icdm.ui.dialogs.ImportA2lWpRespFromInputFileDialog;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ImportA2lWpRespServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NIP4COB
 */
public class ImportA2lWpRespJob extends Job {

  private final Long pidcVersionId;
  private final Long a2lFileId;
  private final Long selectedWpDefnVersionId;
  private final boolean createParamMapping;
  private FC2WPVersion fc2wpVers;
  private Long varGrpId;

  private final boolean importFromFC2WP;
  private final A2LDetailsStructureModel detailsStrucModel;
  private boolean confirm;
  private int returnValue;
  private final A2LWPInfoBO a2lWPInfoBO;
  private ImportA2lWpRespData excelInput;
  private String selVarGrp;


  /**
   * Instantiates a new import A 2 l wp resp job.
   *
   * @param pidcVersionId pidcVersion Id
   * @param a2lFileId A2LFile Id
   * @param selectedWpDefnVersionId selected workpackage definition id
   * @param createParamMapping createParamMapping
   * @param a2lWPInfoBO the a 2 l WP info BO
   * @param importFromFC2WP the import from FC 2 WP
   */
  public ImportA2lWpRespJob(final Long pidcVersionId, final Long a2lFileId, final Long selectedWpDefnVersionId,
      final boolean createParamMapping, final A2LWPInfoBO a2lWPInfoBO, final boolean importFromFC2WP) {
    super(importFromFC2WP ? "Import Work Package-Responsibility from FC2WP ..."
        : "Import Work Package-Responsibility from Excel File ...");
    this.pidcVersionId = pidcVersionId;
    this.a2lFileId = a2lFileId;
    this.selectedWpDefnVersionId = selectedWpDefnVersionId;
    this.createParamMapping = createParamMapping;
    this.detailsStrucModel = a2lWPInfoBO.getDetailsStrucModel();
    this.a2lWPInfoBO = a2lWPInfoBO;
    this.importFromFC2WP = importFromFC2WP;

  }


  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    Job.getJobManager().resume();
    if (this.importFromFC2WP) {
      return importFc2WP(monitor);
    }
    return importExcel(monitor);
  }

  /**
   * @param monitor
   * @return
   */
  private IStatus importExcel(final IProgressMonitor progressMonitor) {
    progressMonitor.worked(20);
    Display.getDefault().syncExec(() -> this.confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
        "Import WP-Resp from Excel - Confirmation",
        "Work packages and responsibilities are already available in this A2L file. Importing might overwrite some of them. Do you want to continue?"));


    if (!this.confirm) {
      return Status.CANCEL_STATUS;
    }

    progressMonitor.setTaskName("Select Excel file for import.....");

    Display.getDefault().syncExec(() -> {
      // parse Excel list
      ImportA2lWpRespFromInputFileDialog wpRespDialog =
          new ImportA2lWpRespFromInputFileDialog(Display.getDefault().getActiveShell(), this.a2lWPInfoBO);
      this.returnValue = wpRespDialog.open();
      if (this.returnValue == 0) {
        this.excelInput = wpRespDialog.getParsedExcelData();
      }
      this.selVarGrp = wpRespDialog.getSelVarGrpName();
      wpRespDialog.close();
    });

    // Excel parsing failed
    if (this.excelInput == null) {
      return Status.CANCEL_STATUS;
    }
    progressMonitor.worked(40);
    try {
      doImport(progressMonitor);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return Status.OK_STATUS;
  }


  /**
   * @param progressMonitor
   * @return
   */
  private IStatus importFc2WP(final IProgressMonitor progressMonitor) {
    try {
      progressMonitor.worked(20);
      this.fc2wpVers = getActiveFc2wpVersion(progressMonitor);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    if (this.fc2wpVers != null) {

      Display.getDefault().syncExec(() -> this.confirm = MessageDialog.openConfirm(
          Display.getCurrent().getActiveShell(), "Import WP from FC2WP - Confirmation",
          "Work Package(s) will be imported from FC2WP based on the attribute 'iCDM Questionnaire Config' in the PIDC. Responsibilities will not be overwritten.\nClick OK to confirm"));
      if (!this.confirm) {
        return Status.CANCEL_STATUS;
      }
      progressMonitor.setTaskName("Select varint group for import.....");

      Display.getDefault().syncExec(() -> {
        A2lWpVariantGrpSelectionDialog a2lVarGrpSelDialog = new A2lWpVariantGrpSelectionDialog(
            Display.getCurrent().getActiveShell(), this.detailsStrucModel.getA2lVariantGrpMap());
        this.returnValue = a2lVarGrpSelDialog.open();
        if (this.returnValue == 0) {
          ImportA2lWpRespJob.this.varGrpId = a2lVarGrpSelDialog.getVariantGrpId();
        }
        a2lVarGrpSelDialog.close();
      });
      progressMonitor.worked(40);
      try {
        doImport(progressMonitor);
      }
      catch (ApicWebServiceException e) {
        if (ApicConstants.ERRCODE_FC2WP_IMPORT_A2L_WP_ALREADY_EXISTS.equals(e.getErrorCode())) {
          CDMLogger.getInstance().warn(e.getMessage(), e);
          CDMLogger.getInstance().infoDialog(e.getMessage(), Activator.PLUGIN_ID);
        }
        else {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      return Status.OK_STATUS;
    }
    return Status.OK_STATUS;
  }


  /**
   * @param progressMonitor
   * @return active Fc2wp Version
   * @throws ApicWebServiceException
   */
  private FC2WPVersion getActiveFc2wpVersion(final IProgressMonitor progressMonitor) throws ApicWebServiceException {
    FC2WPVersion activeFc2wpVers;
    progressMonitor.setTaskName("Checking for active FC2WP version for the PIDC ...");
    activeFc2wpVers =
        new FC2WPVersionServiceClient().getActiveVersionByPidcVersion(ImportA2lWpRespJob.this.pidcVersionId);
    this.fc2wpVers = activeFc2wpVers;
    return activeFc2wpVers;
  }


  /**
   * @param progressMonitor
   * @param activeFc2wpVers
   * @param variantGrpId
   * @throws ApicWebServiceException
   */
  private void doImport(final IProgressMonitor progressMonitor) throws ApicWebServiceException {
    ImportA2lWpRespResponse response = null;
    // Import
    if (this.importFromFC2WP) {
      progressMonitor.setTaskName("Importing work package(s) from FC2WP is in progress.....");
      ImportA2lWpRespInput input = new ImportA2lWpRespInput();
      input.setA2lFileId(this.a2lFileId);
      input.setVariantGrpId(this.varGrpId == null ? null : this.varGrpId);
      input.setFc2wpVersId(this.fc2wpVers.getId());
      input.setWpDefVersId(this.selectedWpDefnVersionId);
      input.setPidcVersionId(this.pidcVersionId);
      input.setCreateParamMapping(this.createParamMapping);
      response = new ImportA2lWpRespServiceClient().importA2lWpRespFromFC2WP(input,
          this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
    }
    else {
      progressMonitor.setTaskName("Importing work package(s) from Excel is in progress.....");
      this.excelInput.setA2lFileId(this.a2lFileId);
      this.excelInput.setPidcVersionId(this.pidcVersionId);
      this.excelInput.setWpDefVersId(this.selectedWpDefnVersionId);
      response = new ImportA2lWpRespServiceClient().importA2lWpRespFromExcel(this.excelInput,
          this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());

    }
    progressMonitor.worked(90);
    progressMonitor.done();
    int skippedParams = 0;
    if ((this.excelInput != null) && (this.excelInput.getParamWpRespMap() != null)) {
      skippedParams = this.excelInput.getParamWpRespMap().size() - response.getA2lWpParamMappingSet().size();
    }
    if (skippedParams == 0) {
      skippedParams = response.getSkippedParams().size();
    }
    StringBuilder infoMsg = new StringBuilder();
    String wpDefLevel = "<DEFAULT>";
    if (this.selVarGrp != null) {
      wpDefLevel = this.selVarGrp;
    }
    infoMsg.append("Data import completed for Work Package definition level '").append(wpDefLevel).append("'")
        .append("\n");
    if (this.importFromFC2WP) {
      infoMsg.append("using FC2WP : ").append(this.fc2wpVers.getName()).append("\n");
    }
    infoMsg.append("\nWP definitions created : ").append(response.getWpRespPalSet().size());
    infoMsg.append("\nWP-Parameter mappings created : ").append(response.getA2lWpParamMappingSet().size());
    infoMsg.append("\nResponsibilities modified: ").append(response.getRespSet().size());
    infoMsg.append("\nTotal WP-Parameter mappings skipped : ").append(skippedParams);
    if (CommonUtils.isNotEmpty(response.getSkippedParams())) {
      infoMsg.append("\nWP-Parameter mappings skipped(WP definition not customized at this level) : ")
          .append(response.getSkippedParams().size());
    }
    CDMLogger.getInstance().infoDialog(infoMsg.toString(), Activator.PLUGIN_ID);
  }

}
