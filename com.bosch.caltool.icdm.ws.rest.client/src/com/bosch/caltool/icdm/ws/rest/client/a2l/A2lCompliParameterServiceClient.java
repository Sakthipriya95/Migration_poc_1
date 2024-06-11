/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class to fetch compliance parameter in an a2l file
 *
 * @author svj7cob
 */
// Task 263282
public class A2lCompliParameterServiceClient extends AbstractRestServiceClient {


  /**
   * initialize client constructor with the compli-param url
   */
  public A2lCompliParameterServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, "");
  }

  /**
   * Fetch compliance parameter in an a2l file
   *
   * @param a2lFilePath the given a2l-file path
   * @param webflowId the given webflow id
   * @return the response contains the Map with key : parameter name and value : ssd compliance class
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public A2lCompliParameterServiceResponse getA2lCompliParams(final String a2lFilePath, final String webflowId)
      throws ApicWebServiceException {

    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    FormDataMultiPart multipart = null;
    A2lCompliParameterServiceResponse retTypeResponse = null;
    try {

      LOGGER.debug("Fetch A2l Compli Parameter service started...");

      // calls the WS
      final FileDataBodyPart filePart = new FileDataBodyPart(WsCommonConstants.RWS_QP_A2L_FILE, new File(a2lFilePath));

      multipart = (FormDataMultiPart) formDataMultiPart.field(WsCommonConstants.RWS_QP_WEB_FLOW_ID, webflowId)
          .bodyPart(filePart);
      retTypeResponse = post(getWsBase().path(WsCommonConstants.RWS_COMPLI_PARAM), multipart,
          A2lCompliParameterServiceResponse.class);

      LOGGER.debug("Fetch A2l Compli Parameter service ended");
    }
    finally {

      closeResource(multipart);
      closeResource(formDataMultiPart);
    }
    return retTypeResponse;
  }

  /**
   * Fetch compliance parameter in an a2l file with PDF and json file as output(together in a zip file)
   *
   * @param a2lFilePath the given a2l-file path
   * @param webflowId the given webflow id
   * @param outputPath path to which zip file will be stored
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public void getA2lCompliParamFiles(final String a2lFilePath, final String webflowId, final String outputPath)
      throws ApicWebServiceException {

    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    FormDataMultiPart multipart1 = null;

    try {

      LOGGER.debug("Fetch A2l Compli Parameter service started...");

      // calls the WS
      final FileDataBodyPart filePart = new FileDataBodyPart(WsCommonConstants.RWS_QP_A2L_FILE, new File(a2lFilePath));
      multipart1 = (FormDataMultiPart) formDataMultiPart.field(WsCommonConstants.RWS_QP_WEB_FLOW_ID, webflowId)
          .bodyPart(filePart);

      try (InputStream responseStream = post(getWsBase().path(WsCommonConstants.RWS_COMPLI_PARAM), multipart1,
          InputStream.class, MediaType.APPLICATION_OCTET_STREAM)) {

        Files.copy(responseStream, new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        LOGGER.debug("Fetch A2l Compli Parameter service report creation ended");

      }
      catch (Exception e) {
        throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR,
            "Error while retrieving output file - " + e.getMessage(), e);
      }

    }
    finally {
      closeResource(multipart1);
      closeResource(formDataMultiPart);
    }

  }

}