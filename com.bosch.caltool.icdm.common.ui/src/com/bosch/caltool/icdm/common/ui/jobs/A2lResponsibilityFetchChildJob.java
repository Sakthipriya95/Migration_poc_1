/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.rcputils.jobs.AbstractChildJob;


/**
 * @author pdh2cob
 */
public class A2lResponsibilityFetchChildJob extends AbstractChildJob {

  /**
   * Editor data provider
   */
  private final A2LEditorDataProvider a2lEditorDp;

  /**
   * @param a2lEditorDp - Editor data provider
   */
  public A2lResponsibilityFetchChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Fetching PIDC WP Resp list for PIDC");
    this.a2lEditorDp = a2lEditorDp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {
    // Fetch A2l Responsibility Model
    this.a2lEditorDp.getA2lWpInfoBO().loadRespForPidc();
    return Status.OK_STATUS;
  }

}
