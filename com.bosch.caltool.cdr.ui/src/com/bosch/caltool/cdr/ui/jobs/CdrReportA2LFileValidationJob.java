/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.ui.jobs.A2LFileDownloadChildJob;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.jobs.ChildJobFamily;

/**
 * @author say8cob
 */
public class CdrReportA2LFileValidationJob extends Job {


  private final A2LEditorDataProvider a2lEditorDP;

  /**
   * @param name        as input
   * @param a2lEditorDP as input
   */
  public CdrReportA2LFileValidationJob(final String name, final A2LEditorDataProvider a2lEditorDP) {
    super(name);
    this.a2lEditorDP = a2lEditorDP;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    CDMLogger.getInstance().debug("A2L File Validations  ...");
    ChildJobFamily subJobFamily = new ChildJobFamily(this);

    A2LFileDownloadChildJob a2lFileDownloadChildJob = new A2LFileDownloadChildJob(this.a2lEditorDP);
    subJobFamily.add(a2lFileDownloadChildJob);
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

    return Status.OK_STATUS;
  }

}
