/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * This class is used to perform IO related operations
 *
 * @author jvi6cob
 */
public final class FileIOUtil {

  /**
   * Private constructor for utiltity class
   */
  private FileIOUtil() {
    // Private constructor for utiltity class
  }

  /**
   * Converts the inputStream to String
   *
   * @param inputStream InputStream
   * @return String
   */
  public static String convertHtmlToString(final InputStream inputStream) {
    String result;
    try {
      result = IOUtils.toString(inputStream);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().warn("IO error converting html to bytes  ", exp, Activator.PLUGIN_ID);
      result = "";
    }
    return result;
  }

  /**
   * Converts the byte array to String
   *
   * @param bytes byte[]
   * @return String
   */
  public static String convertHtmlByteToString(final byte[] bytes) {
    String result;
    try {
      result = IOUtils.toString(bytes);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().warn("IO error converting html to bytes  ", exp, Activator.PLUGIN_ID);
      result = "";
    }
    return result;
  }


  /**
   * iCDM-1455 <br>
   * Method to Check if a file is already locked (already used/opened by other processess) <br>
   * Return TRUE if locked/used by other process <br>
   *
   * @param filePath filePath
   * @return true if file is locked
   */
  public static boolean checkIfFileIsLocked(final String filePath) {
    FileOutputStream fos = null;
    // by default set it to false
    boolean isFileLocked = false;
    File file = null;
    // check if outputstream can be used on the given file
    try {
      file = new File(filePath);
      fos = new FileOutputStream(file, true);
    }
    catch (IOException e) {
      // any exception would mean the file is used by another process
      CDMLogger.getInstance().error("File is used by another process " + filePath, e, Activator.PLUGIN_ID);
      isFileLocked = true;
    }
    finally {
      if (fos != null) {
        try {
          fos.close();
        }
        catch (IOException e) {
          // any exception would mean the file cannot be closed and is locked
          CDMLogger.getInstance().error("File cannot be closed " + filePath, e, Activator.PLUGIN_ID);
          isFileLocked = true;
        }
      }
    }
    return isFileLocked;
  }

  /**
   * @param dirPath path of the directory to be created
   */
  public static void createDir(final String dirPath) {
    if (new File(dirPath).exists()) {
      CDMLogger.getInstance().debug("Directory already exists : {}", dirPath);
    }
    else {
      new File(dirPath).mkdir();
      CDMLogger.getInstance().info("Directory created " + dirPath, Activator.PLUGIN_ID);
    }
  }

  /**
   * Convert the input file to a byte array
   *
   * @param file input
   * @return byte array
   * @throws IOException error while reading the file and converting to byte array
   */
  public static byte[] toByteArray(final File file) throws IOException {
    try (InputStream fin = new FileInputStream(file)) {
      return IOUtils.toByteArray(fin);
    }
  }

  /**
   * Convert the input stream to a byte array
   *
   * @param iStream Input Stream
   * @return byte array
   * @throws IOException error while reading the stream
   */
  public static byte[] toByteArray(final InputStream iStream) throws IOException {
    return IOUtils.toByteArray(iStream);

  }

  /**
   * Method to convert file to byte array with filepath as input
   *
   * @param filePath input file path
   * @return byte array of the file
   * @throws IcdmException exception in converting file to byte array
   */
  public static byte[] toByteArray(final String filePath) throws IcdmException {
    try {
      return FileUtils.readFileToByteArray(new File(filePath));
    }
    catch (IOException e) {
      throw new IcdmException("Exception in converting file to byte array. File path : {}" + e.getMessage(), filePath,
          e);
    }
  }

  /**
   * Update the timestamp to current time (simulates 'touch' command in unix)
   *
   * @param filePath file path
   * @throws IcdmException file touching error
   */
  public static void touch(final String filePath) throws IcdmException {
    try {
      FileUtils.touch(new File(filePath));
    }
    catch (IOException e) {
      throw new IcdmException("Exception while updating file timestamp : {}" + e.getMessage(), filePath, e);
    }
  }

  /**
   * Find the extension of the input file
   *
   * @param fileName file name with or without path
   * @return file extension, or empty string if input is null or does not have extension
   */
  public static String getFileExtension(final String fileName) {
    if ((fileName == null)) {
      return "";
    }

    String name = fileName.trim();
    if ((name.length() <= 1) || (name.indexOf('.') < 0)) {
      return "";
    }

    return fileName.substring(fileName.lastIndexOf('.') + 1);
  }


  /**
   * Copy the source file to the destination
   *
   * @param sourcePath source file
   * @param destPath destination file
   * @throws IOException copy error
   */
  public static void copy(final String sourcePath, final String destPath) throws IOException {
    Files.copy(Paths.get(sourcePath), Paths.get(destPath));
  }
}
