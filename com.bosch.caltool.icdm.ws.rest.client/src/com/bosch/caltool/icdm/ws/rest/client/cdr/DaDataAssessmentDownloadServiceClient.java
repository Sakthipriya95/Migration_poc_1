/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;


import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NDV4KOR
 */
public class DaDataAssessmentDownloadServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public DaDataAssessmentDownloadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_DA_DATA_ASSESSMENT_DOWNLOAD);
  }


  /**
   * @param dataAssessmentId Data Assessment/Baseline ID
   * @return File archival status
   * @throws ApicWebServiceException Exception in case of errors during the service execution
   */
  public String checkFileAvailability(final Long dataAssessmentId) throws ApicWebServiceException {

    LOGGER.debug("Checking the status of file archival in checkFileAvailability()");

    WebTarget wsTargetCheckStatus = getWsBase().path(WsCommonConstants.RWS_CHECK_STATUS)
        .queryParam(WsCommonConstants.RWS_DA_BASELINE_ID, dataAssessmentId);
    String status = get(wsTargetCheckStatus, String.class);

    LOGGER.debug("Baseline file archival status for the baseline with ID {} is : {}", dataAssessmentId, status);

    return status;

  }

  /**
   * @param dataAssessmentId Data Assessment/Baseline ID
   * @param fileName Name of the file to be downloaded
   * @param destDirectory Destination directory where the file needs to be downloaded
   * @throws ApicWebServiceException Exception in case of errors during the service execution
   */
  public void getDataAssessementReport(final Long dataAssessmentId, final String fileName, final String destDirectory)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_DA_BASELINE_ID, dataAssessmentId);
    downloadFile(wsTarget, fileName, destDirectory);
  }


}
