/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author bne4cob
 */
public abstract class AbstractDbTest extends JUnitTest {

  /**
   * Logger for the tests
   */
  protected static final ILoggerAdapter LOG = TESTER_LOGGER;

  /**
   * JPA Logger
   */
  private static final Log4JLoggerAdapterImpl JPA_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("JPA"));

  private static EntityManagerFactory emf;
  private EntityManager entMgr;

  /**
   * Initialize logger, DB connection etc.
   *
   * @throws IcdmException error during initialization
   */
  @BeforeClass
  public static void initialize() throws IcdmException {
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
    String profFilePath = System.getProperty("WebServiceConfPath");
    if (CommonUtils.isEmptyString(profFilePath)) {
      throw new IcdmException("System property 'WebServiceConfPath' not set");
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
    DatabaseInitializer dbInit = new DatabaseInitializer(CDMLogger.getInstance());
    emf = dbInit.connect();
  }

  /**
   * Close DB connection
   */
  @AfterClass
  public static void finish() {
    if ((emf != null) && emf.isOpen()) {
      emf.close();
    }
  }


  /**
   * Create Entity manager for current test
   */
  @Before
  public void createEm() {
    if (emf != null) {
      this.entMgr = emf.createEntityManager();
    }
  }

  /**
   * Close Entity manager for current test
   */
  @After
  public void closeEm() {
    if ((getEntMgr() != null) && getEntMgr().isOpen()) {
      getEntMgr().close();
    }
  }

  /**
   * @return the entMgr
   */
  public final EntityManager getEntMgr() {
    return this.entMgr;
  }

}
