/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.test;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.IcdmBo;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.security.Decryptor;

/**
 * Abstract class for BO tests
 * <p>
 * To run the tests, add the below command line arguments<br>
 * -DWebServiceConfPath=&ltPath to config file directory&gt -Djavax.net.ssl.trustStore=&ltpath to cacerts file&gt <br>
 * e.g. <br>
 * -DWebServiceConfPath="D:\RBEI\iCDM\Bebith\iCDM-WS\OR10WS1\conf"
 * -Djavax.net.ssl.trustStore="C:/toolbase/icdm_client/3.0.7/bin/security/cacerts"
 *
 * @author bne4cob
 */
public abstract class AbstractIcdmBOTest extends JUnitTest {

  /**
   * Defaults
   */
  private static final String DEFAULT_USER = "BNE4COB";
  private static final String DEFAULT_LANGUAGE = "English";
  private static final String DEFAULT_TIMEZONE = "Asia/Calcutta";

  /**
   * Config file path parameter name
   */
  private static final String CONFIG_PATH_PROP = "WebServiceConfPath";

  /**
   * Logger for the tests
   */
  protected static final ILoggerAdapter LOG = TESTER_LOGGER;

  /**
   * JPA Logger
   */
  private static final Log4JLoggerAdapterImpl JPA_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("JPA"));

  private ServiceData serviceData;

  /**
   * Initialize logger, DB connection etc.
   *
   * @throws IcdmException error during initialization
   */
  @BeforeClass
  public static final void initialize() throws IcdmException {
    initLogger();
    initDbConn();
  }

  /**
   * Initialize logger
   */
  private static void initLogger() {
    JPA_LOGGER.setLogLevel(ILoggerAdapter.LEVEL_INFO);
    CDMLogger.getInstance().setLogLevel(ILoggerAdapter.LEVEL_DEBUG);
  }

  /**
   * Initialize Db connection, entities
   *
   * @throws IcdmException
   */
  private static void initDbConn() throws IcdmException {

    // init Config file
    String profFilePath = System.getProperty(CONFIG_PATH_PROP);
    if (CommonUtils.isEmptyString(profFilePath)) {
      throw new IcdmException("System property '" + CONFIG_PATH_PROP + "' not set");
    }
    Messages.setResourceBundleFile(profFilePath);

    // Identify database properties
    final String server = Messages.getString(ObjectStore.P_DB_SERVER);
    final String port = Messages.getString(ObjectStore.P_DB_PORT);

    LOG.debug("Creating database connection to {}:{}", server, port);

    Properties dmProps = new Properties();

    dmProps.setProperty(ObjectStore.P_DB_SERVER, server);
    dmProps.setProperty(ObjectStore.P_DB_PORT, port);
    dmProps.setProperty(ObjectStore.P_USER_NAME, System.getProperty("user.name"));
    dmProps.setProperty(ObjectStore.P_CQN_MODE, String.valueOf(ObjectStore.CQN_MODE_COMMAND));
    dmProps.setProperty(ObjectStore.P_CONN_POLL_MODE, ObjectStore.CP_NO);

    // Object store initialization is required for EclipseLinkSessionCustomizer
    ObjectStore.getInstance().initialise(CDMLogger.getInstance(), JPA_LOGGER, dmProps);

    // Init DB
    new IcdmBo().initialize();
  }

  /**
   * Close DB connection
   */
  @AfterClass
  public static final void finish() {
    new IcdmBo().dispose();
  }

  /**
   * Create Entity manager for current test
   */
  @Before
  public final void createSD() {
    this.serviceData = new ServiceData();

    this.serviceData.setUsername(DEFAULT_USER);

    // Note : By default Web service's config does not have this key. Add if required (refer iCDM client's config)
    String password =
        new PasswordServiceWrapper(CDMLogger.getInstance()).getPassword(Messages.getString(Messages.ICDM_WS_TOKEN_KEY));
    password = Decryptor.INSTANCE.decrypt(password, CDMLogger.getInstance());
    this.serviceData.setPassword(password);

    this.serviceData.setLanguage(DEFAULT_LANGUAGE);

    // Set timezone to 'Asia/Calcutta', as jenkins test are executed in this timezone
    this.serviceData.setTimezone(DEFAULT_TIMEZONE);

    // Authenticate user
    new UserLoader(this.serviceData).authenticateCurrentUser();
  }

  /**
   * Close Entity manager for current test
   */
  @After
  public final void closeSD() {
    this.serviceData.close();
  }

  /**
   * @return the serviceData
   */
  public final ServiceData getServiceData() {
    return this.serviceData;
  }


}
