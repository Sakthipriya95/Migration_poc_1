/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdrReportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;


/**
 * Child job to fetch review report using iCDM web service
 *
 * @author bne4cob
 */
public class RvwReportDataFetchChildJob extends AbstractChildJob {

  /**
   * A2L File
   */
  private final PidcA2l pidcA2l;

  /**
   * PIDC Variant, can be null
   */
  private final PidcVariant variant;

  /**
   * Maximum reviews to be considered per parameter
   */
  private final int maxReviews;

  /**
   * Output object
   */
  private CdrReport parmRvwObject;
  /**
   * flag to fecth checkvalue or not
   */
  private final boolean fetchCheckVal;

  private final Long a2lRespId;

  private final Long a2lWpId;


  /**
   * Constructor
   *
   * @param pidcA2l A2L File
   * @param variant variant, null if no variant is selected
   * @param maxReviews max Reviews
   * @param fetchCheckVal if true, fetches the checked value also via web service
   * @param a2lWpId A2L WP Id
   * @param a2lRespId A2l Resp Id
   */
  public RvwReportDataFetchChildJob(final PidcA2l pidcA2l, final PidcVariant variant, final int maxReviews,
      final boolean fetchCheckVal, final Long a2lRespId, final Long a2lWpId) {

    super("Fetching Data Report from server for A2L file : " + pidcA2l.getName());

    this.pidcA2l = pidcA2l;
    this.variant = variant;
    this.maxReviews = maxReviews;
    this.fetchCheckVal = fetchCheckVal;
    this.a2lRespId = a2lRespId;
    this.a2lWpId = a2lWpId;
  }

  /**
   * Invoke the rest web service to fetch the data review details
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(final IProgressMonitor monitor) {

    Long variantID =
        CommonUtils.isNull(this.variant) || CommonUtils.isEqual(this.variant.getId(), ApicConstants.NO_VARIANT_ID)
            ? null : this.variant.getId();

    getLogger().info(
        "Invoking CDR Report service with parameters - pidcA2lId={}; variantID={}; maxReviews={}; fetchCheckVal={}",
        this.pidcA2l.getId(), variantID, this.maxReviews, this.fetchCheckVal);

    // Invokes the web service method. 'variantID' is null, if no variant is selected in the dialog
    try {
      this.parmRvwObject = new CdrReportServiceClient().getCdrReport(this.pidcA2l.getId(), variantID, this.maxReviews,
          this.fetchCheckVal, this.a2lRespId, this.a2lWpId);
    }
    catch (ApicWebServiceException exp) {
      getLogger().errorDialog("Could not retrieve data review information from server" + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
      getLogger().error(exp.getMessage(), exp);
      return Status.CANCEL_STATUS;
    }

    getLogger().info(
        "CDR Report Response received. Number of parameters with properties = {}; Number of parameters with reviews = {}; max reviews/parameter = {}; total reviews fetched = {}",
        this.parmRvwObject.getParamPropsMap().size(), this.parmRvwObject.getParamRvwDetMap().size(),
        this.parmRvwObject.getMaxParamReviewCount(), this.parmRvwObject.getReviewDetMap().size());

    return Status.OK_STATUS;
  }

  /**
   * Get the output of the job
   *
   * @return the parmRvwObject
   */
  public CdrReport getParmRvwObject() {
    return this.parmRvwObject;
  }

  /**
   * Get the logger
   *
   * @return logger for this class
   */
  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }


}
