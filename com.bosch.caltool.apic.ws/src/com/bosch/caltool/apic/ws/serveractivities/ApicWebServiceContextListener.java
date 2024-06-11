/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.serveractivities;

import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.externallink.LinkInitProperties;
import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.dmframework.bo.CnsMessageSender;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.IcdmBo;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.WsSystemLoader;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.CDMLoggerUtil;
import com.bosch.caltool.icdm.logger.CnsClientLogger;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author rgo7cob
 */
public class ApicWebServiceContextListener implements ServletContextListener {

  /**
   * Service configuration file path
   */
  private static final String WEB_SERVICE_CONF_PATH_KEY = "WebServiceConfPath";
  /**
   * MBean Object Name - connectioin
   */
  private static final String ON_CONCTR = "*:type=Connector,*";
  /**
   * MBean - query expression for HTTP 1.1 protocol
   */
  private static final QueryExp QRY_HTTP = Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"));
  /**
   * Property - port
   */
  private static final String PROP_PORT = "port";
  /**
   * iCDM User for CNS event messages
   */
  private static final String CNS_CLIENT_ICDM_USER_SERVER = "DGS_ICDM";

  /**
   * {@inheritDoc}
   */
  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    doInitialize();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void contextDestroyed(final ServletContextEvent arg0) {
    new IcdmBo().dispose();

    CnsMessageSender.shutdown();
  }

  /**
   * Initializes database connection, when the request is hit first time, after server start
   */
  private void doInitialize() {
    initializeLoggerNMessages();

    initializeDbConnection();

    // Initialize external link provider
    initializeExternalLinks();

    try (ServiceData serviceData = new ServiceData()) {
      // Log iCDM environment details
      String icdmEnv = new CommonParamLoader(serviceData).getValue(CommonParamKey.ICDM_ENV_CODE);
      String version = new CommonParamLoader(serviceData).getValue(CommonParamKey.ICDM_CLIENT_VERSION);
      getLogger().info("iCDM environment : {}, version {}", icdmEnv, version);

      // Initialize CNS service client
      checkCnsClientInitialization(serviceData);
    }
  }


  /**
   * Initialize logger
   */
  private void initializeLoggerNMessages() {
    // Initialize logger
    CDMLoggerUtil.initialise(WEB_SERVICE_CONF_PATH_KEY);
    // Init the right message.properties file
    String confFilePath = System.getProperty(WEB_SERVICE_CONF_PATH_KEY);
    Messages.setResourceBundleFile(confFilePath);

    getLogger().info("ICDM WS logger successfully created, log level: {}", getLogger().getLogLevel());

    getLogger().info("********************** Starting ICDM Web Service ***************************");
  }

  /**
   * Initialize DB connection
   */
  private void initializeDbConnection() {
    try {
      new IcdmBo().initialize();
    }
    catch (IcdmException e) {
      getLogger().fatal("Error while initializing DB connection. " + e.getMessage(), e);
    }
  }


  /**
   * Initialize external links
   */
  private void initializeExternalLinks() {
    LinkInitProperties props = new LinkInitProperties();
    props.setProtocol(WsCommonConstants.EXT_LINK_PROTOCOL);
    props.setLogger(CDMLogger.getInstance());

    props.addLinkTypes(EXTERNAL_LINK_TYPE.values());

    // Initialize logger and protocol
    LinkRegistry.INSTANCE.initialize(props);
  }

  private void checkCnsClientInitialization(final ServiceData serviceData) {

    CnsClientConfiguration config = new CnsClientConfiguration();

    config.setBaseUrl(new CommonParamLoader(serviceData).getValue(CommonParamKey.CNS_SERVER_URL));
    config.setLogger(CnsClientLogger.getInstance());
    config.setUser(CNS_CLIENT_ICDM_USER_SERVER);
    config.setPassword(new WsSystemLoader(serviceData).getTokenIcdm());

    String portStr = getServerPort();
    if (portStr == null) {
      getLogger().warn("Could not find server port number");
    }
    else {
      getLogger().debug("Server port number is {}", portStr);
      config.setProducerPort(Integer.parseInt(portStr));
    }

    try {
      CnsClientConfiguration.initialize(config);
    }
    catch (CnsServiceClientException e) {
      getLogger().error("Failed to initialize CNS client. " + e.getMessage(), e);
    }

  }

  /**
   * Retrieve server port number from MBeans
   */
  private String getServerPort() {

    String port = null;

    try {
      Set<ObjectName> set = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName(ON_CONCTR), QRY_HTTP);
      port = set.stream().map(on -> on.getKeyProperty(PROP_PORT)).filter(Objects::nonNull).findFirst().orElse(null);
    }
    catch (MalformedObjectNameException e) {
      getLogger().error("Error while trying to find server port : " + e.getMessage(), e);
    }

    return port;

  }

  /**
   * Logger
   */
  private ILoggerAdapter getLogger() {
    return WSLogger.getInstance();
  }

}
