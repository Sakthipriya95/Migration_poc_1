/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.serviceloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.icdm.bo.IcdmBo;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.rest.serviceloader.common.ServiceLoaderException;

/**
 * @author bne4cob
 */
public class MainClass {

  private static final String CONF_PATH_KEY = "WebServiceConfPath";

  private static final String LOG4J_PROPERTIES_PATH = "config/log4j.properties";

  private static final String REGEX_URL = "\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

  private static final String REGEX_FILE_PATH = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";
  /**
   * Name of the temporary local WADL file if input WADL is URL.
   */
  private static final String LOCAL_TEMP_WADL_PATH = System.getProperty("java.io.tmpdir") + "LocalWadl.xml";

  private static ILoggerAdapter logger;

  /**
   * Main method
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) {

    long startTime = System.currentTimeMillis();

    initializeLogger();

    try {
      initializeConfig();

      String wadlFile = readInput(args);

      // Establish database connection.
      new IcdmBo().initialize();

      new ServiceLoaderMaster(logger, wadlFile).run();

      long endTime = System.currentTimeMillis();

      logger.info("WADL Parsing, uploading to database and report generation completed. Total time taken = {}",
          (endTime - startTime));
    }
    catch (ServiceLoaderException | IcdmException e) {
      logger.error(e.getMessage(), e);
    }
    finally {
      // Close the database connection.
      new IcdmBo().dispose();
    }
  }

  /**
   * Initialize Logger Object
   *
   * @throws ServiceLoaderException
   */
  private static void initializeLogger() {
    // PropertyConfigurator.configure(LOG4J_PROPERTIES_PATH);
    logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("RSLDR"));
  }

  /**
   *
   */
  private static void initializeConfig() throws ServiceLoaderException {
    // Init the right message.properties file
    String confFilePath = System.getProperty(CONF_PATH_KEY);
    if (CommonUtils.isEmptyString(confFilePath)) {
      throw new ServiceLoaderException("System property '" + CONF_PATH_KEY + "' not set");
    }
    Messages.setResourceBundleFile(confFilePath);
  }


  /**
   * Read the input from the command line arguments.
   *
   * @param cmdLine
   * @return String File path
   * @throws ServiceLoaderException
   */
  private static String readInput(final String[] cmdLine) throws ServiceLoaderException {
    if (cmdLine.length != 1) {
      throw new ServiceLoaderException("Invalid number of inputs");
    }
    else if (cmdLine[0].matches(REGEX_URL)) {
      return readFileFromURL(cmdLine[0]);
    }
    else if (cmdLine[0].matches(REGEX_FILE_PATH)) {
      return cmdLine[0];
    }
    throw new ServiceLoaderException("Invalid file path or url path.");
  }

  /**
   * Returns the file path after copying the contents from the url.
   *
   * @param urlPath String
   * @return String WADL file path
   * @throws ServiceLoaderException
   */
  private static String readFileFromURL(final String urlPath) throws ServiceLoaderException {
    try {
      URL url = new URL(urlPath);
      URLConnection connection = url.openConnection();

      File file = new File(LOCAL_TEMP_WADL_PATH);
      if (file.exists()) {
        Files.delete(file.toPath());
        logger.debug("Temporary local WADL file exists. Deleted successfully. Path : {} ", LOCAL_TEMP_WADL_PATH);
      }

      if (file.createNewFile()) {
        logger.debug("New temporary local WADL file created successfully. Path : {} ", LOCAL_TEMP_WADL_PATH);
      }
      String readLine;
      try (BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
          FileWriter fWriter = new FileWriter(file.getAbsolutePath());
          BufferedWriter bWriter = new BufferedWriter(fWriter);) {

        while ((readLine = bReader.readLine()) != null) {
          bWriter.write(readLine + "\n");
        }
        logger.debug("Contents read from the URL and copied successfully");
      }

    }
    catch (IOException e) {
      throw new ServiceLoaderException(e.getMessage(), e);
    }

    return LOCAL_TEMP_WADL_PATH;
  }

}
