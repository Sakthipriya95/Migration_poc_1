/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.fileservice;

import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;

import com.bosch.ssd.api.logger.SSDApiLogger;
import com.bosch.ssd.api.util.SSDAPIUtil;

/**
 * @author VAU3COB
 */
@Path("/fileservice")
public class FileService {


  /**
   *
   */
  private static final String STR_FILE_CHECK_SUM = "fileCheckSum";

  /**
   *
   */
  private static final String STR_FILE_IDENTICAL_MSG = "File is identical. Same file can be used";

  /**
   *
   */
  private static final String STR_CONTENT_DISPOSITION = "Content-Disposition";

  /**
   *
   */
  private static final String STR_ATTACHMENT_FILENAME = "attachment; filename=";

  /**
  *
  */
  private static final String STR_FILE_NAME_WITH_EXTN = "fileNameWithExtn";

  /**
   * Str File Category
   */
  private static final String STR_FILE_CATEGORY = "fileCategory";

  /**
   * Str Toolname
   */
  private static final String STR_TOOL_NAME = "toolName";

  /**
   * sample file path
   */
  private static final String FILE_PATH = "C:/Vikram/Temp/SSD_API_War/01/ssdapi.war";

  /**
   * Default Master File Directory, the property doesnt exists
   */
  private static final String DEFAULT_MASTER_FILE_DIRECTORY = "c:/Vikram/Temp/WebserviceTest/";

  /**
   * Master File Directory Property
   */
  private static final String STR_MASTER_FILE_DIR = "MASTER_FILE_DIR";

  /**
   * @param keyName
   * @param fileName
   * @return
   */
  @GET
  @Path("/downloadFile")
  @Produces(MediaType.MULTIPART_FORM_DATA)
  public Response downloadFile(@QueryParam("keyName") final String keyName,
      @QueryParam(STR_FILE_NAME_WITH_EXTN) final String fileName) {

    File file = new File(FILE_PATH);

    ResponseBuilder response = generateResponse(fileName, file);

    return response.build();

  }

  /**
   * @param toolName
   * @param fileCategory
   * @param fileNameWithExtn
   * @return
   */
  @GET
  @Path("/getFile")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@QueryParam(STR_TOOL_NAME) final String toolName,
      @QueryParam(STR_FILE_CATEGORY) final String fileCategory,
      @QueryParam(STR_FILE_NAME_WITH_EXTN) final String fileNameWithExtn) {

    String filePath = generateFileName(toolName, fileCategory, fileNameWithExtn);
    SSDApiLogger.getLoggerInstance().info(filePath);
    File file = new File(filePath);

    // ResponseBuilder response = generateResponse(fileNameWithExtn, file);

    ResponseBuilder response = Response.ok(file);
    SSDApiLogger.getLoggerInstance().info("Response Built");
    SSDApiLogger.getLoggerInstance().info(fileNameWithExtn);
    response.header(STR_CONTENT_DISPOSITION, STR_ATTACHMENT_FILENAME + fileNameWithExtn);
    return response.build();

  }

  /**
   * @param toolName
   * @param fileCategory
   * @param fileNameWithExtn
   * @param fileCheckSum
   * @return
   * @throws IOException
   */
  @GET
  @Path("/getFileIfDifferent")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFileIfDifferent(@QueryParam(STR_TOOL_NAME) final String toolName,
      @QueryParam(STR_FILE_CATEGORY) final String fileCategory,
      @QueryParam(STR_FILE_NAME_WITH_EXTN) final String fileNameWithExtn,
      @QueryParam(STR_FILE_CHECK_SUM) final long fileCheckSum)
      throws IOException {

    String filePath = generateFileName(toolName, fileCategory, fileNameWithExtn);

    File file = new File(filePath);

    ResponseBuilder response = null;

    Checksum checkSum = null;

    long originalFileCheckSum = 0;

    try {

      checkSum = FileUtils.checksum(file, new CRC32());

      originalFileCheckSum = checkSum.getValue();

    }

    catch (IOException e2) {

      System.out.println(e2.getLocalizedMessage());

    }

    if (originalFileCheckSum == fileCheckSum) {

      System.out.println("File is identical. Please use the same file");
      response = Response.serverError().entity(STR_FILE_IDENTICAL_MSG);

    }

    else {

      response = generateResponse(fileNameWithExtn, file);

    }

    return response.build();

  }

  /**
   * @param fileName
   * @param file
   * @return
   */
  private ResponseBuilder generateResponse(final String fileName, final File file) {

    ResponseBuilder response = Response.ok(file);

    response.header(STR_CONTENT_DISPOSITION, STR_ATTACHMENT_FILENAME + fileName);

    return response;
  }

  /**
   * @param toolName
   * @param fileCategory
   * @param fileNameWithExtn
   * @return
   */
  private String generateFileName(final String toolName, final String fileCategory, final String fileNameWithExtn) {
    String masterFileDirectory = SSDAPIUtil.getInstance().getProperty(STR_MASTER_FILE_DIR);

    if ((masterFileDirectory == null) || masterFileDirectory.isEmpty()) {
      masterFileDirectory = DEFAULT_MASTER_FILE_DIRECTORY;
    }
    SSDApiLogger.getLoggerInstance().info("Master File Directory: " + masterFileDirectory);
    return masterFileDirectory + File.separator + toolName + File.separator + fileCategory + File.separator +
        fileNameWithExtn;
  }

}
