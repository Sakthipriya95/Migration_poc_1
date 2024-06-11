/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.report.excel.WPLabelAssignmentExport;

/**
 * @author NIP4COB
 */
public class WPLabelExportJob extends Job {

  /**
   *
   */
  private final String filePath;
  /**
   *
   */
  private final String fileExtn;
  /**
   *
   */
  private final A2LWPInfoBO a2lwpInfoBO;

  private final A2lVariantGroup a2lVariantGroup;
  private final Set<A2LWpParamInfo> excelData;

  /**
   * @param a2lwpInfoBO - data handler for the WP-Label Assignment Page
   * @param fileExtn - file extension
   * @param filePath - complete file path
   * @param excelData - data to be exported
   */
  public WPLabelExportJob(final String filePath, final String fileExtn, final A2LWPInfoBO a2lwpInfoBO,
      final Set<A2LWpParamInfo> excelData) {
    super("Exporting Work Package - Parameter mapping");
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.a2lwpInfoBO = a2lwpInfoBO;
    this.a2lVariantGroup = a2lwpInfoBO.getSelectedA2lVarGroup();
    this.excelData = excelData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    WPLabelAssignmentExport wpLabelExport = new WPLabelAssignmentExport();
    wpLabelExport.exportWPLabel(this.a2lwpInfoBO, this.filePath, this.fileExtn, this.a2lVariantGroup, this.excelData);
    return Status.OK_STATUS;
  }

}
