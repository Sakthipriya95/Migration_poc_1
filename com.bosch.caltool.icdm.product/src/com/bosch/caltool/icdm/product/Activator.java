/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.bosch.calcomp.externallink.LinkInitProperties;
import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calcomp.externallink.process.listener.URIListeningServer;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.client.bo.general.IcdmSessionHandler;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.CDMLoggerUtil;
import com.bosch.caltool.icdm.product.extlink.ExtLinkLoggingCustomization;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.caltool.icdm.product.util.IcdmWsClientLinkInfoProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = "com.bosch.caltool.icdm.product"; //$NON-NLS-1$

  /**
   * The shared instance
   */
  private static Activator plugin;


  /**
   * @param plugin the plugin to set
   */
  private static void setPlugin(final Activator plugin) {
    Activator.plugin = plugin;
  }

  /**
   * Key to get the bundle's version
   */
  private static final String BUNDLE_VERSION = "Bundle-Version";

  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public final void start(final BundleContext context) throws Exception { // NOPMD by bne4cob on 8/16/13 3:01 PM
    super.start(context);
    setPlugin(this);

    // ICDM-1493
    CDMLoggerUtil.initialise(null);
    CDMLogger.getInstance().info(ICDMConstants.STARTUP_LOG_MESSAGE);

    // ICDM-1649
    initializeExternalLinks();

  }


  /**
   * Initialize external link framework
   */
  private void initializeExternalLinks() {
    LinkInitProperties props = new LinkInitProperties();

    props.setProtocol(WsCommonConstants.EXT_LINK_PROTOCOL);
    props.setLogger(CDMLogger.getInstance());
    props.setLogCustomization(new ExtLinkLoggingCustomization());
    props.setLinkInfoProvider(new IcdmWsClientLinkInfoProvider());

    props.addLinkTypes(EXTERNAL_LINK_TYPE.values());

    // Initialize logger and protocol
    LinkRegistry.INSTANCE.initialize(props);

    // Start the external URI listener
    new URIListeningServer().start();

  }


  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public final void stop(final BundleContext context) throws Exception {
    new IcdmSessionHandler().closeSessions();
    setPlugin(null); // NOPMD by bne4cob on 8/16/13 3:01 PM Eclipse generated code
    super.stop(context);
    File tempFilesDir = new File(CommonUtils.getICDMTmpFileDirectoryPath() + "");
    FileUtils.cleanDirectory(tempFilesDir);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(final String path) {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  /**
   * ICDM-252
   *
   * @return String - This method will return the plugin current version
   */
  public String getPluginVersion() {
    return getBundle().getHeaders().get(BUNDLE_VERSION);
  }


}
