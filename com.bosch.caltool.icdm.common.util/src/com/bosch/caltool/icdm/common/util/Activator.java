package com.bosch.caltool.icdm.common.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {


  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.common.util"; //$NON-NLS-1$

  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    Activator.context = context;
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
