/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test;

import org.junit.BeforeClass;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;

/**
 * @author bne4cob
 */
public abstract class AbstractCnsServiceClientTest extends JUnitTest {

  /**
   * CNS Server base URL
   */
  private static final String CNS_SERVER_BASE_URL =
      "http://si-cdm05.de.bosch.com:8180/com.bosch.caltool.icdm.cns.server/services";

  // LOCAL HOST : "http://localhost:8080/com.bosch.caltool.icdm.cns.server/services";

  /**
   * Test user
   */
  private static final String TEST_LOGIN_USER = "BNE4COB";

  /**
   * WS token
   */
  private static final String TEST_TOKEN = "ICDM_PWD";

  /**
   * Default session ID
   */
  private static final String TEST_DEF_SESSION_ID = "test-session-001";

  /**
   * Producer Port number for junit tests
   */
  private static final int PRODUCER_PORT_JUNITS = 1000;

  /**
   * Initialize client configuration
   *
   * @throws CnsServiceClientException error while initializing client
   */
  @BeforeClass
  public static void initializeClientConfig() throws CnsServiceClientException {
    CnsClientConfiguration config = new CnsClientConfiguration();
    config.setBaseUrl(CNS_SERVER_BASE_URL);

    config.setLogger(AUT_LOGGER);

    config.setUser(TEST_LOGIN_USER);
    config.setPassword(TEST_TOKEN);
    config.setSessionId(TEST_DEF_SESSION_ID);
    config.setProducerPort(PRODUCER_PORT_JUNITS);

    CnsClientConfiguration.initialize(config);
  }

  /**
   * @return logger
   */
  protected ILoggerAdapter getLogger() {
    return TESTER_LOGGER;
  }

}
