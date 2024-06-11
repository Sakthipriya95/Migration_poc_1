/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoProviderClient;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileUploadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Job to upload a2l file to vCDM <br>
 * iCDM-775.
 *
 * @author adn1cob
 */
public class A2LFileUploaderJob extends Job {

  /**
   *
   */
  private static final String RESPONSIBILITIES_ROOT_GRP = "_Responsibilities";

  /**
   *
   */
  private static final String WORKPACKAGE_ROOT_GRP = "_Workpackage";

  /**
   * selected sdom pver.
   */
  private final SdomPVER sdomPver;

  /**
   * selected pver variant.
   */
  private final String varName;

  /**
   * selected pver var revision.
   */
  private final Long varRev;

  /**
   * The a 2 l file path.
   */
  private final String a2lFilePath;
  /**
   * source wp def vers id
   */
  private final Long sourceWpDefVersId;
  /**
   * boolean to copy WP/Resp
   */
  private final boolean copyWpResp;


  /**
   * Constructor.
   *
   * @param rule mutex rule
   * @param a2lFilePath path of a2l file
   * @param sdomPver sdom pver
   * @param varName pver variant
   * @param varRev varRev
   * @param sourceWpDefVersId sourceWpDefVersId
   * @param copyWpResp copyWpResp
   */
  public A2LFileUploaderJob(final MutexRule rule, final String a2lFilePath, final SdomPVER sdomPver,
      final String varName, final Long varRev, final Long sourceWpDefVersId, final boolean copyWpResp) {
    super("Uploading A2L file to vCDM: " + a2lFilePath);
    setRule(rule);
    this.sdomPver = sdomPver;
    this.varName = varName;
    this.varRev = varRev;
    this.a2lFilePath = a2lFilePath;
    this.sourceWpDefVersId = sourceWpDefVersId;
    this.copyWpResp = copyWpResp;
    // log the sdom details entered in the dialog
    CDMLogger.getInstance().debug("Selected Pver details in the dialog" + "\n" + "Sdom pver:" + this.sdomPver +
        "\nVariant" + this.varName + "\nVar revision" + this.varRev);
  }

  /**
   * {@inheritDoc} <br>
   * Starts loading A2L file to vCDM
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    monitor.beginTask("", 100);
    monitor.subTask("Saving A2L contents to database . . .");

    CDMLogger.getInstance().debug("Started loading a2l contents using APIC web server... ");
    monitor.worked(30);
    try {
      // Load the contents using webservice calls
      PidcA2l output = new A2LFileUploadServiceClient().uploadA2LFile(this.a2lFilePath,
          this.sdomPver.getPidcVersion().getId(), this.sdomPver.getPverName(), this.varName, this.varRev);

      PidcA2LBO pidcA2lbo = new PidcA2LBO(output.getId(), null);
      A2LFileLoaderJob job = new A2LFileLoaderJob(new PidcA2LBO(pidcA2lbo.getPidcA2lId(), null), true);
      if (this.sourceWpDefVersId != null) {
        copyA2lMappings(output);
      }
      else {
        A2LFileInfo a2lFileContents = new A2LFileInfoProviderClient().fetchA2LFileInfo(pidcA2lbo.getA2lFile());
        boolean hasRootGrp = false;
        SortedSet<Group> allSortedGroups = a2lFileContents.getAllSortedGroups();
        for (Group group : allSortedGroups) {
          if (group.isRoot() &&
              (group.getName().equals(WORKPACKAGE_ROOT_GRP) || (group.getName().equals(RESPONSIBILITIES_ROOT_GRP)))) {
            hasRootGrp = true;
            break;
          }
        }
        if (hasRootGrp && MessageDialogUtils.getConfirmMessageDialogWithYesNo("Import from groups",
            "Would you like to take GROUP's as WP and RESP ?")) {
          job.setUploadA2lImportGrps(true);
        }
      }
      job.schedule();
      StringBuilder infoMsg = new StringBuilder();
      infoMsg.append(pidcA2lbo.getA2LFileName())
          .append(": A2lFile uploaded successfully! A2lFile editor would be opened");
      if (job.isUploadA2lImportGrps()) {
        infoMsg.append(" after import of Work Package-Responsibility and mappings from A2L Groups!");
      }
      else {
        infoMsg.append("!");
      }
      CDMLogger.getInstance().infoDialog(infoMsg.toString(), Activator.PLUGIN_ID);
      CDMLogger.getInstance().debug("A2l contents loaded for version number " +
          pidcA2lbo.getA2lFile().getVcdmA2lfileId() + " with a2l file id as : " + output.getA2lFileId());
    }
    catch (ApicWebServiceException | IOException | IcdmException e) {
      // Log appropriate error message for this task
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    monitor.worked(60);

    // Done
    monitor.done();
    CDMLogger.getInstance().info("A2L File uploaded successfully to vCDM : " + this.a2lFilePath, Activator.PLUGIN_ID);
    return Status.OK_STATUS;
  }

  /**
   * Copy the mappings from source wp def vers
   *
   * @param output
   * @throws ApicWebServiceException
   */
  private void copyA2lMappings(final PidcA2l output) throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    CopyPar2WpFromA2lInput inputData = new CopyPar2WpFromA2lInput();
    inputData.setDescPidcA2lId(output.getId());
    inputData.setSourceWpDefVersId(this.sourceWpDefVersId);
    inputData.setDerivateFromFunc(this.copyWpResp);
    List<CopyPar2WpFromA2lInput> inputDataList = new ArrayList<>();
    inputDataList.add(inputData);
    servClient.copyA2lWpResp(inputDataList);

  }
}
