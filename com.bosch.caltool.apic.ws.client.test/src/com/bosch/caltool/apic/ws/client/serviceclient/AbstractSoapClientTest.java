/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.BeforeClass;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.test.SoapTestClientProperties;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.InitializationProperties;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;


/**
 * @author bne4cob
 */
public abstract class AbstractSoapClientTest extends JUnitTest {


  /**
   * Logger for the test logs. Same as TESTER_LOGGER
   */
  protected static final ILoggerAdapter LOG = TESTER_LOGGER;

  // Initialize Log4j
  static {
    // Set log level of non relevant loggers to WARN
    getLoggerConfig("org.apache").setLevel(Level.WARN);
    getLoggerConfig("httpclient").setLevel(Level.WARN);
  }

  private static boolean initalized = false;


  /**
   * @return LoggerContext
   */
  private static LoggerContext getLoggerContext() {
    return (LoggerContext) LogManager.getContext(false);
  }

  /**
   * @param theLoggerName logger name
   * @return LoggerConfig
   */
  private static LoggerConfig getLoggerConfig(final String theLoggerName) {
    return getLoggerContext().getConfiguration().getLoggerConfig(theLoggerName);
  }

  /**
   * Initialize tests
   *
   * @throws SoapServiceTestException error during initializing client
   */
  @BeforeClass
  public static void initialize() throws SoapServiceTestException {
    // initialized check added to avoid multiple initializations, in case the method is directly called.
    if (!initalized) {
      setAttributes();
      initalized = true;
    }
  }

  /**
   * Set common attributes
   *
   * @throws ApicWebServiceException error during initializing client
   * @throws SoapServiceTestException
   */
  private static void setAttributes() throws SoapServiceTestException {
    InitializationProperties props = new InitializationProperties();

    props.setLogger(CDMLogger.getInstance());

    // Server from messages.properties
    props.setServer(SoapTestClientProperties.getValue(SoapTestClientProperties.WS_SERVER_KEY));

    // User name from LDAP authentication
    props.setUserName(SoapTestClientProperties.getValue(SoapTestClientProperties.DEF_TEST_USER));

    // Get web service token from password service
    try {
      props.setPassword((new PasswordService(false))
          .getPassword(SoapTestClientProperties.getValue(SoapTestClientProperties.WS_TOKEN_KEY)));
      ClientConfiguration.getDefault().initialize(props);
    }
    catch (PasswordNotFoundException | ApicWebServiceException e) {
      throw new SoapServiceTestException(e.getMessage(), e);
    }

  }

  /**
   * Create and log the AbstractStringOutput
   *
   * @param output AbstractStringOutput
   */
  protected void showOutput(final AbstractStringOutput output) {
    output.createOutput();

    // Line break necessary to sepearte Log4J header from data header
    LOG.info("\n" + output.getOutput());
  }

}
