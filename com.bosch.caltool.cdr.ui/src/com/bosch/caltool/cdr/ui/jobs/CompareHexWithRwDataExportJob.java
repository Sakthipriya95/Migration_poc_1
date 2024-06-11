/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.excel.CompareHexWithRwDataExport;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author bru2cob
 */
public class CompareHexWithRwDataExportJob extends Job {

  /**
   * location to store the excel
   */
  private final String filePath;
  /**
   * type of excel report
   */
  private final String fileExtn;
  /**
   * instance of CompHEXWithCDFxData
   */
  private final CompHexWithCDFxDataHandler compData;

  private final boolean isStandardExcelExport;

  /**
   * Creates an instance of this class
   *
   * @param rule mutex rule
   * @param compData CompHEXWithCDFxData
   * @param filePath file location
   * @param fileExtn file type
   * @param isStandardExcelExport as boolean
   */
  public CompareHexWithRwDataExportJob(final MutexRule rule, final CompHexWithCDFxDataHandler compData,
      final String filePath, final String fileExtn, final boolean isStandardExcelExport) {
    super("Hex comparison report Export");
    setRule(rule);
    this.compData = compData;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.isStandardExcelExport = isStandardExcelExport;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    // This fix shows progress monitor
    Job.getJobManager().resume();

    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Export Hex compare report started");
    }

    monitor.beginTask("", AbstractChildJob.JOB_TOTAL);
    monitor.worked(AbstractChildJob.JOB_BEGIN);

    // Call for compare hex export
    final CompareHexWithRwDataExport compareReportExport =
        new CompareHexWithRwDataExport(monitor, this.isStandardExcelExport);
    boolean isCompHexReportExportPossible =
        compareReportExport.exportCompareReportResult(this.compData, this.filePath, this.fileExtn);

    if (isCompHexReportExportPossible) {


      if (monitor.isCanceled()) {
        final File file = new File(this.filePath + "." + this.fileExtn);
        // delete the created file if the user cancels the export operation in between
        file.delete();
        CDMLogger.getInstance().info("Export of Hex comparison report is cancelled", Activator.PLUGIN_ID);
        return Status.CANCEL_STATUS;
      }
      monitor.done();
      CDMLogger.getInstance().info("Hex Compare report exported successfully to file: " + this.filePath,
          Activator.PLUGIN_ID);

      boolean canOpen = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Hex Comparison Report",
          "Do you want to open the exported file?");
      if (canOpen) {
        // open the exported excel file
        ExcelCommon.getInstance().openExcelFile(this.filePath, Activator.PLUGIN_ID);
      }
      return Status.OK_STATUS;
    }
    return Status.CANCEL_STATUS;
  }
}

