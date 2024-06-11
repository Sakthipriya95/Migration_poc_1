/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.fileservice.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.program.Program;

/**
 * @author VAU3COB
 */
public class FileServiceClient {

  // Server
  private final String targetUrl = "http://si-cdm01.de.bosch.com:8680/ssdapi/rest/fileservice";

  /**
   *
   */
  private static final String STR_FILE_CATEGORY = "fileCategory";
  /**
   *
   */
  private static final String STR_FILE_NAME_WITH_EXTN = "fileNameWithExtn";
  /**
   *
   */
  private static final String STR_TOOL_NAME = "toolName";

  // LocalHost
  // private final String targetUrl = "http://localhost:8080/com.bosch.ssd.api/rest/fileservice";

  /**
   * @param toolName
   * @param fileCategory
   * @param fileNameWithExtn
   * @param localFilePath
   * @return
   */
  public boolean getFile(final String toolName, final String fileCategory, final String fileNameWithExtn,
      final String localFilePath) {


    Client client = ClientBuilder.newClient();

    WebTarget target = client.target(this.targetUrl).path("getFile");

    Response response = target.queryParam(STR_TOOL_NAME, toolName).queryParam(STR_FILE_CATEGORY, fileCategory)
        .queryParam(STR_FILE_NAME_WITH_EXTN, fileNameWithExtn).request().get();

    writeToFile(localFilePath, response);


    return true;

  }

  /**
   * @param toolName
   * @param fileCategory
   * @param fileNameWithExtn
   * @param localFilePath
   * @param fileCheckSum
   * @return
   */

  public boolean getFileIfDifferent(final String toolName, final String fileCategory, final String fileNameWithExtn,
      final String localFilePath) {

    boolean result = false;

    long fileCheckSum = 0;

    File file = new File(localFilePath);

    Checksum checkSum = null;

    try {

      checkSum = FileUtils.checksum(file, new CRC32());

      fileCheckSum = checkSum.getValue();

    }

    catch (IOException e) {

      System.out.println(e.getLocalizedMessage());

    }


    Client client = ClientBuilder.newClient();

    WebTarget target = client.target(this.targetUrl).path("getFileIfDifferent");

    Response response = target.queryParam(STR_TOOL_NAME, toolName).queryParam(STR_FILE_CATEGORY, fileCategory)
        .queryParam(STR_FILE_NAME_WITH_EXTN, fileNameWithExtn).queryParam("fileCheckSum", fileCheckSum).request().get();

    if ((response != null) && (response.getStatus() != Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())) {

      writeToFile(localFilePath, response);

      result = true;
    }

    return result;

  }

  /**
   * Method to write the Multipart response to the local file
   *
   * @param localFilePath
   * @param response
   */
  private void writeToFile(final String localFilePath, final Response response) {

    try (FileOutputStream out = new FileOutputStream(localFilePath);

        InputStream is = (InputStream) response.getEntity()) {

      int len = 0;

      byte[] buffer = new byte[is.available()];

      while ((len = is.read(buffer)) != -1) {

        out.write(buffer, 0, len);

      }

      out.flush();

    }

    catch (IOException e) {
      System.out.println(e.getLocalizedMessage());
    }
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {

    FileServiceClient client = new FileServiceClient();

    client.getFile("ssd", "user_guide", "SSD_Application_UserGuide.pdf",
        "C:\\Vikram\\Temp\\WebserviceTest\\downloaded\\ssd_oss.pdf"/* , client.targetUrl */);

    client.getFileIfDifferent("ssd", "user_guide", "SSD_Application_UserGuide.pdf",
        "C:\\Vikram\\Temp\\WebserviceTest\\downloaded\\ssd_oss.pdf"/* , client.targetUrl */);
    Program.launch("C:\\Vikram\\Temp\\WebserviceTest\\downloaded\\ssd_oss.pdf");
  }
}
