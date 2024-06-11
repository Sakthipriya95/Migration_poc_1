/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

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
 * @author NIP4COB
 */
public class IcdmClientStartupServiceClient extends AbstractRestServiceClient {

  /**
   * constructor
   */
  public IcdmClientStartupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ICDM_CLIENT_STARTUP);
  }


  /**
   * Retrieves the welcome page files
   *
   * @param dirPath path of the directory in which the welcome page files will be downloaded
   * @return byte[]
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getWelcomePageFiles(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_WELCOME_PAGE_FILES);
    downloadFile(wsTarget, ApicConstants.WELCOME_PAGE_FILE, dirPath);
    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.WELCOME_PAGE_FILE));
    }

    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }

  }


  /**
   * Retrieves the disclaimer file
   *
   * @param dirPath directory path
   * @return byte[]
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getDisclaimerFile(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_DISCLAIMER_FILE);
    downloadFile(wsTarget, ApicConstants.DISCLAIMER_FILE, dirPath);

    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.DISCLAIMER_FILE));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }

  }

  /**
   * Retrieves the disclaimer file
   *
   * @param dirPath directory path
   * @return byte[] of hot line files
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getMailtoHotLineFile(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_MAIL_TEMPLATE_FILE);
    downloadFile(wsTarget, ApicConstants.MAIL_TEMPLATE_FILE, dirPath);

    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.MAIL_TEMPLATE_FILE));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }

  }


}
