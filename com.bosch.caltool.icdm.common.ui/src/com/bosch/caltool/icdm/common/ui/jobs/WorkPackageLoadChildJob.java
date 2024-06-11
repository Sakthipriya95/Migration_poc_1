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
 * @author nip4cob
 */
public class WorkPackageLoadChildJob extends AbstractChildJob {

  /**
   * Editor data provider
   */
  private final A2LEditorDataProvider a2lEditorDp;

  /**
   */
  public WorkPackageLoadChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Fetching Workpackages Mapped to PidcVersion");
    this.a2lEditorDp = a2lEditorDp;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {
    this.a2lEditorDp.getA2lWpInfoBO().loadWpMappedToPidcVers();
    return Status.OK_STATUS;
  }

}
