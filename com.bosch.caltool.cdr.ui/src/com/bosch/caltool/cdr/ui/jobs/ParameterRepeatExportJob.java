/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData;
import com.bosch.caltool.icdm.report.excel.ParameterRepeatExport;


/**
 * ICDM 636 This class represents the job to export the information about parameters repeated
 *
 * @author mkl2cob
 */
public class ParameterRepeatExportJob extends Job {

  /**
   * Info to be present in the excel
   */
  List<com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData> paramsRepeated;
  /**
   * location to store the excel
   */
  final private String filePath;
  /**
   * type of excel report
   */
  final private String fileExtn;

  /**
   * Constructor
   *
   * @param rule MutexRule
   * @param paramRepeated List of info about parameters repeated
   * @param filePath String which contains file path
   * @param fileExtn String which contains extension of the file
   */
  public ParameterRepeatExportJob(final MutexRule rule, final List<ParamRepeatExcelData> paramRepeated,
      final String filePath, final String fileExtn) {
    super("Parameters repeated");
    setRule(rule);
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.paramsRepeated = paramRepeated;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    final ParameterRepeatExport paramRepeatExport = new ParameterRepeatExport(monitor);
    paramRepeatExport.exportParamRepeatInfo(this.paramsRepeated, this.filePath, this.fileExtn);
    monitor.beginTask("Export Repeated Parameters", IProgressMonitor.UNKNOWN);
    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Export Repeated Parameters in " + this.filePath + " is cancelled",
          Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("Repeated Parameters exported successfully to file  " + this.filePath,
        Activator.PLUGIN_ID);
    return Status.OK_STATUS;
  }

}
