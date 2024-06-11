/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.cda.CalDataAnalyzerServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class CalDataAnalyzerInvokationJob extends Job {

  /**
   * instance of CompHEXWithCDFxData
   */
  private final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel;

  private final String outputFilepath;

  /**
   * @param name
   * @param compData
   * @param file
   */
  public CalDataAnalyzerInvokationJob(final String name, final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final String outputFilePath) {
    super(name);
    this.caldataAnalyzerFilterModel = caldataAnalyzerFilterModel;
    this.outputFilepath = outputFilePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {

    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Save CDA output files....");
    }

    monitor.beginTask("Processing...", IProgressMonitor.UNKNOWN);
    monitor.worked(10);
    CalDataAnalyzerServiceClient caldata = new CalDataAnalyzerServiceClient();
    try {
      caldata.invokeCalDataAnalyzer(this.caldataAnalyzerFilterModel, this.outputFilepath);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    monitor.worked(80);

    if (monitor.isCanceled()) {
      try {
        FileUtils.forceDelete(new File(this.outputFilepath));
      }
      catch (IOException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      CDMLogger.getInstance().info("Calibration Data Analysis cancelled!", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("Calibration Data Analysis successfully completed ", Activator.PLUGIN_ID);
    return Status.OK_STATUS;

  }


  /**
   * @return the outputFilepath
   */
  public String getOutputFilepath() {
    return this.outputFilepath;
  }

}
