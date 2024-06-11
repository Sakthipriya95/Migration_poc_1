/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;


/**
 * System tray management of the application
 *
 * @author bne4cob
 */
public enum SystemTray {

                        /**
                         * Unique instance
                         */
                        INSTANCE;

  /**
   * X position of ToolTip when system tray is not available
   */
  private static final int TT_POS_X = 400;

  /**
   * Y position of ToolTip when system tray is not available
   */
  private static final int TT_POS_Y = 400;

  /**
   * Notification title
   */
  private static final String NOTIFICATION_TITLE = "iCDM";

  /**
   * Tray item
   */
  private TrayItem trayItem;

  /**
   * Info tooltip
   */
  private ToolTip infoToolTip;

  /**
   * Warning tooltip
   */
  private ToolTip warnToolTip;

  /**
   * Private constructor to prevent instantiationa
   */
  private SystemTray() {
    Tray tray = Display.getDefault().getSystemTray();
    if ((tray != null) && !isTrayAvailable()) {
      this.trayItem = new TrayItem(tray, SWT.NONE);
      this.trayItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ICDM_LOGO_16X16));
    }
  }

  /**
   * Shows balloon tooltip with the given message and icon style
   *
   * @param message message
   * @param msgStyle style of the message identified by SWT icon SWT.ICON_INFORMATION/SWT.ICON_WARNING
   */
  public void showToolTip(final String message, final int msgStyle) {
    final ToolTip tip = getToolTip(msgStyle);
    tip.setText(NOTIFICATION_TITLE);

    tip.setMessage(message);

    if (isTrayAvailable()) {
      this.trayItem.setToolTip(tip);
    }
    else {
      tip.setLocation(TT_POS_X, TT_POS_Y);
    }

    tip.setVisible(true);
  }

  /**
   * @return true if system tray is available
   */
  private boolean isTrayAvailable() {
    return this.trayItem != null;
  }

  /**
   * Gets the tooltip based on the style
   *
   * @param msgStyle message style
   * @return tooltip
   */
  private ToolTip getToolTip(final int msgStyle) {

    // Warning message
    if (msgStyle == SWT.ICON_WARNING) {
      if ((this.warnToolTip == null) || this.warnToolTip.isDisposed()) {
        this.warnToolTip = createToolTip(SWT.BALLOON | SWT.ICON_WARNING);
      }
      return this.warnToolTip;
    }

    // If not warning, show as info message
    if ((this.infoToolTip == null) || this.infoToolTip.isDisposed()) {
      this.infoToolTip = createToolTip(SWT.BALLOON | SWT.ICON_INFORMATION);
    }
    return this.infoToolTip;

  }

  /**
   * Creates the tooltip for the given style
   *
   * @param style style
   * @return tooltip
   */
  private ToolTip createToolTip(final int style) {
    Display display = Display.getDefault();
    Shell shell = display.getActiveShell();
    if (shell == null) {
      shell = new Shell(display);
    }

    return new ToolTip(shell, style);
  }

}
