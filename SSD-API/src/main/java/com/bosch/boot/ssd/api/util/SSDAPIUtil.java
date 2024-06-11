/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * @author GDH9COB
 */
public class SSDAPIUtil {

  private SSDAPIUtil() {}

  /**
   * @param delimiter to join the string values
   * @param values    input sytring
   * @return
   */
  public static String joinString(String delimiter, String... values) {

    StringJoiner filePathJoiner = new StringJoiner(delimiter);
    for (String value : values) {
      filePathJoiner.add(value);
    }

    return filePathJoiner.toString();
  }

  /**
   * @param filePath to create folder structure
   * @return file
   */
  public static File createDirectory(final String filePath) {
    File file = new File(filePath);

    try {
      file.getParentFile().mkdirs();
      return file;
    }
    catch (SecurityException exception) {
      throw new IOError(exception);
    }
  }

  /**
   * This method is used to check if the file is available in the provided file path and return it.
   * 
   * @param filePath to download the file
   * @param logger 
   * @return the dowload file
   * @throws IOException
   */
  public static Resource loadFileAsResource(Path filePath, Logger logger) throws IOException {
    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        logger.info("resouse URL is" + resource.getURL());
        logger.error("resouse URL is" + resource.getURL());
        return resource;
      }
      throw new IOException("File doe not exist " + filePath.getFileName());
    }
    catch (IOException ex) {
      throw new IOException("Invalid file path " + filePath.getFileName(), ex);
    }
  }

  /**
   * @param value
   * @return
   */
  public static boolean isValid(String value) {
    return Objects.nonNull(value) && !(value.isEmpty());
  }

  /**
   * @return local date in required format
   */
  public static String getCurrentDate() {
    LocalDate localDate = LocalDate.now();
    return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
  }
  
  /**
   * @return folder path
   */
  public static String getAppDataFolderPath() {
    String appDataPath = System.getenv("AppData");
    String applicationFolder = appDataPath + "\\" + "SSDAPI"+ "\\";
    File dir = new File(applicationFolder);
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdirs();
    }
    return applicationFolder;

  }
  
  /**
   * @param text 
   * @param fileLocation
   * @throws IOException
   */
  public static void writeToFile(final String text, final String fileLocation) throws IOException {

    File file = SSDAPIUtil.createDirectory(fileLocation);

    try(FileOutputStream outputStream = new FileOutputStream(file)){
    byte[] strToBytes = text.getBytes();
    outputStream.write(strToBytes);

    }
  }
}
