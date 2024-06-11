/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.reports;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDFWriterLogger;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author mga1cob
 */
// ICDM-242
public class CDFFileExport {

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  /**
   * @param monitor defines IProgressMonitor
   */
  public CDFFileExport(final IProgressMonitor monitor) {

    this.monitor = monitor;
  }

  /**
   * @param cdfCalDataObjects defines CDF CalData objects
   * @param filePath defines file path
   */
  public void exportCDF(final Map<String, CalData> cdfCalDataObjects, final String filePath) {
    this.monitor.beginTask("Export as CDF: ", IProgressMonitor.UNKNOWN);
    try {
      CDFWriter cdfWriter = new CDFWriter(cdfCalDataObjects, filePath, CDFWriterLogger.getInstance());
      // invoke this method to generate CDF file
      cdfWriter.writeCalDataToXML();
      // after clicking cancel button in the progress window
      if (this.monitor.isCanceled()) {
        CDMLogger.getInstance().info("Export as CDF is cancelled", Activator.PLUGIN_ID);
      }
    }
    catch (CDFWriterException cdfwe) {
      CDMLogger.getInstance().error(cdfwe.getLocalizedMessage(), cdfwe, Activator.PLUGIN_ID);
      this.monitor.setCanceled(true);
    }
  }
}
