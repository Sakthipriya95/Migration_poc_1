/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author bne4cob
 */
public class Activator implements BundleActivator {

  /**
   *  The plug-in ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.client.bo"; //$NON-NLS-1$
  
  private static BundleContext context;

  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    Activator.context = null;
  }
}
