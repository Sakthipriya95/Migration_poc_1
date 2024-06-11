/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.net.MalformedURLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.rcputils.browser.BrowserUtil;

/**
 * @author mkl2cob
 */
public class HelpIconAction extends Action {

  /**
   * wiki link for help
   */
  private final Link helpLink;

  /**
   * @param tooBarMngr IToolBarManager
   * @param helpLink2 String
   */
  public HelpIconAction(final IToolBarManager tooBarMngr, final Link helpLink2) {
    super(helpLink2.getDescription(), ImageManager.getImageDescriptor(ImageKeys.HELP_16X16));
    this.helpLink = helpLink2;
    // set tooltip
    setToolTipText(this.helpLink.getDescription());
    // set description
    setDescription(this.helpLink.getDescription());
    setEnabled(true);
  }

  /**
   * Refresh the table viewer
   */
  @Override
  public final void run() {
    // open wiki link
    try {
      BrowserUtil.getInstance().openExternalBrowser(this.helpLink.getLinkUrl());
    }
    catch (PartInitException | MalformedURLException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }
}
