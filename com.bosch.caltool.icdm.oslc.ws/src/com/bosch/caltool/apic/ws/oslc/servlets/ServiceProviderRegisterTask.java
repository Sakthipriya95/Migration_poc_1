/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryClient;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * @author mkl2cob
 */
public class ServiceProviderRegisterTask extends TimerTask {

  /**
   * base path
   */
  private final String basePathOfApp;
  /**
   * ILoggerAdapter
   */
  private final ILoggerAdapter logger;

  /**
   * @param basePath String
   * @param logger ILoggerAdapter
   */
  public ServiceProviderRegisterTask(final String basePath, final ILoggerAdapter logger) {
    this.basePathOfApp = basePath;
    this.logger = logger;
  }

  @Override
  public void run() {
    final URI pidcServiceProviderURI;
    ServiceProviderRegistryClient registryClient;

    try {
      Map<String, Object> parameterMap = new HashMap<String, Object>();

      // TODO put values in parameter map if needed

      final ServiceProvider serviceProvider = PIDCServiceProviderFactory.createPIDCServiceProvider(this.basePathOfApp,
          "iCDM Project ID Card", parameterMap);
      registryClient = new ServiceProviderRegistryClient(JenaProvidersRegistry.getProviders());
      pidcServiceProviderURI = registryClient.registerServiceProvider(serviceProvider);

      // set values of registered service provider and URI
      ICDMOSLCServiceProviderSingleton.setServiceProviderURI(pidcServiceProviderURI);
      ICDMOSLCServiceProviderSingleton.setServiceProvider(serviceProvider);

      this.logger.info("Service provider registration complete.");
    }
    catch (final Exception exception) {
      StackTraceElement[] stackTrace = exception.getStackTrace();
      this.logger.error("Unable to register with service provider catalog" + exception.getMessage());
      int index = 0;
      while (index < stackTrace.length) {
        System.out.println(stackTrace[index++]);
      }
      return;
    }
  }
}
