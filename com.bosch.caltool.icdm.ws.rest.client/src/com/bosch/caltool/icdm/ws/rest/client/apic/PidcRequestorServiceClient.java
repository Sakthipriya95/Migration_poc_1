/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class PidcRequestorServiceClient extends AbstractRestServiceClient {

  /**
   * constructor
   */
  public PidcRequestorServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_PIDC_REQUESTOR);
  }


  /**
   * Retrieves the pidcrequestor file
   *
   * @return byte[]
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getPidcRequestorFile(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ICDM_PIDC_REQUESTOR_FILE);
    downloadFile(wsTarget, ApicConstants.ICDM_PIDC_REQUESTOR_FILE_NAME, dirPath);
    try {
      return IOUtils
          .toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.ICDM_PIDC_REQUESTOR_FILE_NAME));
    }

    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }


  }
}
