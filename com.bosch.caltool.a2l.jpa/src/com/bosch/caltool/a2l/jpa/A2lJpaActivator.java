/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author DMO5COB
 */
public class A2lJpaActivator implements BundleActivator {

  public static final String PLUGIN_ID = "com.bosch.caltool.a2l.jpa";

  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    A2lJpaActivator.context = context;
  }

  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(final BundleContext bundleContext) throws Exception {
    setContext(bundleContext);
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(final BundleContext bundleContext) throws Exception {
    setContext(null);
  }


}
