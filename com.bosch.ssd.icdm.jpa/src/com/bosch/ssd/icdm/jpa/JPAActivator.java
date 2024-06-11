package com.bosch.ssd.icdm.jpa;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author apl1cob Activator class for ssd icdm interface plugin
 */
public class JPAActivator implements BundleActivator {


  /**
   * The shared instance
   */
  private JPAActivator plugin;

  private BundleContext context;
  
  /**
   * The constructor
   */
  public JPAActivator() {
    // Empty
  }

  /**
   * @return context
   */
  public BundleContext getContext() {
    return this.context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    this.context = bundleContext;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    this.context = null;
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public JPAActivator getDefault() {
    return this.plugin;
  }
}
