/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * ICDM Property file reader
 * 
 * @author adn1cob
 */
public final class PropFileReader {

  /**
   * Defines iCDM properties file path
   */
  final private String propFilePath;
  /**
   * PropFileReader instance
   */
  private static PropFileReader instance;

  /**
   * The Private constructor
   */
  private PropFileReader() {
    // Initialize iCDM.properties file path
    this.propFilePath = CommonUtils.getICDMPropertiesFilePath();
  }

  /**
   * @return PropFileReader
   */
  public static PropFileReader getInstance() {
    if (instance == null) {
      instance = new PropFileReader();
    }
    return instance;
  }

  /**
   * Loads the properties file iCDM.Properties
   * 
   * @return properties
   */
  public Properties getiCDMProperties() {
    final Properties props = new Properties();
    try {
      // check if properties file is available
      if (null == this.propFilePath) {
        CDMLogger.getInstance().error("iCDM properties file not Found!! ");
        return null;
      }
      final File file = new File(this.propFilePath);
      props.load(new FileInputStream(file.getAbsolutePath()));
    }
    catch (FileNotFoundException exp) {
      CDMLogger.getInstance().error("iCDM properties file not Found " + exp.getLocalizedMessage(), exp);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error("IO Error occured while loading iCDM Properties file " + exp.getLocalizedMessage(),
          exp);
    }
    return props;
  }


}
