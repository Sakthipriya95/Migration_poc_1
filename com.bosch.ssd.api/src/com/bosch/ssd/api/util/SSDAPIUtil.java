/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.bosch.ssd.api.logger.SSDApiLogger;

/**
 * @author VAU3COB
 */
public class SSDAPIUtil {

  /**
   * To define the location of the properties file in the development environment
   */
  private static final String STR_PROPERTIES_LOC_WS =
      "C:\\Vikram\\Work_related\\ALM_Orion_11\\SSD\\com.bosch.ssd.api\\ssdapi_config\\ssdapi.properties";
  /**
   * To define the location of the properties file in the server where the webservice is hosted.
   */
  private static final String STR_PROPERTIES_PRO_SERVER = "D:/CalTools_Webservice/properties/ssdapi.properties";
  /**
   * To define the location of the properties file in the server where the webservice is hosted.
   */
  private static final String STR_PROPERTIES_TEST_SERVER = "D:/CalTools_Webservice/properties/ssdapi_test.properties";
  /**
   *
   */
  private static SSDAPIUtil ssdapiUtil;
  /**
   *
   */
  private Properties configProperties;

  /**
   *
   */
  private SSDAPIUtil() {}

  /**
   * @return
   */
  public static SSDAPIUtil getInstance() {
    if (ssdapiUtil == null) {
      ssdapiUtil = new SSDAPIUtil();
    }
    return ssdapiUtil;
  }

  /**
   * @return the configProperties
   */
  public Properties getConfigProperties() {
    return this.configProperties;
  }

  /**
   * @param configProperties the configProperties to set
   */
  public void setConfigProperties(final Properties configProperties) {
    this.configProperties = configProperties;
  }

  /**
   *
   */
  private void loadConfigProperties() {
    try {
      this.configProperties = new Properties();
      // String propertiesLoction = System.getProperty("WebServiceSSDConfPath");
      System.out.println(STR_PROPERTIES_TEST_SERVER);
      File file = new File(STR_PROPERTIES_TEST_SERVER);
      this.configProperties.load(new FileInputStream(file.getAbsolutePath()));
    }
    catch (FileNotFoundException fnfe) {
      // To check the properties from the workspace if in development mode
      File file = new File(STR_PROPERTIES_LOC_WS);
      try {
        this.configProperties.load(new FileInputStream(file.getAbsolutePath()));
      }
      catch (IOException e1) {
        SSDApiLogger.getLoggerInstance().error(e1.getLocalizedMessage());
      }
    }
    catch (IOException e) {
      SSDApiLogger.getLoggerInstance().error(e.getLocalizedMessage());
    }
  }

  /**
   * @return
   */
  public String getFileLocation() {
    if (this.configProperties == null) {
      loadConfigProperties();
    }
    return this.configProperties.getProperty("FILE_LOC", System.getenv("TEMP"));
  }

  /**
   * @param propertyName
   * @return
   */
  public String getProperty(final String propertyName) {
    if (this.configProperties == null) {
      loadConfigProperties();
    }
    return this.configProperties.getProperty(propertyName, "D:/CalTools_Webservice/");
  }
}