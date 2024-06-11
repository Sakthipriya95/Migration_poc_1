/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.CaldataAnalyzerPage;

/**
 * @author pdh2cob
 */
public class CaldataLoadFilterJob extends Job {


  private final CaldataAnalyzerPage page;

  private final String filePath;

  /**
   * @param name - name of job
   * @param page - instance of caldata analyzer page
   * @param filePath - filepath from ui
   */
  public CaldataLoadFilterJob(final String name, final CaldataAnalyzerPage page, final String filePath) {
    super(name);
    this.page = page;
    this.filePath = filePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {

    monitor.beginTask("Loading Filter Criteria", IProgressMonitor.UNKNOWN);
    try {
      monitor.worked(30);
      CaldataAnalyzerFilterModel model =
          JsonUtil.toModel(new File(this.filePath), CaldataAnalyzerFilterModel.class);
      monitor.worked(80);
      if (model != null) {
        CaldataLoadFilterJob.this.page.setCaldataAnalyzerFilterModel(model);
        monitor.worked(100);
        monitor.done();
      }
      else {
        return Status.CANCEL_STATUS;
      }

    }
    catch (InvalidInputException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    return Status.OK_STATUS;
  }


}
