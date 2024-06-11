/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.bosch.caltool.ssd.service.client.SsdReleaseInfoServiceClient;

/**
 *
 */
public class SsdReleasePluginProperties {
  
//  private static final Logger LOGGER = LogManager.getLogger(SsdReleasePluginProperties.class);
  
  
  /**
  *
  */
  private static String targetUrl;

  private SsdReleasePluginProperties() {}

  static {
    setProperties();
  }

  /**
  *
  */
  public static void setProperties() {
    Properties properties = new Properties();
    ClassLoader classLoader = SsdReleaseInfoServiceClient.class.getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("ssdreleaseplugin.properties")) {
      properties.load(inputStream);
      setTargetUrl(properties.getProperty("targeturl"));
    }
    catch (IOException e1) {
//      LOGGER.error("IOException in ApplicationProperties::setProperties, full stack trace follows: ", e1);
    }
  }

  /**
   * @return the targetUrl
   */
  public static String getTargetUrl() {
    return targetUrl;
  }

  /**
   * @param targetUrl the targetUrl to set
   */
  public static void setTargetUrl(final String targetUrl) {
    SsdReleasePluginProperties.targetUrl = targetUrl;
  }

}
