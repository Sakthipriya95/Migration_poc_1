/**
 * 
 */
package com.bosch.easee.eASEEcdm_Service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.util.LoggerUtil;

/**
 * @author vau3cob
 */
public class PropertyFileReader {

  private static PropertyFileReader propertyFileReader;

  private Properties properties;

  private ILoggerAdapter logger = LoggerUtil.getLogger();

  /**
   * @return the singleton instance of property file reader
   */
  public static PropertyFileReader getInstance() {
    if (propertyFileReader == null) {
      propertyFileReader = new PropertyFileReader();
    }
    return propertyFileReader;

  }

  private PropertyFileReader() {
    try {
      File propFile = null;
      URL dtdUrl = this.getClass().getClassLoader().getResource("/resources/servicelib.properties");
      if (dtdUrl != null) {
        dtdUrl = FileLocator.toFileURL(dtdUrl);
        propFile = new File(dtdUrl.getFile());
      }
      if (propFile == null) {
        propFile = new File("./resources/servicelib.properties");
      }
      final FileInputStream fileInput = new FileInputStream(propFile);
      properties = new Properties();
      properties.load(fileInput);
      fileInput.close();
    }
    catch (IOException e1) {
      logger.error("Error while reading property file " + e1.getMessage());
    }
  }

  /**
   * @param propertyName
   * @return
   */
  public String getProperty(String propertyName) {
    String value = null;
    if (properties != null && properties.containsKey(propertyName)) {
      value = properties.getProperty(propertyName);
    }
    return value;
  }

}
