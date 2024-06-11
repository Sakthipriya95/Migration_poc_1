/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.reports.CreateLISFile;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * iCDM-360
 * 
 * Icdm-521
 * Moved the class to common UI 
 * 
 * @author adn1cob
 */
public class CreateLISFileJob extends Job {

  /**
   * List of parameters to be written to LIS file
   */
  final private List<DefCharacteristic> paramList;
  /**
   * File path
   */
  final private String filePath;
  /**
   * Fil eextn
   */
  final private String fileExtn;

  /**
   * Constructor for creation of LIS file
   * 
   * @param rule
   * @param paramList
   * @param filePath
   * @param fileExtn
   */
  public CreateLISFileJob(final MutexRule rule, final List<DefCharacteristic> paramList, final String filePath,
      final String fileExtn) {
    super("Creating LIS file :");
    setRule(rule);
    this.paramList = paramList;
    this.filePath = filePath;
    this.fileExtn = fileExtn;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {
    monitor.beginTask("Creating LIS file: ", IProgressMonitor.UNKNOWN);
    final CreateLISFile createObj = new CreateLISFile(monitor);
    createObj.createFile(this.paramList, this.filePath);
    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Creation of LIS file is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("Created LIS file in  " + this.filePath, Activator.PLUGIN_ID);
    return Status.OK_STATUS;

  }
}
