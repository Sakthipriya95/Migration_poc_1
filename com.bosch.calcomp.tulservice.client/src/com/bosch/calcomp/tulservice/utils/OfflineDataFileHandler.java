/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import com.bosch.calcomp.tulservice.exception.ToolUsageLoggingException;
import com.bosch.calcomp.tulservice.internal.model.ToolUsageStat;

/**
 * @author ICP1COB
 */
public class OfflineDataFileHandler {

  private static File offlineDataFile;

  private static final String TUL_DIRECTORY = "TUL_OFFLINE_DATA";

  private static final String FILE_EXTENSION = ".json";

  private static final String TEMP_DIRECTORY = "java.io.tmpdir";

  private OfflineDataFileHandler() {}

  /**
   * To append the offline data if the post call is not successful.
   *
   * @param json - serialized object
   * @param filePath - File location
   * @throws IOException - Exception
   */
  public static void appendOfflineData(final String json, final String filePath) throws IOException {
    try {
      String fileName = UUID.randomUUID().toString();
      File dir = new File(filePath + TUL_DIRECTORY);
      if (!dir.isDirectory()) {
        dir.mkdir();
      }

      offlineDataFile = new File(dir.getPath() + File.separator + fileName + FILE_EXTENSION);
      Files.write(offlineDataFile.toPath(), json.getBytes());
    }
    catch (Exception e) {
      throw new ToolUsageLoggingException("Failed to append the offline data." + e.getMessage());
    }
  }


  /**
   * To set the default file path, if the value is not provide dby the tool.
   *
   * @param toolUsageStat Object
   */
  public static void setFileLocation(final ToolUsageStat toolUsageStat) {

    String filePath = System.getProperty(MessageConstants.OFFLINE_DATA_FILE_PATH);

    if ((filePath != null) && !filePath.isEmpty()) {
      toolUsageStat.setFilePath(filePath);
    }
    else {
      toolUsageStat.setFilePath(System.getProperty(TEMP_DIRECTORY));
    }
  }

  /**
   * If the post call is successful, delete the offline files
   *
   * @throws IOException Exception
   */
  public static void deleteOfflineFiles() throws IOException {
    File parentDirectory = offlineDataFile.getParentFile();
    
    if(parentDirectory!=null)
    {
    File[] listOfFiles = OfflineDataFileHandler.getListOfFiles(parentDirectory.getParentFile().toString());
    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        Files.deleteIfExists(listOfFiles[i].toPath());
      }
    }
    }
  }

  /**
   * To get the list of files in offline path folder
   *
   * @param fileDirectory - parent directory
   * @return all the files in the directory
   */

  public static File[] getListOfFiles(final String fileDirectory) {


    File folder = new File(fileDirectory + File.separator + TUL_DIRECTORY);
    return folder.listFiles();

  }
}
