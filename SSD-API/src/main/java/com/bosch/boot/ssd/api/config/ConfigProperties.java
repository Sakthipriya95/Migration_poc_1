/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author GDH9COB
 *
 */
@Component
public class ConfigProperties {
  
  private String propertiesFileLocation;
  
  private Properties properties;
  
  private static final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);  
  /**
   * @param propertiesFileLocation
   */
  @Autowired
  public ConfigProperties(@Value("${ssdapi.properties.loc}") String propertiesFileLocation) {
    this.propertiesFileLocation = propertiesFileLocation;
  }

  /**
   * @param propertyName 
   * @return
   */
  public String getFileLocation(String propertyName) {
    if (this.properties == null || properties.isEmpty()) {
      loadConfigProperties();
    }
    return this.properties.getProperty(propertyName, System.getenv("TEMP"));
  }

  /**
  *
  */
  private void loadConfigProperties() {
    try {
      this.properties = new Properties();
      File file = new File(propertiesFileLocation);
      if(file.exists()) {
        logger.info("file is present in location" +file.getAbsolutePath());
        logger.error("file is present in location" +file.getAbsolutePath());
      }
      else {
        logger.info("file is not present in location" +file.getAbsolutePath());
        logger.error("file is not present in location" +file.getAbsolutePath());
      } 
      this.properties.load(new FileInputStream(file.getPath()));
    }
    catch (IOException exception) {
      logger.error("Error reading the properties file {}", propertiesFileLocation, exception);
    }
  }

}
