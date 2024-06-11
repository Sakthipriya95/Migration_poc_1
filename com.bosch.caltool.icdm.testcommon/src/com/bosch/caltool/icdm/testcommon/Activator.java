/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.testcommon;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author bne4cob
 */
public class Activator implements BundleActivator {

  /**
   * Bundle Context
   */
  private static BundleContext context;

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
  public void start(final BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
  }

  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    Activator.context = null;
  }

}
