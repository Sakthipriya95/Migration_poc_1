/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.excel.FocusMatrixExport;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author mkl2cob
 */
public class FocusMatrixExportJob extends Job {


  /**
   * Full path of the file. eg : c:\temp\filename
   */
  final private String filePath;
  /**
   * File extension
   */
  final private String fileExtn;
  private final FocusMatrixDataHandler fmDataHandler;

  /**
   * @param mutexRule MutexRule
   * @param fileSelected String
   * @param fileExtn String
   * @param fmDataHandler FocusMatrixDataHandler
   */
  public FocusMatrixExportJob(final MutexRule mutexRule, final String filePath, final String fileExtn,
      final FocusMatrixDataHandler fmDataHandler) {
    super("Exporting focus matrix.. ");
    this.fmDataHandler = fmDataHandler;
    setRule(mutexRule);
    this.filePath = filePath;
    this.fileExtn = fileExtn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    monitor.beginTask("Exporting focus matrix", IProgressMonitor.UNKNOWN);
    // Call for export the focus matrix
    FocusMatrixExport fmExport = new FocusMatrixExport();
    fmExport.exportFM(this.filePath, this.fileExtn, this.fmDataHandler, monitor);
    // Check for cancellation of Job.
    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Export  of " + " Focus Matrix is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    // Log the message after the Completion
    CDMLogger.getInstance().info(
        "Focus Matrix" + " exported successfully to file " + this.filePath + "." + this.fileExtn, Activator.PLUGIN_ID);
    return Status.OK_STATUS;
  }

}
