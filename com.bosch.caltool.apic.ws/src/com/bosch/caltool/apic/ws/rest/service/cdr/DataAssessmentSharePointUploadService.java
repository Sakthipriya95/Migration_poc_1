/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

import com.bosch.calcomp.commonutil.CommonUtils;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.DaDataAssessmentLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.database.entity.cdr.TDaFile;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.security.Decryptor;

/**
 * @author msp5cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_DATA_ASSESSMENT_SHARE_POINT_UPLOAD))
public class DataAssessmentSharePointUploadService extends AbstractRestService {

  private static final String SLASH = "/";
  private static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//SHARE_POINT_UPLOAD//";
  private static final String CMD_SEPARATOR = " ";
  private static final String OK_MSG = "200 OK";
  private static final String CREATED_MSG = "201 CREATED";

  private static final String SUCCESS_MSG = "Data Assessment file successfully uploaded to the SharePoint URL : ";
  private static final String FAILURE_MSG =
      "Data Assessment file upload to the SharePoint Failed, check the SharePoint URL configured in PIDC Attribute : ";


  /**
   * @param input DataAssessSharePointUploadInputModel
   * @return Status
   * @throws IcdmException Service Exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public String uploadFileToSharePoint(final DataAssessSharePointUploadInputModel input) throws IcdmException {
    String filePath = downloadFile(input.getBaselineId());
    return executeCurlCopyCmd(getCommandString(input, filePath), filePath, input);
  }

  private String executeCurlCopyCmd(final String command, final String filePath,
      final DataAssessSharePointUploadInputModel input)
      throws IcdmException {
    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
    builder.redirectErrorStream(true);
    try {
      Process process = builder.start();
      getLogger().debug("Copy of Data Assessment files to SharePoint started, printing message from console....");

      if (isFileUploaded(process)) {
        getLogger().debug("Copy of Data Assessment files to SharePoint completed.");
        return SUCCESS_MSG + input.getSharePointUrl();
      }

      getLogger().debug("Copy of Data Assessment files to SharePoint failed.");
      return FAILURE_MSG + input.getSharePointAttrName();
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage());
    }
    finally {
      deleteFile(filePath);
    }
  }

  /**
   * @param process
   * @return
   * @throws IcdmException
   */
  private boolean isFileUploaded(final Process process) throws IcdmException {
    boolean fileUploaded = false;
    try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while (true) {
        line = r.readLine();
        if (line == null) {
          break;
        }

        if (!fileUploaded) {
          fileUploaded = (line.contains(OK_MSG) || line.contains(CREATED_MSG));
        }

        getLogger().debug(line);
      }
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage());
    }
    return fileUploaded;
  }

  private String getCommandString(final DataAssessSharePointUploadInputModel input, final String filePath) {
    StringBuilder commandSb = new StringBuilder();
    String curlCmdExe = Messages.getString("ICDM.CURL_CMD_LOCATION");
    String boschCertLocation = Messages.getString("ICDM.BOSCH_CERT_LOCATION");

    commandSb.append(curlCmdExe);
    appendCmdSeparator(commandSb);

    commandSb.append("--ntlm --user");
    appendCmdSeparator(commandSb);

    commandSb.append(input.getUserName());
    commandSb.append(":");
    commandSb.append(Decryptor.getInstance().decrypt(input.getEncryptedPassword(), getLogger()));
    appendCmdSeparator(commandSb);

    commandSb.append("--upload-file");
    appendCmdSeparator(commandSb);

    commandSb.append(filePath);
    appendCmdSeparator(commandSb);

    commandSb.append(getFormattedSharePointUrl(input.getSharePointUrl()));
    appendCmdSeparator(commandSb);

    commandSb.append("--cacert");
    appendCmdSeparator(commandSb);

    commandSb.append(boschCertLocation);
    appendCmdSeparator(commandSb);

    commandSb.append("-verbose");

    return commandSb.toString();
  }

  private void appendCmdSeparator(final StringBuilder commandSb) {
    commandSb.append(CMD_SEPARATOR);
  }

  private String getFormattedSharePointUrl(final String sharePointUrl) {
    if (sharePointUrl.endsWith(SLASH)) {
      return sharePointUrl;
    }
    return sharePointUrl + SLASH;
  }

  private String downloadFile(final Long dataAssessmentId) throws IcdmException {
    List<TDaFile> downloadFileList =
        new DaDataAssessmentLoader(getServiceData()).getEntityObject(dataAssessmentId).getTDaFiles();
    if (CommonUtils.isNullOrEmpty(downloadFileList)) {
      throw new IcdmException("No Data Assessment files found for Data Assessment ID : " + dataAssessmentId);
    }
    byte[] ret = downloadFileList.get(0).getFileData();
    String outputZipfileName = "DataAssessment_" + dataAssessmentId + ".zip";
    String outputZipFilePath = SERVER_PATH + outputZipfileName;
    File file = new File(outputZipFilePath);
    file.getPath();
    try {
      FileUtils.writeByteArrayToFile(file, ret);
      getLogger().debug("Data Assessment Files downloaded to server work directory : {}", file.getPath());
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage());
    }
    return file.getPath();
  }

  private void deleteFile(final String filePath) throws IcdmException {
    java.nio.file.Path path = Paths.get(filePath);
    try {
      long size = Files.size(path);
      if (size > 0) {
        Files.delete(path);
        getLogger().debug("Data Assessment Files deleted from server work directory : {}", filePath);
      }
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage());
    }
  }


}
