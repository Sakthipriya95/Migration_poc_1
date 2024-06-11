/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;


import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service Client for WpFiles Download
 *
 * @author msp5cob
 */
public class WpFilesDownloadServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public WpFilesDownloadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_WP_FILES_DOWNLOAD);
  }


  /**
   * @param wpArchivalId Wp Archival ID
   * @param fileName Name of the file to be downloaded
   * @param destDirectory Destination directory where the file needs to be downloaded
   * @throws ApicWebServiceException Exception in case of errors during the service execution
   */
  public void getWpArchivalFile(final Long wpArchivalId, final String fileName, final String destDirectory)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_WP_ARCHIVAL_ID, wpArchivalId);
    downloadFile(wsTarget, fileName, destDirectory);
  }


}
