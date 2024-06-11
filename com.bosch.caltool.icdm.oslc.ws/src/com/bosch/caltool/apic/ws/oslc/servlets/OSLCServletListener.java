/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryClient;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.oslc.ws.db.OSLCObjectStore;


/**
 * @author mkl2cob
 */
public class OSLCServletListener implements ServletContextListener {

  private static final String SYS_PROPERTY_SCHEME = OSLCServletListener.class.getPackage().getName() + ".scheme";
  private static final String SYS_PROPERTY_PORT = OSLCServletListener.class.getPackage().getName() + ".port";

  private static final int REGISTRATION_DELAY = 10000; // Delay before contacting OSLC4JRegistry
  private static final String SYSTEM_PROPERTY_NAME_REGISTRY_URI =
      ServiceProviderRegistryURIs.class.getPackage().getName() + ".registryuri";

  private static final String HOST = getHost();


  private ServiceProviderRegistryClient registryClient;

  /**
   *
   */
  public OSLCServletListener() {
    super();
  }


  @Override
  public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    if (this.registryClient != null) {
      // Don't try to deregister if catalog is on same HOST as us.
      if (!this.registryClient.getClient().getUri().contains(HOST)) {
        try {
          this.registryClient.deregisterServiceProvider(ICDMOSLCServiceProviderSingleton.getServiceProviderURI());
        }
        catch (final Exception exception) {
          CDMLogger.getInstance().error("Unable to deregister with service provider catalog", exception);
        }
        finally {
          this.registryClient = null;
          ICDMOSLCServiceProviderSingleton.setServiceProviderURI(null);
        }
      }
    }

  }

  @Override
  public void contextInitialized(final ServletContextEvent servletContextEvent) {
    ILoggerAdapter logger = OSLCObjectStore.getLogger();
    final String basePathForOSLCProvider = generateBasePathForOSLC(servletContextEvent);
    // set the oslc registry URI
    System.setProperty(SYSTEM_PROPERTY_NAME_REGISTRY_URI, Messages.getString(SYSTEM_PROPERTY_NAME_REGISTRY_URI));

    Timer regTimer = new Timer();
    regTimer.schedule(new ServiceProviderRegisterTask(basePathForOSLCProvider, logger), REGISTRATION_DELAY);


  }

  private static String generateBasePathForOSLC(final ServletContextEvent servletContextEvent) {
    final ServletContext servletContext = servletContextEvent.getServletContext();

    String scheme = Messages.getString(SYS_PROPERTY_SCHEME);
    if (scheme == null) {
      scheme = servletContext.getInitParameter(SYS_PROPERTY_SCHEME);
    }

    String port = Messages.getString(SYS_PROPERTY_PORT);
    if (port == null) {
      port = servletContext.getInitParameter(SYS_PROPERTY_PORT);
    }

    return scheme + "://" + HOST + ":" + port + servletContext.getContextPath();
  }

  private static String getHost() {
    try {
      return InetAddress.getLocalHost().getCanonicalHostName();
    }
    catch (final UnknownHostException exception) {
      return "localhost";
    }
  }
}
