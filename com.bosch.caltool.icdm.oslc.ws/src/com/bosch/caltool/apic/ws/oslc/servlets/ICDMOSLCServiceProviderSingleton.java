/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;

import java.net.URI;

import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;


/**
 * @author mkl2cob
 */
public class ICDMOSLCServiceProviderSingleton {

  /**
   * ServiceProvider instance
   */
  private static ServiceProvider iCDMProviderSingelton;
  /**
   * URI
   */
  private static URI serviceProviderURISingelton;

  private ICDMOSLCServiceProviderSingleton() {
    super();
  }

  /**
   * @return ServiceProvider
   */
  public static synchronized ServiceProvider getServiceProvider() {
    return iCDMProviderSingelton;
  }

  /**
   * @param serviceProvider ServiceProvider
   */
  public static void setServiceProvider(final ServiceProvider serviceProvider) {
    ICDMOSLCServiceProviderSingleton.iCDMProviderSingelton = serviceProvider;
  }

  /**
   * @return URI
   */
  public static synchronized URI getServiceProviderURI() {
    return serviceProviderURISingelton;
  }

  /**
   * @param serviceProviderURI URI
   */
  public static void setServiceProviderURI(final URI serviceProviderURI) {
    ICDMOSLCServiceProviderSingleton.serviceProviderURISingelton = serviceProviderURI;
  }
}
