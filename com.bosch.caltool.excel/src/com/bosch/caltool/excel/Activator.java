package com.bosch.caltool.excel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the com.bosch.caltool.excel plugin
 *
 * @author bne4cob
 */
public class Activator implements BundleActivator {

  /**
   * Bundle context
   */
  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    Activator.context = context;
  }

  /**
   * @return the bundle context
   */
  public static BundleContext getContext() {
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
    setContext(null); // NOPMD by bne4cob on 6/5/13 12:45 PM Auto generated code
  }

}
