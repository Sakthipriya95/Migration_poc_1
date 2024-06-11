package com.bosch.caltool.monicareportparser;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author bne4cob
 */
public class Activator implements BundleActivator {

  /**
   * Bundle context
   */
  private static BundleContext context;

  /**
   * Get context
   * 
   * @return bundle context
   */
  static BundleContext getContext() { // NOPMD by bne4cob on 10/30/13 9:58 AM
    return context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public final void start(final BundleContext bundleContext) throws Exception { // NOPMD by bne4cob on 10/30/13 9:58 AM
    Activator.context = bundleContext;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public final void stop(final BundleContext bundleContext) throws Exception { // NOPMD by bne4cob on 10/30/13 9:58 AM
    Activator.context = null; // NOPMD by bne4cob on 10/30/13 9:59 AM
  }

}
