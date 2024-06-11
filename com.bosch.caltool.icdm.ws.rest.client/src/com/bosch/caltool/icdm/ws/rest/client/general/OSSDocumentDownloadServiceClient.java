/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NDV4KOR
 */
public class OSSDocumentDownloadServiceClient extends AbstractRestServiceClient {

  /**
   * download the iCDM OSS Document
   */
  public OSSDocumentDownloadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ICDM_OSS_DOCUMENT);
  }

  /**
   * @param dirPath directory path for OSS Document to download
   * @return file inform of byte[]
   * @throws ApicWebServiceException Exception from service.
   */
  public byte[] getOSSDocument(final String dirPath) throws ApicWebServiceException {
    downloadFile(getWsBase(), ApicConstants.OSS_DOCUMENT, dirPath);
    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.OSS_DOCUMENT));
    }

    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
  }
}

