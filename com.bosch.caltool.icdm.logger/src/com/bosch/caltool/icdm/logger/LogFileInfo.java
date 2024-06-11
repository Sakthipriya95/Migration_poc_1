/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import java.io.File;


/**
 * This Singleton class provides log file utilities.
 */
public enum LogFileInfo {
                         /**
                          * Enum constant for singleton class implimentation.
                          */
                         INSTANCE;


  /**
   * String instance for logFileName, initialize default to iCDM.log.
   */
  private String logFileName = ICDMLoggerConstants.DEFAULT_LOG_FILE_NAME;

  /**
   * iCDM logfile path, initialized to default path.
   */
  private String logFilePath =
      getICDMLogDirectoryDefaultPath() + File.separator + ICDMLoggerConstants.DEFAULT_LOG_FILE_NAME;

  /**
   * This method returns LogFileInfo instance.
   *
   * @return LogFileInfo
   */
  public static LogFileInfo getInstance() {
    return INSTANCE;
  }

  /**
   * Gets the log file path.
   *
   * @return String
   */
  public String getLogFilePath() {
    return this.logFilePath;
  }

  /**
   * Sets the log file path.
   *
   * @param path the log file path
   */
  void setLogFilePath(final String path) {
    this.logFilePath = path;
  }

  /**
   * Gets the log file name.
   *
   * @return String
   */
  public String getLogFileName() {
    return this.logFileName;
  }

  /**
   * Sets the log file name.
   *
   * @param fileName the log file name
   */
  void setLogFileName(final String fileName) {
    this.logFileName = fileName;
  }

  /**
   * Method to get the log directory path.
   *
   * @return the path
   */
  private String getICDMLogDirectoryDefaultPath() {
    return System.getProperty("java.io.tmpdir") + File.separator + ICDMLoggerConstants.DEFAULT_LOG_FILE_SUBDIR;
  }

}
