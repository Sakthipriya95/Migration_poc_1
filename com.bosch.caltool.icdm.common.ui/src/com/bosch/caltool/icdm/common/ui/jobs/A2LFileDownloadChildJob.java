/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.jobs.AbstractChildJob;


/**
 * The Class A2LFileDownloadChildJob.
 *
 * @author bne4cob
 */
public class A2LFileDownloadChildJob extends AbstractChildJob {


  /** Mutex rule for A2L file download. */
  private static final ISchedulingRule SCHRUL_A2L_MUTEX_RULE = new MutexRule();

  /**
   * A2L file contents, downloaded from vCDM service and parsed to the data model using A2L parser. A2L editor data
   * provided is created using file contents
   */
  private A2LEditorDataProvider a2lFileEditorDP;


  /**
   * @deprecated
   */
  /**
   * Constructor.
   *
   * @param a2lFile A2L file
   */
  @Deprecated
  public A2LFileDownloadChildJob(final com.bosch.caltool.icdm.model.a2l.A2LFile a2lFile) {
    super("Downloading A2L File " + a2lFile.getFilename());
    setRule(SCHRUL_A2L_MUTEX_RULE);
  }

  /**
   * Constructor.
   *
   * @param a2lEditorDp A2LEditorDataProvider
   */
  public A2LFileDownloadChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Downloading A2L File " + a2lEditorDp.getPidcA2LBO().getA2LFileName());
    setRule(SCHRUL_A2L_MUTEX_RULE);
    this.a2lFileEditorDP = a2lEditorDp;
  }

  /**
   * {@inheritDoc}
   */
  @Override

  protected IStatus doRun(final IProgressMonitor monitor) {
    // ICDM-2608
    Job.getJobManager().resume();
    // check if the contents is already available in cache of this session
    A2LFileInfo a2lFileContents = null;
    // If A2LContents not available, then get load a2l from the cdm webservice
    if (CommonUtils.isNotNull(monitor)) {
      monitor.beginTask("", 100);
      monitor.subTask("Initialising . . .");
    }
    if (this.a2lFileEditorDP.getPidcA2LBO().getA2lFile() != null) {
      try {
        this.a2lFileEditorDP.getA2lFileInfo();
        a2lFileContents = this.a2lFileEditorDP.getA2lFileInfoBO().getA2lFileInfo();
      }
      catch (IcdmException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      if (null != a2lFileContents) {
        CDMLogger.getInstance().info("A2L File Contents loaded" + a2lFileContents.getA2lFileSize());
      }
    }
    // If A2LContents is still not available, then return status
    if (a2lFileContents == null) {
      CDMLogger.getInstance().errorDialog("Could not retrieve the A2L file!", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    CDMLogger.getInstance()
        .info("Downloading A2L file completed. A2L file : " + this.a2lFileEditorDP.getPidcA2LBO().getA2LFileName());
    return Status.OK_STATUS;
  }
}
