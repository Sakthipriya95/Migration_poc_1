package com.bosch.caltool.labfunparser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.labfunparser"; //$NON-NLS-1$

  /**
   * Bundle context
   */
  private static BundleContext context;

  /**
   * @return the bundle context
   */
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
    Activator.context = bundleContext;
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
    Activator.context = null; // NOPMD by bne4cob on 6/5/13 12:45 PM Auto generated code
  }


}
