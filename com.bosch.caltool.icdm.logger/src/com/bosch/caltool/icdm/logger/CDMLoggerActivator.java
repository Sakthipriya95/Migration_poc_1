package com.bosch.caltool.icdm.logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CDMLoggerActivator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.logger"; //$NON-NLS-1$


  // The shared instance
  private static CDMLoggerActivator plugin;


  /**
   * @param plugin the plugin to set
   */
  private static void setPlugin(final CDMLoggerActivator plugin) {
    CDMLoggerActivator.plugin = plugin;
  }

  /**
   * BundleContext reference
   */
  private static BundleContext context;


  /**
   * @param context the context to set
   */
  private static void setContext(final BundleContext context) {
    CDMLoggerActivator.context = context;
  }

  /**
   * Gets BundleContext
   *
   * @return BundleContext
   */
  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    setContext(bundleContext);
    super.start(context);
    setPlugin(this);
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    setContext(null);
    setPlugin(null);
    super.stop(bundleContext);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static CDMLoggerActivator getDefault() {
    return plugin;
  }


}
