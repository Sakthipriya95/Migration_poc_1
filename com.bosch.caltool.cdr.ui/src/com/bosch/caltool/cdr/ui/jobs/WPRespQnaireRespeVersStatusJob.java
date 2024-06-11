/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;

/**
 * @author say8cob
 *
 */
public class WPRespQnaireRespeVersStatusJob extends AbstractChildJob{

  private Long pidcVersId;
  
  private Long variantId;
  
  private CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper;
  
  
  /**
   * @param pidcVersId as PIDC Version Id
   * @param variantId as Variant Id
   * @param a2lFileName as A2l File Name
   */
  public WPRespQnaireRespeVersStatusJob(final Long pidcVersId, final Long variantId, final String a2lFileName) {
    
    super("Fetching Data Report from server for A2L file : " + a2lFileName);

    this.pidcVersId = pidcVersId;
    this.variantId = variantId;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(IProgressMonitor monitor) {
    try {
      cdrReportQnaireRespWrapper = new RvwQnaireResponseServiceClient().getQniareRespVersByPidcVersIdAndVarId(pidcVersId, variantId);
    }
    catch (ApicWebServiceException exp) {
      getLogger().errorDialog("Could not retrieve data review information from server" + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
      getLogger().error(exp.getMessage(), exp);
      return Status.CANCEL_STATUS;
    }
    
    return Status.OK_STATUS;
  }
  
  /**
   * Get the logger
   *
   * @return logger for this class
   */
  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }

  
  /**
   * @return the cdrReportQnaireRespWrapper
   */
  public CdrReportQnaireRespWrapper getCdrReportQnaireRespWrapper() {
    return cdrReportQnaireRespWrapper;
  }

}
