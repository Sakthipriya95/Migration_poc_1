/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author bne4cob
 */
public class Activator implements BundleActivator {

  /**
   * BundleContext
   */
  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    Activator.context = context;
  }

  /**
   * @return context
   */
  protected static BundleContext getContext() {
    return context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final BundleContext bundleContext) {
    setContext(bundleContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop(final BundleContext bundleContext) {
    setContext(null); // NOPMD by BNE4COB on 12/12/13 5:15 PM Auto generated code
  }

}
