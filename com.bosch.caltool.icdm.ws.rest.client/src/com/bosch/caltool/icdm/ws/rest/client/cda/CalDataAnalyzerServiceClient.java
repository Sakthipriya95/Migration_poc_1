/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cda;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CalDataAnalyzerServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public CalDataAnalyzerServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDA, WsCommonConstants.RWS_CAL_DATA_ANALYSIS);
  }


  /**
   * Invoke CalDataAnalyzer service
   *
   * @param caldataAnalyzerFilterModel input model
   * @param outputFilePath path to the output zip file
   * @throws ApicWebServiceException exception
   */
  public void invokeCalDataAnalyzer(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final String outputFilePath)
      throws ApicWebServiceException {
    CDMLogger.getInstance().debug("Asynchronous service : target - {}", getWsBase());
    WebTarget wsTarget = getWsBase();
    InputStream inputStream =
        post(wsTarget, caldataAnalyzerFilterModel, InputStream.class, MediaType.APPLICATION_OCTET_STREAM);


    createZipFile(inputStream, outputFilePath);
  }

  /**
   * @param readEntity
   */
  private void createZipFile(final InputStream readEntity, final String outputFilePath) {

    try {
      Files.copy(readEntity, new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    catch (IOException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
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
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CDA_DISCLAIMER_FILE);
    downloadFile(wsTarget, ApicConstants.CDA_DISCLAIMER_FILE, dirPath);

    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.CDA_DISCLAIMER_FILE));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }

  }

}
