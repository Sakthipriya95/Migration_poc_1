package com.bosch.caltool.icdm.report;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * * @author bne4cob
 */
public class Activator implements BundleActivator {


  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.report";

  /**
   * The bundle context
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
  @Override
  public final void start(final BundleContext bundleContext) {
    setContext(bundleContext);
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public final void stop(final BundleContext bundleContext) {
    setContext(null); // NOPMD by bne4cob on 6/5/13 5:45 PM Auto generated code
  }

}
