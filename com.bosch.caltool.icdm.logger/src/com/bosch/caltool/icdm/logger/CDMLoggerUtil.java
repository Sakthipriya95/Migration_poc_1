package com.bosch.caltool.icdm.logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.eclipse.core.runtime.FileLocator;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.adapter.logger.util.Log4jLoggerUtil;
import com.bosch.calcomp.adapter.logger.util.LoggerUtil;


/**
 * This class provides logger utilities
 */
public final class CDMLoggerUtil {


  /**
   * The private constructor, to prevent object creation of this utility class
   */
  private CDMLoggerUtil() {
    // Nothing to do
  }

  /**
   * icdm-1493 initialising the logger properties
   *
   * @param webServiceConfigPath String
   */
  public static void initialise(final String webServiceConfigPath) {
    if ("WebService".equals(System.getProperty("applicationMode"))) {
      initWebService(webServiceConfigPath);
    }
    else {
      initClient();
    }

    // Set this as default logger in dependant components
    ILoggerAdapter defLoggerUtilLgr = new Log4JLoggerAdapterImpl(LogManager.getLogger("LoggerUtil"));
    LoggerUtil.setLogger(defLoggerUtilLgr);
  }

  /**
   * icdm-1493 initialise logger for client
   */
  private static void initClient() {

    // Initialize log4j properties
    File log4jProperties = null;
    // Read the log4j properties
    final URL url = CDMLoggerActivator.getDefault().getBundle().getResource("/config/log4j2.xml");

    try {
      if (url != null) {
        log4jProperties = new File(FileLocator.toFileURL(url).getFile());
        // Validate file existance
        if (log4jProperties.exists()) {
          // Configure Properties
          Log4jLoggerUtil.configureProperties(CDMLogger.class.getClassLoader(), log4jProperties.getAbsolutePath());
          Configuration config = Log4jLoggerUtil.getLoggerContext(CDMLogger.class.getClassLoader()).getConfiguration();
          // Set Log File Path and Log File name by reading the log4j2.xml config file
          if (null != config) {
            setLogPathFromConfig(Log4jLoggerUtil.getLoggerContext(CDMLogger.class.getClassLoader()).getConfiguration());
          }
        }
      }
    }
    catch (IOException e1) {
      CDMLogger.getInstance().error("Could not find log4j properties ." + e1.getMessage(), e1);
    }
  }

  /**
   * icdm-1493 initialise logger for web service
   *
   * @param configPropName String
   */
  private static void initWebService(final String configPropName) {
    File log4jProperties = new File(System.getProperty(configPropName) + "/log4j2.xml");
    // Validate file existance
    if (log4jProperties.exists()) {
      // Configure Properties
      Log4jLoggerUtil.configureProperties(log4jProperties.getAbsolutePath());
    }
  }


  /**
   * Method to set the log path from log4j2.xml
   *
   * @param config the configuration
   */
  private static void setLogPathFromConfig(final Configuration config) {
    String logPath = "";
    Map<String, Appender> appendersMap = config.getAppenders();
    for (Appender appender : appendersMap.values()) {
      if (appender instanceof RollingFileAppender) {
        logPath = ((RollingFileAppender) appender).getFileName();// C:\Users\..\AppData\Local\Temp\3\/iCDM_DEV/iCDM.log
      }
    }

    final File dir = new File(logPath);
    String logFileDir = dir.getParent();
    if (!dir.exists()) {
      final boolean retValue = new File(logFileDir).mkdirs();
      if (!retValue) {
        printErrorMessageToConsole("WARNING!!! Unable to create log file directory " + logFileDir);
      }
    }
    LogFileInfo.getInstance().setLogFileName(dir.getName());
    setLogFile(dir.getAbsolutePath());
  }


  /**
   * @param message
   */
  private static void printErrorMessageToConsole(final String message) {
    // At this point logger is not initialised, so the error has to be written to console
    System.err.println(message);
  }

  /**
   * ICDM-1493 Sets the log file path.
   *
   * @param logFilePath the log file path
   */
  public static void setLogFile(final String logFilePath) {
    LogFileInfo.getInstance().setLogFilePath(logFilePath);
  }

}
