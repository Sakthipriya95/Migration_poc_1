/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * JOB class for comparing 2 or more PIDC
 *
 * @author jvi6cob
 */
public class ComparePIDCJob extends Job {


  /**
   * PIDCVersion
   */
  private final PidcVersion pidcVer1;
  /**
   * PIDCVersion
   */
  private final PidcVersion pidcVer2;
  /**
   * PIDCVariant
   */
  private final PidcVariant variant1;
  /**
   * PIDCVariant
   */
  private final PidcVariant variant2;
  /**
   * selected file
   */
  private final String fileSelected;
  /**
   * file extension
   */
  private final String fileExtn;

  /**
   * Constructor
   *
   * @param rule MutexRule
   * @param pidcVer1 PIDCVersion
   * @param pidcVer2 PIDCVersion
   * @param variant1 PIDCVariant
   * @param variant2 PIDCVariant
   * @param fileSelected String
   * @param fileExtn String
   * @param progressInfo String
   */
  public ComparePIDCJob(final MutexRule rule, final PidcVersion pidcVer1, final PidcVersion pidcVer2,
      final PidcVariant variant1, final PidcVariant variant2, final String fileSelected, final String fileExtn,
      final String progressInfo) {
    super(progressInfo);
    setRule(rule);
    this.pidcVer1 = pidcVer1;
    this.pidcVer2 = pidcVer2;
    this.variant1 = variant1;
    this.variant2 = variant2;
    this.fileSelected = fileSelected;
    this.fileExtn = fileExtn;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    String info = "";
    if (this.fileSelected != null) {
      if ((this.pidcVer1 != null) && (this.pidcVer2 != null)) {
        info += " PIDC ";
        monitor.beginTask("Comparing PID Cards", IProgressMonitor.UNKNOWN);
        monitor.done();
      }
      else if ((this.variant1 != null) && (this.variant2 != null)) {
        info += " Variant ";
        monitor.beginTask("Comparing PID Variants", IProgressMonitor.UNKNOWN);
        monitor.done();
      }
      // TODO:Not implemented : this.subVariant1 != null) && (this.subVariant2 != null
    }

    if (monitor.isCanceled()) {
      // when the job is cancelled
      info += "Compare Cancelled";
      CDMLogger.getInstance().info(info, Activator.PLUGIN_ID);


      File file = new File(this.fileSelected + "." + this.fileExtn);

      if (file.delete()) {
        CDMLogger.getInstance().info(file.getName() + " is deleted!", Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().info("Delete operation failed for " + file.getName(), Activator.PLUGIN_ID);
      }
      return Status.CANCEL_STATUS;
    }

    return Status.OK_STATUS;
  }
}
