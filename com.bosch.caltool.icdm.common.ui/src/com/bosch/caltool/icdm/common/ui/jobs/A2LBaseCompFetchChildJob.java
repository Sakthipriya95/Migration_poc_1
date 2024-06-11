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


// TODO: Auto-generated Javadoc
/**
 * Sub job to fetch the A2L basecomponents using iCDM web service call.
 *
 * @author bne4cob
 */
public class A2LBaseCompFetchChildJob extends AbstractChildJob {

  private final A2LEditorDataProvider a2lEditorDp;

  /**
   * Instantiates a new a 2 L base comp fetch child job.
   *
   * @param a2lEditorDp A2LEditorDataProvider
   */
  public A2LBaseCompFetchChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Fetching A2L BaseComponents ... ");
    this.a2lEditorDp = a2lEditorDp;
  }

  /**
   * Fetch the data using iCDM web service
   * <p>
   * {@inheritDoc} //ICDM-2608
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    // Fetch the data from A2LEditorDataProvider
    try {
      this.a2lEditorDp.getBaseComponents();
      // Load all a2l base components for WP assignment
      this.a2lEditorDp.getA2lWpInfoBO().loadFuncBcMap();
    }
    catch (IcdmException exp) {
      CDMLogger.getInstance().errorDialog("A2L BaseComponents not loaded" + exp.getMessage(), exp, Activator.PLUGIN_ID);
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
