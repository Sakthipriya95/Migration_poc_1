/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.comphex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.util.ZipUtils;

/**
 * Client class to fetch compliance parameter in an a2l file
 */
public class CompHexWithCDFxServiceClient extends AbstractRestServiceClient {

  /**
   * initialize client constructor with the compli-param url
   */
  public CompHexWithCDFxServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_COMPHEX, WsCommonConstants.RWS_REVIEW);
  }

  /**
   * Performs the comparison between the given HEX file and the latest review results applicable to the given A2L file
   *
   * @param metaData the meta data input
   * @return the comp hex result
   * @throws ApicWebServiceException any error in service call
   */
  public CompHexResponse getCompHexResult(final CompHexMetaData metaData) throws ApicWebServiceException {
    CompHexResponse response = null;
    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    FormDataMultiPart multipart = new FormDataMultiPart();
    multiPartList.add(multipart);

    try {
      if (!metaData.isHexFromVcdm() && (null != metaData.getSrcHexFilePath())) {
        String zippedFile = compressFile(metaData.getSrcHexFilePath());
        FileDataBodyPart hexBodyPart = new FileDataBodyPart(WsCommonConstants.HEX_FILE_MUTIPART, new File(zippedFile));
        multipart = (FormDataMultiPart) multipart.bodyPart(hexBodyPart);
        multiPartList.add(multipart);
      }

      multipart.field(WsCommonConstants.RWS_COMP_HEX_META_DATA, metaData, MediaType.APPLICATION_JSON_TYPE);
      multiPartList.add(multipart);
      response = post(getWsBase(), multipart, CompHexResponse.class);

      downloadAllFiles(response.getReferenceId());

      if (!response.isProcessCompleted()) {
        // If process completed flag is false, process was not completed in the server and the error details is
        // available in the respone model. In this case raise ApicWebServiceException
        String errMsg = response.getErrorMsgSet().isEmpty() ? "Error occured in Compare HEX review"
            : String.join("\n", response.getErrorMsgSet());
        throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, errMsg);
      }
    }
    finally {
      closeResource(multiPartList);
    }

    return response;
  }

  private void downloadAllFiles(final String referenceId) {
    if (referenceId == null) {
      LOGGER.warn("Reference ID is null. Nothing to download");
      return;
    }

    // If reference ID is available, start the download in a separate thread
    new Thread(() -> {
      try {
        String downloadToDir = getClientConfiguration().getIcdmTempDirectory();
        String zipFilePath = downloadAllFiles(referenceId, downloadToDir);
        ZipUtils.unzip(zipFilePath, downloadToDir);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error("Error occurred while downloading HEX Compare result files : " + exp.getMessage(),
            exp, Activator.PLUGIN_ID);
      }
      catch (IOException e) {
        CDMLogger.getInstance().error("Error occurred while unzipping HEX Compare result  : " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }).start();
  }


  /**
   * @param referenceId reference id from the comp hex service
   * @param downloadToDir Destination directory
   * @return zip path
   * @throws ApicWebServiceException any error in service call
   */
  public String downloadAllFiles(final String referenceId, final String downloadToDir) throws ApicWebServiceException {
    String zipFileName = referenceId + ".zip";

    downloadFile(
        getWsBase().path(WsCommonConstants.RWS_DOWNLOAD_ALL).queryParam(WsCommonConstants.RWS_QP_REF_ID, referenceId),
        zipFileName, downloadToDir);

    String absPath = downloadToDir +
        ((downloadToDir.endsWith("\\") || downloadToDir.endsWith("/")) ? "" : File.separator) + zipFileName;

    CDMLogger.getInstance().info("HEX Compare output files downloaded. Path : " + absPath, Activator.PLUGIN_ID);
    return absPath;
  }

  /**
   * Download comp hex result.
   *
   * @param referenceId the reference id
   * @param reportType the report type
   * @param outputFileName the output file name
   * @param outputFileDirectory the output file directory
   * @return the response
   * @throws ApicWebServiceException the apic web service exception
   */
  public String downloadCompHexReport(final String referenceId, final String reportType, final String outputFileName,
      final String outputFileDirectory)
      throws ApicWebServiceException {
    return downloadFile(
        getWsBase().path(WsCommonConstants.RWS_DOWNLOAD_REPORT).queryParam(WsCommonConstants.RWS_QP_REF_ID, referenceId)
            .queryParam(WsCommonConstants.RWS_QP_REPORT_TYPE, reportType),
        outputFileName, outputFileDirectory);
  }
}
