/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.jobs.AbstractChildJob;

/**
 * Sub job to fetch the A2L Workpackages using iCDM web service call.
 *
 * @author bne4cob
 * @deprecated not used
 */
@Deprecated
public class A2LWorkpkgFetchChildJob extends AbstractChildJob {

  private final A2LEditorDataProvider a2lEditorDp;

  /**
   * Instantiates a new a 2 L workpkg fetch child job.
   *
   * @param a2lEditorDp A2LEditorDataProvider
   */
  public A2LWorkpkgFetchChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Fetching A2L Workpackage Mapping (FC2WP/A2lGroup)");
    this.a2lEditorDp = a2lEditorDp;
  }

  /**
   * Fetch the data using iCDM web service
   * <p>
   * {@inheritDoc} //ICDM-2608
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    try {
      this.a2lEditorDp.getA2lWpMapping();
    }
    catch (IcdmException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().errorDialog("A2l Workpackage responsible not loaded!!  " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
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
