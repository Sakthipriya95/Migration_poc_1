/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.osgi.framework.Bundle;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.citi.lite.client.util.CITIConstants;
import com.bosch.citi.lite.client.util.CITIUtil;


/**
 * @author DJA7COB
 *
 */
public class CitiToolHotlineOpenAction extends Action implements IWorkbenchAction {

  /**
   * 
   */
  private static final String CITI_ICON_PATH = "icons/chat_20px.png";
  /**
   * 
   */
  private static final String CITI_BUNDLE = "com.bosch.citi.lite.client";
  /**
   * Action text
   */
  private static final String ACTION_TEXT = "CITI - Tool Hotline Support";
  /**
   * hot line action ID
   */
  private static final String CITI_ACTION_ID = "com.bosch.icdm.citi";

  /**
   * 
   */
  public CitiToolHotlineOpenAction() {
    super();
    super.setId(CITI_ACTION_ID);
    super.setText(ACTION_TEXT);
    super.setImageDescriptor(getLogoFromCitiPlugin());
  }

  /**
   * @return
   */
  private ImageDescriptor getLogoFromCitiPlugin() {
    Bundle bundle = Platform.getBundle(CITI_BUNDLE);
    URL url = FileLocator.find(bundle, new Path(CITI_ICON_PATH), null);
    return ImageDescriptor.createFromURL(url);
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public void run() {
    CITIUtil.getInstance().setCalledFrom(CITIConstants.NATIVE_METHOD);
    CommonUiUtils.getInstance().showView(ICDMConstants.CITI_VIEW_ID);
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // NA
  }


}
