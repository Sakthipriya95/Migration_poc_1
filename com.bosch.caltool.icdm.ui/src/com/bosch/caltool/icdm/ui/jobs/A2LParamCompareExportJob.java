/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.a2l.A2LCompareHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.excel.A2LParamCompareExport;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author apj4cob
 */
public class A2LParamCompareExportJob extends Job {

  /**
   * Complete file path for the excel file being exported
   */
  private final String filePath;
  /**
   * extension of the excel file
   */
  private final String fileExtn;
  /**
   * Map to create excel column header
   */
  private final Map<Integer, String> propertyToLabelMap;
  /**
   * Collection of data to populate excel
   */
  private Set<A2lParamCompareRowObject> excelData = new HashSet<>();
  /**
   * A2l Compare Handler
   */
  private final A2LCompareHandler paramCompareHandler;

  /**
   * @param filePath -Complete file path for the excel file being exported
   * @param fileExtn -extension of the excel file
   * @param propertyToLabelMap -map to form excel column
   * @param excelData -data to be exported to excel file
   * @param paramCompareHandler A2LCompareHandler
   */
  public A2LParamCompareExportJob(final String filePath, final String fileExtn,
      final Map<Integer, String> propertyToLabelMap, final Set<A2lParamCompareRowObject> excelData,
      final A2LCompareHandler paramCompareHandler) {
    super("Exporting A2L Parameter Compare");
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.excelData = excelData;
    this.propertyToLabelMap = propertyToLabelMap;
    this.paramCompareHandler = paramCompareHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Export of compare A2l file data has started");
    }
    A2LParamCompareExport a2LParamCompareExport = new A2LParamCompareExport(monitor);
    a2LParamCompareExport.exportA2LParamCompare(this.propertyToLabelMap, this.filePath, this.fileExtn, this.excelData,
        this.paramCompareHandler);
    monitor.beginTask("Export Compare A2l Data", IProgressMonitor.UNKNOWN);
    final File file = new File(this.filePath);
    if (monitor.isCanceled()) {
      file.delete();
      CDMLogger.getInstance().info("Export of compare A2l file data is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info(
        "A2L File data chosen for comparision has been exported successfully to file :" + this.filePath,
        Activator.PLUGIN_ID);

    boolean canOpen = MessageDialogUtils.getConfirmMessageDialogWithYesNo("A2l Compare Export",
        "Do you want to open the exported file?");
    if (canOpen) {
      ExcelCommon.getInstance().openExcelFile(this.filePath, Activator.PLUGIN_ID);
    }
    return Status.OK_STATUS;
  }
}
