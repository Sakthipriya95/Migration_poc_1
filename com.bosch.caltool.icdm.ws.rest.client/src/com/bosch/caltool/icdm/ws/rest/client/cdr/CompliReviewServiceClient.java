/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CompliDstInput;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.util.ZipUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client class to fetch compliance parameter in an a2l file
 *
 * @author svj7cob
 */
// Task 263282
public class CompliReviewServiceClient extends AbstractRestServiceClient {

  /**
   *
   */
  private static final String ERROR_WHILE_RETRIEVING_OUTPUT_FILE = "Error while retrieving output file - ";
  /**
   * Date Format : yyyy_MM_dd_HH_mm_ss for naming the pdf report
   */
  public static final String DATE_FORMAT_14 = "yyyy_MM_dd___HH_mm_ss";

  /**
   * initialize client constructor with the compli-param url
   */
  public CompliReviewServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_COMPLI_REVIEW);
  }

  /**
   * @param a2lfilePath A2L file path
   * @param metaData Review metadata
   * @param hexFilePathSet Set of HEX files
   * @param outputPath output file path
   * @throws ApicWebServiceException any error while reading file
   */
  public void executeCompliReview(final String a2lfilePath, final CompliReviewInputMetaData metaData,
      final Set<String> hexFilePathSet, final String outputPath)
      throws ApicWebServiceException {

    String zippedA2lFilePath = compressFile(a2lfilePath);

    String jsonFilePath = createJsonFile(a2lfilePath, metaData);

    Set<String> zippedFilePathSet = new TreeSet<>();
    for (String hexFilePath : hexFilePathSet) {
      String zippedHexFilePath = compressFile(hexFilePath);
      zippedFilePathSet.add(zippedHexFilePath);
    }

    try {
      executeCompliReview(zippedA2lFilePath, jsonFilePath, zippedFilePathSet, outputPath);
    }
    finally {
      // added in set to delete the files
      zippedFilePathSet.add(jsonFilePath);
      zippedFilePathSet.add(zippedA2lFilePath);

      deleteFiles(zippedFilePathSet);
    }

  }

  /**
   * @param pidcA2lId A2L file id
   * @param a2lFileName a2l file name
   * @param metaData Review metadata
   * @param hexFilePathSet Set of HEX files
   * @param outputPath output file path
   * @throws ApicWebServiceException any error while reading file
   */
  public void executeCompliReviewUsingPidcA2lId(final String pidcA2lId, final String a2lFileName,
      final CompliReviewInputMetaData metaData, final Set<String> hexFilePathSet, final String outputPath)
      throws ApicWebServiceException {


    String jsonFilePath = createJsonFile(a2lFileName, metaData);

    Set<String> zippedFilePathSet = new TreeSet<>();
    for (String hexFilePath : hexFilePathSet) {
      String zippedHexFilePath = compressFile(hexFilePath);
      zippedFilePathSet.add(zippedHexFilePath);
    }

    try {
      performCompliReview(pidcA2lId, jsonFilePath, zippedFilePathSet, outputPath);
    }
    finally {
      // added in set to delete the files
      zippedFilePathSet.add(jsonFilePath);

      deleteFiles(zippedFilePathSet);
    }

  }

  /**
   * @param pidcA2lId A2L file id
   * @param jsonFilePath JSON file path
   * @param hexFilePathSet Set of HEX files
   * @param outputPath output file path
   * @throws ApicWebServiceException any error while reading file
   */
  public void performCompliReview(final String pidcA2lId, final String jsonFilePath, final Set<String> hexFilePathSet,
      final String outputPath)
      throws ApicWebServiceException {

    // Json file body part
    final FileDataBodyPart jsonFile = new FileDataBodyPart(WsCommonConstants.META_DATA, new File(jsonFilePath));

    FormDataMultiPart multipart = new FormDataMultiPart();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    multiPartList.add(multipart);

    try {

      // add json files
      multipart = (FormDataMultiPart) multipart.bodyPart(jsonFile);
      multiPartList.add(multipart);
      multipart = multipart.field(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
      multiPartList.add(multipart);
      // Add Hex files.
      FileDataBodyPart hexBodyPart;
      for (String hexFilePath : hexFilePathSet) {
        // Hex file body part
        hexBodyPart = new FileDataBodyPart(WsCommonConstants.HEX_FILE_MUTIPART, new File(hexFilePath));
        multipart = (FormDataMultiPart) multipart.bodyPart(hexBodyPart);
        multiPartList.add(multipart);
      }

      try (InputStream responseStream = post(getWsBase().path(WsCommonConstants.RWS_COMPLI_REVIEW_PIDC_A2LID),
          multipart, InputStream.class, MediaType.APPLICATION_OCTET_STREAM)) {
        // Copy the files to the new location
        Files.copy(responseStream, new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e) {
        throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR,
            ERROR_WHILE_RETRIEVING_OUTPUT_FILE + e.getMessage(), e);
      }

    }
    finally {
      closeResource(multiPartList);
    }
  }

  /**
   * @param a2lfilePath A2L file path
   * @param jsonFilePath JSON file path
   * @param hexFilePathSet Set of HEX files
   * @param outputPath output file path
   * @throws ApicWebServiceException any error while reading file
   */
  public void executeCompliReview(final String a2lfilePath, final String jsonFilePath, final Set<String> hexFilePathSet,
      final String outputPath)
      throws ApicWebServiceException {

    // A2l file body part
    final FileDataBodyPart a2lFile = new FileDataBodyPart(WsCommonConstants.A2L_FILE_MULTIPART, new File(a2lfilePath));
    // Json file body part
    final FileDataBodyPart jsonFile = new FileDataBodyPart(WsCommonConstants.META_DATA, new File(jsonFilePath));

    FormDataMultiPart multipart = new FormDataMultiPart();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    multiPartList.add(multipart);

    try {
      // add a2l file
      multipart = (FormDataMultiPart) multipart.bodyPart(a2lFile);
      multiPartList.add(multipart);
      // add json files
      multipart = (FormDataMultiPart) multipart.bodyPart(jsonFile);
      multiPartList.add(multipart);

      // Add Hex files.
      FileDataBodyPart hexBodyPart;
      for (String hexFilePath : hexFilePathSet) {
        // Hex file body part
        hexBodyPart = new FileDataBodyPart(WsCommonConstants.HEX_FILE_MUTIPART, new File(hexFilePath));
        multipart = (FormDataMultiPart) multipart.bodyPart(hexBodyPart);
        multiPartList.add(multipart);
      }

      try (InputStream responseStream =
          post(getWsBase(), multipart, InputStream.class, MediaType.APPLICATION_OCTET_STREAM)) {
        // Copy the files to the new location
        Files.copy(responseStream, new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e) {
        throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR,
            ERROR_WHILE_RETRIEVING_OUTPUT_FILE + e.getMessage(), e);
      }
    }
    finally {
      closeResource(multiPartList);
    }
  }

  /**
   * @param compliDstInp Input for Compliance review
   * @param outputPath output File path
   * @throws ApicWebServiceException Exception while performing compliance review
   */

  public void executeCompliReviewUsingDstId(final CompliDstInput compliDstInp, final String outputPath)
      throws ApicWebServiceException {

    try (InputStream responseStream = post(getWsBase().path(WsCommonConstants.RWS_VCDM_DST_ID), compliDstInp,
        InputStream.class, MediaType.APPLICATION_OCTET_STREAM)) {
      // Copy the files to the new location
      Files.copy(responseStream, new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR,
          ERROR_WHILE_RETRIEVING_OUTPUT_FILE + e.getMessage(), e);
    }
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  private String createJsonFile(final String a2lfileName, final CompliReviewInputMetaData webFlowCompliReviewInput)
      throws ApicWebServiceException {
    String jsonPathName = "JsonFileInput_" + FilenameUtils.getBaseName(a2lfileName) + "_" +
        new SimpleDateFormat(DATE_FORMAT_14).format(new Date()) + ".json";
    final String jsonPath = getClientConfiguration().getUserTempDirectory() + jsonPathName;

    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(jsonPath), webFlowCompliReviewInput);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    return jsonPath;
  }


  /**
   * @return response header value
   */
  public String getLastExecutionId() {
    return null == getResponseHeader(WsCommonConstants.RESP_HEADER_COMPLI_RVW_ID) ? null
        : (String) getResponseHeader(WsCommonConstants.RESP_HEADER_COMPLI_RVW_ID).get(0);
  }

  /**
   * @param executionId Execution id of compli data review
   * @return Imported file directory path
   * @throws ApicWebServiceException Exception while downloading file
   * @throws IOException Excption while unzipping the file
   */
  public String importCompliReviewInputData(final String executionId) throws ApicWebServiceException, IOException {
    String downloadToDir = null;
    String zipFilePath;
    downloadToDir = getClientConfiguration().getIcdmTempDirectory();
    zipFilePath = downloadAllFiles(executionId, executionId, downloadToDir);
    ZipUtils.unzip(zipFilePath, downloadToDir);
    return downloadToDir + File.separator + executionId;
  }

  /**
   * @param executionId reference id from the Compli data review
   * @param fileName Name of the file to be created
   * @param downloadToDir Destination directory
   * @return zip path
   * @throws ApicWebServiceException any error in service call
   */
  public String downloadAllFiles(final String executionId, final String fileName, final String downloadToDir)
      throws ApicWebServiceException {
    String zipFileName = fileName + ".zip";

    downloadFile(getWsBase().path(WsCommonConstants.RWS_DOWNLOAD_ALL).queryParam(WsCommonConstants.RWS_QP_EXECUTION_ID,
        executionId), zipFileName, downloadToDir);

    String absPath = downloadToDir +
        ((downloadToDir.endsWith("\\") || downloadToDir.endsWith("/")) ? "" : File.separator) + zipFileName;

    CDMLogger.getInstance().info(
        "Compliance Review Input/Output files downloaded for Execution ID : " + executionId + " at Path : " + absPath,
        Activator.PLUGIN_ID);
    return absPath;
  }

  /**
   * @param jsonPath Input Json Path
   * @return {@link CompliReviewInputMetaData}
   * @throws ApicWebServiceException Exception
   */
  public CompliReviewInputMetaData readJsonMetaData(final String jsonPath) throws ApicWebServiceException {
    CompliReviewInputMetaData compliRvwInputMetaData;
    ObjectMapper mapper = new ObjectMapper();
    try {
      compliRvwInputMetaData = mapper.readValue(new File(jsonPath), CompliReviewInputMetaData.class);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    return compliRvwInputMetaData;
  }

  /**
   * @param pverName pverName
   * @return isNONSDOM
   * @throws ApicWebServiceException Exception
   */
  public boolean checkIsNONSDOM(final String pverName) throws ApicWebServiceException {

    WebTarget target =
        getWsBase().path(WsCommonConstants.CHECK_IS_NON_SDOM).queryParam(WsCommonConstants.PVER_NAME, pverName);
    Response response = target.request(MediaType.TEXT_PLAIN).get();
    boolean isNonSDOM = Boolean.parseBoolean(response.readEntity(String.class));
    response.close();

    return isNonSDOM;

  }
}
