/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * JPAActivator.java - The main activator class
 */
public class JPAActivator implements BundleActivator {

  /**
   * PLUGIN_ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.jpa";

  /**
   * Bundle context
   */
  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    JPAActivator.context = context;
  }

  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final BundleContext bundleContext) {
    setContext(bundleContext);
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public void stop(final BundleContext bundleContext) {
    setContext(null);
  }


}
