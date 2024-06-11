/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.service.client;

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
  private final String targetUrl = "https://si-cdm01.de.bosch.com:8743/ssdapiservice/fileservice";

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


  /**
   * @param toolName name of tool
   * @param fileCategory doc type
   * @param fileNameWithExtn doc name with extension
   * @param localFilePath file path
   * @return boolean
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

    WebTarget target = client.target(this.targetUrl).path("downloadFile");

    Response response = target.queryParam(STR_TOOL_NAME, toolName).queryParam(STR_FILE_CATEGORY, fileCategory)
        .queryParam(STR_FILE_NAME_WITH_EXTN, fileNameWithExtn).queryParam("fileCheckSum", fileCheckSum).request().get();

    if ((response != null) && (response.getStatus() == 200)) {

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

    client.getFileIfDifferent("ssd", "oss", "OSS_Latest.pdf",
        "C:\\Users\\tab1ja\\AppData\\Roaming\\SSD\\OSS_Latest.pdf"/* , client.targetUrl */);
    Program.launch("C:\\Users\\tab1ja\\AppData\\Roaming\\SSD\\OSS_Latest.pdf");
  }
}
