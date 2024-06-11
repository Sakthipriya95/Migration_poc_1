/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service client A2LFileDownloadService.
 *
 * @author bne4cob
 */
public class A2LFileDownloadServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public A2LFileDownloadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_DOWNLOAD);
  }


  /**
   * Download a2L file based on vCDM A2L file ID
   *
   * @param vcdmA2lFileId vCDM A2L file ID
   * @param fileName A2L file name
   * @param destDirectory destination directory to download A2L
   * @throws ApicWebServiceException service error
   */
  public void getA2lFileById(final Long vcdmA2lFileId, final String fileName, final String destDirectory)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID, vcdmA2lFileId);
    downloadFile(wsTarget, fileName, destDirectory);
  }

}
