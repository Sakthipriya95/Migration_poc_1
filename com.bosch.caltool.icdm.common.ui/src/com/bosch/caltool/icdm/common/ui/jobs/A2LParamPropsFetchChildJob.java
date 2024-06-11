/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.rcputils.jobs.AbstractChildJob;

/**
 * Sub job to fetch the additional properties of A2L parameters using iCDM web service call.
 *
 * @author bne4cob
 */
public class A2LParamPropsFetchChildJob extends AbstractChildJob {

  /** Parameter properties, retrieved by the web service call. */
  private Map<String, ParamProperties> paramProps;

  private final A2LEditorDataProvider a2lEditorDp;

  /**
   * Instantiates a new a 2 L param props fetch child job.
   *
   * @param a2lEditorDp A2LEditorDataProvider
   */
  public A2LParamPropsFetchChildJob(final A2LEditorDataProvider a2lEditorDp) {
    super("Fetching A2L Parameter Properties ... ");
    this.a2lEditorDp = a2lEditorDp;
  }

  /**
   * Fetch the data using iCDM web service
   * <p>
   * {@inheritDoc} //ICDM-2608
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    // Fetch a2l parameter properties using iCDM web service
    CDMLogger.getInstance()
        .info("Fetching a2l parameter properties using iCDM web service. " + this.a2lEditorDp.getA2lFileId());

    try {
      if (!CommonUtils.isNotEmpty(this.a2lEditorDp.getA2lFileInfoBO().getParamProps())) {
        this.a2lEditorDp.getA2lParamProperties();
      }

      // Load all a2l parameters for WP assignment
      if (CommonUtils.isNotEmpty(this.a2lEditorDp.getA2lFileInfoBO().getParamProps())) {
        this.a2lEditorDp.getA2lWpInfoBO().initialiseA2LWParamInfo(
            this.a2lEditorDp.getA2lFileInfoBO().getA2lParamMap(this.a2lEditorDp.getA2lFileInfoBO().getParamProps()));
      }
    }
    catch (IcdmException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().errorDialog("A2L parameter properties not loaded!! " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }

    return Status.OK_STATUS;

  }

  /**
   * Gets the param props.
   *
   * @return the paramProps
   */
  public Map<String, ParamProperties> getParamProps() {
    return this.paramProps;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {
    return null;
  }

}
