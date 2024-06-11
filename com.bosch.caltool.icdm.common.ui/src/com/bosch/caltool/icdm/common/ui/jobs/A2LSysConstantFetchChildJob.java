/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.icdm.client.bo.a2l.A2LDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.rcputils.jobs.AbstractChildJob;

/**
 * Sub job to fetch all A2L system constants and values using iCDM web service call.
 *
 * @author bne4cob
 */
public class A2LSysConstantFetchChildJob extends AbstractChildJob {

  /**
   * Instantiates a new a 2 L sys constant fetch child job.
   */
  public A2LSysConstantFetchChildJob() {
    super("Fetching All A2L System constants ");
  }

  /**
   * Fetch the data using iCDM web service
   * <p>
   * {@inheritDoc} //ICDM-2608
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    // Fetch a2l basecomponents using iCDM web service
    CDMLogger.getInstance().info("Fetching system constants using iCDM web service. ");
    Map<String, A2LSystemConstant> retMap = new A2LDataBO().getA2lSystemConstants();
    CDMLogger.getInstance().info("A2l system constants loaded in cache - count : " + retMap.size());
    return Status.OK_STATUS;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {
    // TODO Auto-generated method stub
    return null;
  }

}
