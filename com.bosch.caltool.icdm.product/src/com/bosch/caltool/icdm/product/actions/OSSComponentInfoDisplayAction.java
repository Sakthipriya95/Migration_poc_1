/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;


import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.calcomp.oss.display.OSSInfoDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.product.Activator;


/**
 *
 */
public class OSSComponentInfoDisplayAction extends Action implements IWorkbenchAction {

  private static final String ACTION_ID = "com.bosch.icdm.OssInfoDisplayComponent";

  private static final String ACTION_TEXT = "OSS Components Information";


  /**
   *
   */
  public OSSComponentInfoDisplayAction() {
    super();
    // sets Action id,ActionText and imageDescriptor
    super.setId(ACTION_ID);
    super.setText(ACTION_TEXT);
    super.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OSS_INFO));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // ovverriden method

  }

  @Override
  public void run() {
    URL url = Activator.getDefault().getBundle().getResource("resources/icdm.properties");
    try {
      url = FileLocator.toFileURL(url);
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(String.format("Exception while getting properties file location-- %s", e));
    }
    OSSInfoDialog dialog = new OSSInfoDialog(Display.getCurrent().getActiveShell(), url.getFile());
    dialog.open();

  }
}

