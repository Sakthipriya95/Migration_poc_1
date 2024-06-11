/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.excel.CdrReportDataExport;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

// ICDM-1703
/**
 * @author bru2cob
 */
public class CdrReportExcelExportJob extends Job {

  /**
   * cdr result obj
   */
  private final CdrReportDataHandler cdrData;
  /**
   * location to store the excel
   */
  private final String filePath;
  /**
   * type of excel report
   */
  private final String fileExtn;

  private final boolean isStandardExcelExport;

  /**
   * Creates an instance of this class
   *
   * @param cdrData cdrData
   * @param filePath file location
   * @param fileExtn file type
   * @param isStandardExcelExport as boolean value
   */
  public CdrReportExcelExportJob(final CdrReportDataHandler cdrData, final String filePath, final String fileExtn,
      final boolean isStandardExcelExport) {
    super("Exporting Data Review Report of A2L File - " + cdrData.getPidcA2l().getName());
    this.cdrData = cdrData;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.isStandardExcelExport = isStandardExcelExport;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {


    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Export Data Review Report Data started");
    }
    // Call for export the Cdr Report Data
    final CdrReportDataExport cdrReportDataExport = new CdrReportDataExport(monitor);
    boolean isCDRExportPossible = cdrReportDataExport.exportCdrReportData(this.cdrData, this.filePath, this.fileExtn, this.isStandardExcelExport);

    if(isCDRExportPossible) {
      monitor.beginTask("Export Data Review Report Data", IProgressMonitor.UNKNOWN);


      if (monitor.isCanceled()) {
        final File file = new File(this.filePath + "." + this.fileExtn);
        file.delete();
        CDMLogger.getInstance().info(
            "Export Data Review Report Data of :  " + this.cdrData.getPidcA2l().getName() + " is cancelled",
            Activator.PLUGIN_ID);
        return Status.CANCEL_STATUS;
      }
      monitor.done();

      boolean canOpen = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Data Review Report",
          "Do you want to open the exported file?");

      String filePathWithExtn = this.filePath;
      if (CommonUtils.isEmptyString(FilenameUtils.getExtension(this.filePath))) {
        filePathWithExtn = this.filePath + "." + this.fileExtn;
      }
      if (canOpen) {
        // open the exported excel file
        ExcelCommon.getInstance().openExcelFile(filePathWithExtn, Activator.PLUGIN_ID);
      }

      CDMLogger.getInstance().info("Data Review Report Data of  " + this.cdrData.getPidcA2l().getName() +
          "  exported successfully to file  " + filePathWithExtn, Activator.PLUGIN_ID);
      
      return Status.OK_STATUS;
    }
    
    return Status.CANCEL_STATUS;
  }

}
