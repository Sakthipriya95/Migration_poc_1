/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import java.io.File;

import org.junit.Before;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.test.RestTestClientProperties;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;


/**
 * @author bne4cob
 */
public abstract class AbstractRestClientTest extends JUnitTest {

  /**
   * Logger for the test logs. Same as TESTER_LOGGER
   */
  protected static final ILoggerAdapter LOG = TESTER_LOGGER;

  /**
   * Test data root directory
   */
  protected static final String TESTDATA_ROOT_DIR = "testdata" + File.separator;

  /**
   * Constant to be used when expected exception message is not thrown
   */
  protected static final String EXPECTED_MESSAGE_NOT_THROWN = "Expected message not thrown";


  /**
   * Set common attributes
   *
   * @throws ApicWebServiceException error during initializing client
   */
  @Before
  public void setAttributes() throws ApicWebServiceException {
    InitializationProperties props = new InitializationProperties();

    props.setLogger(CDMLogger.getInstance());

    // Server from messages.properties
    props.setServer(RestTestClientProperties.getValue(RestTestClientProperties.WS_SERVER_KEY));

    // User name from LDAP authentication
    props.setUserName(RestTestClientProperties.getValue(RestTestClientProperties.DEF_TEST_USER));

    // Get web service token from password service
    try {
      props.setPassword((new PasswordService(false))
          .getPassword(RestTestClientProperties.getValue(RestTestClientProperties.WS_TOKEN_KEY)));
    }
    catch (PasswordNotFoundException e) {
      throw new ApicWebServiceException("TEST_INIT_ERROR", e.getMessage(), e);
    }

    ClientConfiguration.getDefault().initialize(props);
  }

  /**
   * Creates a client configuration, with the given NT user ID. Other properties are same as default
   *
   * @param ntUser NT user ID
   * @return new clent configuration
   * @throws ApicWebServiceException error while creating the configuration
   */
  protected static final ClientConfiguration createClientConfigTestUser(final String ntUser)
      throws ApicWebServiceException {

    ClientConfiguration ret = new ClientConfiguration(ClientConfiguration.getDefault());

    InitializationProperties props = new InitializationProperties();
    props.setUserName(ntUser);
    ret.initialize(props);

    return ret;
  }

  /**
   * @return user's temp directory for tests
   */
  protected static final String getUserTempDir() {
    return TEMP_DIR;
  }

}
