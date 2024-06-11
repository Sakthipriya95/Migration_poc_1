/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ImportA2lWpRespServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;


/**
 * @author pdh2cob
 */
public class A2lWPDefnVersionFetchChildJob extends AbstractChildJob {

  /**
   * Editor data provider
   */
  private final A2LEditorDataProvider a2lEditorDp;

  private final boolean uploadA2lImportGrps;

  /**
   * @param a2lEditorDp - Editor data provider
   * @param uploadA2lImportGrps - true if groups to be imported during a2l upload
   */
  public A2lWPDefnVersionFetchChildJob(final A2LEditorDataProvider a2lEditorDp, final boolean uploadA2lImportGrps) {
    super("Fetching WP Defintion definitions for PIDCA2l");
    this.a2lEditorDp = a2lEditorDp;
    this.uploadA2lImportGrps = uploadA2lImportGrps;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {

    // Fetch A2lWpDefinitionVersion
    CDMLogger.getInstance().info("Fetching A2lWpDefinitionVersion using iCDM web service. ");
    if (!checkIfA2lWpDefExists()) {
      try {
        createA2lWpDefinitionVersion();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        return Status.CANCEL_STATUS;
      }
    }
    Map<Long, A2lWpDefnVersion> retMap = this.a2lEditorDp.getA2lWpInfoBO().loadWPDefnVersionsForA2l();
    CDMLogger.getInstance().info("A2lWpDefinitionVersion loaded - count : " + retMap.size());
    CDMLogger.getInstance().info("Fetching A2lWpDefinitionModel using iCDM web service. ");

    return Status.OK_STATUS;
  }

  /**
   * create a wp def version working set if it's not available
   *
   * @param a2lwpInfoBO
   * @throws ApicWebServiceException
   */
  private void createA2lWpDefinitionVersion() throws ApicWebServiceException {

    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

    a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
    a2lWpDefnVersion.setActive(false);
    a2lWpDefnVersion.setWorkingSet(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
    a2lWpDefnVersion.setPidcA2lId(this.a2lEditorDp.getPidcA2LBO().getPidcA2lId());
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
    // Creates working set - default Resp Pal, Pidc Resps & Param mapping
    a2lWpDefnVersion = client.create(a2lWpDefnVersion, this.a2lEditorDp.getPidcA2LBO().getPidcA2l());
    // import groups if chosen during a2l upload
    if (this.uploadA2lImportGrps) {
      new ImportA2lWpRespServiceClient()
          .a2lWpRespGrpsImport(this.a2lEditorDp.getA2lWpInfoBO().getPidcA2lBo().getPidcA2l(), a2lWpDefnVersion.getId());
      CDMLogger.getInstance().infoDialog(
          "Work Package-Responsibility and mappings from A2L Groups are imported successfully!", Activator.PLUGIN_ID);
    }
    CDMLogger.getInstance().info(
        "WorkingSet with default Work Package-Responsibility defintion has been created for the selected A2L file",
        Activator.PLUGIN_ID);
  }

  /**
   * Check if A 2 l wp def exists.
   *
   * @return true if a2l wp def exists
   */
  public boolean checkIfA2lWpDefExists() {
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
    try {
      Map<Long, A2lWpDefnVersion> wpDefnVersForPidcA2l =
          client.getWPDefnVersForPidcA2l(this.a2lEditorDp.getPidcA2LBO().getPidcA2lId());
      return (wpDefnVersForPidcA2l != null) && !wpDefnVersForPidcA2l.isEmpty();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return true;
  }
}
