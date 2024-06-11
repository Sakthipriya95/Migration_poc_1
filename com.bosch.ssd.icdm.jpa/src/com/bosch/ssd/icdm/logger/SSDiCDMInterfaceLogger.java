package com.bosch.ssd.icdm.logger;


import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.adapter.logger.util.Log4jLoggerUtil;

/**
 * A class to create a base Logger for SSD iCDM Interface
 *
 * @author SSN9COB
 */
public final class SSDiCDMInterfaceLogger {

  private static ILoggerAdapter logger;

  // private constructor
  private SSDiCDMInterfaceLogger() {}


  /**
   * @param logger the logger to set
   */
  public static void setLogger(final ILoggerAdapter logger) {
    SSDiCDMInterfaceLogger.logger = logger;
  }

  /**
   * @return logger
   */
  public static ILoggerAdapter getLogger() {
    if (Objects.isNull(logger)) {
      logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("SSDICDMINTERFACE"));
    }
    return logger;
  }

  /**
   * Set level for logger
   *
   * @param level int
   */
  public static void setLogLevel(final int level) {
    logger.setLogLevel(level);
  }


  /**
   * Log Message to be used to log different levels of info
   *
   * @param message Message to be logged
   * @param level logger level
   * @param cause cause, if fatal or error
   */
  public static void logMessage(final String message, final int level, final Throwable cause) {
    if (level == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug(message);
    }
    else if (level == ILoggerAdapter.LEVEL_INFO) {
      getLogger().info(message);
    }
    else if (level == ILoggerAdapter.LEVEL_ERROR) {
      getLogger().error(message, cause);
    }
    else if (level == ILoggerAdapter.LEVEL_WARN) {
      getLogger().warn(message, cause);
    }
    else if (level == ILoggerAdapter.LEVEL_FATAL) {
      getLogger().fatal(message, cause);
    }
  }


  /**
   */
  public static void initClient() {
    Path path = FileSystems.getDefault().getPath("lib", "log4j2.xml");
  
    // Read the log4j properties
//    URL url = SSDiCDMInterfaceLogger.class.getClassLoader().getResource("log4j2.xml")
    File log4jProperties = new File(path.toAbsolutePath().toString());
//      log4jProperties = new File(FileLocator.toFileURL(url).getFile())
    // Validate file existance
    if (log4jProperties.exists()) {
      // Configure Properties
      Log4jLoggerUtil.configureProperties(SSDiCDMInterfaceLogger.class.getClassLoader(),
          log4jProperties.getAbsolutePath());
    }
  }

}
