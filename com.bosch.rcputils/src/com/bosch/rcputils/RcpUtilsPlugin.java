/*******************************************************************************
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.bosch.rcputils;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class RcpUtilsPlugin extends Plugin {

  public static final String PLUGIN_ID = "com.bosch.rcputils";

  private static RcpUtilsPlugin plugin;


  /**
   * @param plugin the plugin to set
   */
  private static void setPlugin(RcpUtilsPlugin plugin) {
    RcpUtilsPlugin.plugin = plugin;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    setPlugin(this);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    setPlugin(null);
    super.stop(context);
  }

  public static RcpUtilsPlugin getDefault() {
    return plugin;
  }

  public static void logException(Exception excep) {
    getDefault().getLog().log(new Status(Status.ERROR, RcpUtilsPlugin.PLUGIN_ID, -1, excep.getMessage(), excep));
  }
}
