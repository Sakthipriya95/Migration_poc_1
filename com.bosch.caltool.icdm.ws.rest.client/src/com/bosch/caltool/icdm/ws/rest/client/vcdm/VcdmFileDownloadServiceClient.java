/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmFileDownloadServiceClient extends AbstractRestServiceClient {

  /**
   * constructor
   */
  public VcdmFileDownloadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_VCDM, WsCommonConstants.RWS_FILE_DOWNLOAD);
  }

  /**
   * Download file from vCDM
   *
   * @param pidcId pidc id
   * @param vCDMFileId vCDM file id
   * @param destDirectory destination directory to download the vCDM file
   * @param destFileName destination file name
   * @throws ApicWebServiceException error during webservice call
   */
  public void get(final long pidcId, final Long vCDMFileId, final String destFileName, final String destDirectory)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, vCDMFileId);

    downloadFile(wsTarget, destFileName, destDirectory);
  }
}
