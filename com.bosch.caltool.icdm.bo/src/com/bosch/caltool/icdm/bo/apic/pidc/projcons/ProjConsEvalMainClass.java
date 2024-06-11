/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.io.File;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.adapter.logger.util.Log4jLoggerUtil;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.database.DatabaseInitializer;


/**
 * Main class to run project consistency evaluation
 *
 * @author bne4cob
 */
public final class ProjConsEvalMainClass {

  /**
   * Log file path
   */
  private static final String LOG_FILE_PATH = System.getProperty("java.io.tmpdir") + "/ProjConsEval.log";

  /**
   * Minimum number of arguments
   */
  private static final int APP_ARG_MIN = 1;

  /**
   * Evaluation mode key
   */
  private static final String CMD_EVAL = "EVAL";

  /**
   * Tool Logger
   */
  private static ILoggerAdapter logger;

  /**
   * JPA Logger
   */
  private static ILoggerAdapter jpaLogger;

  /**
   * Private constructor
   */
  private ProjConsEvalMainClass() {
    // Private constructor
  }

  /**
   * Main Method
   *
   * @param args command line arguments
   * @throws IcdmException if database connection fails
   */
  public static void main(final String... args) throws IcdmException {

    // Logger creation
    createLogger();

    // The below call returns error. If new lines are added after runTool(), the return should be validated.
    runTool(args);
  }

  /**
   * Run the ProjectConsistencyEvaluation with the user inputs
   *
   * @param args tool arguments
   * @throws IcdmException
   */
  private static void runTool(final String... args) throws IcdmException {
    if ((args.length < APP_ARG_MIN) || !CMD_EVAL.equals(args[0])) {
      showUsage();
      return;
    }

    Long[] projectIDs = getProjectIDs(args);

    EntityManagerFactory emf = null;

    try {

      emf = createEMF();

      ProjectConsistencyEvaluation eval = new ProjectConsistencyEvaluation(emf, logger, projectIDs);

      eval.validate();
      eval.logResults();

    }
    finally {
      if (emf != null) {
        emf.close();
      }
    }
  }

  /**
   * Convert project ids from comma separated string input to long
   *
   * @param projectIDArg project id argument of the tool
   * @return array of project ids
   */
  private static Long[] getProjectIDs(final String... args) {

    if (args.length == 1) {
      return new Long[0];
    }

    Long[] projectIDs = new Long[args.length - 1];
    String strProjID = null;

    try {
      for (int index = 1; index < args.length; index++) {
        strProjID = args[index];
        projectIDs[index - 1] = Long.parseLong(strProjID);
      }
    }
    catch (NumberFormatException exp) {
      showUsage();
      throw new IllegalArgumentException("Invalid project ID - " + strProjID);
    }

    return projectIDs;
  }

  /**
   * Show usage of the tool, in case of error inputs
   */
  private static void showUsage() {
    logger.error("Error : Invalid arguments");
    logger.error("Usage : ");
    logger.error("    java ProjConsEvalMainClass EVAL");
    logger.error("  OR");
    logger.error("    java ProjConsEvalMainClass EVAL <pidcID 1> <pidcID 2> ...");
  }

  /**
   * Logger creation
   */
  private static void createLogger() {

    String logFileName = LOG_FILE_PATH;

    File logFile = new File(logFileName);

    if (logFile.exists()) {
      logFile.delete();
    }

    Properties prop = new Properties();

    // Default Level - Error
    prop.put("log4j.logger.JPA", "ERROR");

    // Rolling file logging
    prop.put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
    prop.put("log4j.appender.FILE.File", logFileName);
    prop.put("log4j.appender.FILE.MaxFileSize", "2MB");
    prop.put("log4j.appender.FILE.MaxBackupIndex", "5");
    prop.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout ");
    prop.put("log4j.appender.FILE.layout.ConversionPattern", "%d [%t] [%c] %-5p - %m%n");

    // Console Logging
    prop.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
    prop.put("log4j.appender.CONSOLE.Target", "System.out");
    prop.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
    prop.put("log4j.appender.CONSOLE.layout.ConversionPattern", "%d [%t] [%c] %-5p - %m%n");

    // Log Levels
    prop.put("log4j.rootLogger", "DEBUG,FILE,CONSOLE");

    // Level set to avoid NPE from Log4JLoggerAdapter.getLevel() method.
    prop.put("log4j.logger.PROJEVALTOOL", "INFO");
    prop.put("log4j.logger.JPA", "WARN");

    prop.put("log4j.logger.org.apache.axis2", "WARN");
    prop.put("log4j.logger.org.apache.axiom", "WARN");
    prop.put("log4j.logger.org.apache.commons.httpclient", "WARN");
    prop.put("log4j.logger.httpclient.wire", "WARN");

    // TODO: Yet to be implemented
    Log4jLoggerUtil.configureProperties(prop);

    // Applicatin logger
    logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PROJEVALTOOL"));
    logger.info("Log file : {}", logFileName);

    // JPA Logger
    jpaLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("JPA"));
  }


  /**
   * Create the entity manager factory
   *
   * @return EntityManagerFactory
   * @throws IcdmException
   */
  private static EntityManagerFactory createEMF() throws IcdmException {

    String profFilePath = System.getProperty("WebServiceConfPath");
    if (CommonUtils.isEmptyString(profFilePath)) {
      throw new IcdmException("System property 'WebServiceConfPath' not set");
    }
    Messages.setResourceBundleFile(profFilePath);

    // Identify database properties
    final String server = Messages.getString(ObjectStore.P_DB_SERVER);
    final String port = Messages.getString(ObjectStore.P_DB_PORT);

    logger.debug("Creating database connection to " + server + ":" + port);

    Properties dmProps = new Properties();

    dmProps.setProperty(ObjectStore.P_DB_SERVER, server);
    dmProps.setProperty(ObjectStore.P_DB_PORT, port);
    dmProps.setProperty(ObjectStore.P_USER_NAME, System.getProperty("user.name"));
    dmProps.setProperty(ObjectStore.P_CQN_MODE, String.valueOf(ObjectStore.CQN_MODE_COMMAND));
    dmProps.setProperty(ObjectStore.P_CONN_POLL_MODE, ObjectStore.CP_NO);

    // Object store initialization is required for EclipseLinkSessionCustomizer
    ObjectStore.getInstance().initialise(logger, jpaLogger, dmProps);

    DatabaseInitializer dbConnector = new DatabaseInitializer(logger);
    return dbConnector.connect();
  }

}
