/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author adn1cob
 */
public class CPJpaActivator implements BundleActivator {

  /**
   * Plugin ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.comppkg.jpa";

  /**
   * Bundle context
   */
  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    CPJpaActivator.context = context;
  }

  /**
   * @return BundleContext
   */
  static BundleContext getContext() {
    return context;
  }

  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) {
    setContext(bundleContext);
  }

  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) {
    setContext(null); // NOPMD by adn1cob on 6/30/14 10:14 AM
  }

}
