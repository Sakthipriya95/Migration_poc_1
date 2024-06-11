/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.io.File;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.excel.FC2WPReportDataExport;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * The Class FC2WPExcelExportJob.
 *
 * @author gge6cob
 */
public class FC2WPExcelExportJob extends Job {

  /**
   * location to store the excel
   */
  final private String filePath;

  /**
   * type of excel report
   */
  final private String fileExtn;

  /**
   * map that holds header information
   */
  private final Map<Integer, String> headerMap;

  /**
   * the collection of mapping data
   */
  private final FC2WPMappingResult result;

  private final SortedSet<FC2WPMapping> fc2wpMapFiltered;

  /**
   * Instantiates a new FC2WP excel export job.
   *
   * @param headerMap the header map
   * @param result the result
   * @param filteredData FC2WP data available in table aftering filtering
   * @param filePath the file path
   * @param fileExtn the file extn
   */
  public FC2WPExcelExportJob(final Map<Integer, String> headerMap, final FC2WPMappingResult result,
      final SortedSet<FC2WPMapping> filteredData, final String filePath, final String fileExtn) {
    super("Exporting FC2WP Report Data");
    this.result = result;
    this.fc2wpMapFiltered = filteredData;
    this.headerMap = headerMap;
    this.fileExtn = fileExtn;
    this.filePath = filePath + "." + this.fileExtn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {

    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Export FC2WP mapping Data started");
    }
    final FC2WPReportDataExport fc2wpReportDataExport =
        new FC2WPReportDataExport(monitor, this.result, this.fc2wpMapFiltered);
    fc2wpReportDataExport.exportFC2WPReportData(this.headerMap, this.filePath, this.fileExtn);

    monitor.beginTask("Export FC2WP mapping Data", IProgressMonitor.UNKNOWN);
    final File file = new File(this.filePath);
    if (monitor.isCanceled()) {
      file.delete();
      CDMLogger.getInstance().info("Export FC2WP mapping Data is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("Selected FC2WP mappings exported successfully to file :" + this.filePath,
        Activator.PLUGIN_ID);

    boolean canOpen = MessageDialogUtils.getConfirmMessageDialogWithYesNo("FC-WP List Export",
        "Do you want to open the exported file?");
    if (canOpen) {
      ExcelCommon.getInstance().openExcelFile(this.filePath, Activator.PLUGIN_ID);
    }
    return Status.OK_STATUS;
  }
}
