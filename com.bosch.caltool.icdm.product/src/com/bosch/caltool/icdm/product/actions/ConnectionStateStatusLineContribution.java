/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.bosch.caltool.icdm.client.bo.connectionstate.ConnectionState;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * Status line contribution of network connection state. Automatically gets updated by listening to connection poller
 *
 * @author bne4cob
 */
public class ConnectionStateStatusLineContribution extends ContributionItem {

  /**
   * Contribution's ID
   */
  public static final String OBJ_ID = "com.bosch.caltool.icdm.product.actions.ConnectionStateStatusLineContribution";

  /**
   * Label Width
   */
  private static final int STATUS_LBL_WIDTH_CHRS = 30;

  /**
   * Status Label
   */
  private CLabel label;

  /**
   * Constructor. Adds this object as a connection status listener
   */
  public ConnectionStateStatusLineContribution() {
    super(OBJ_ID);
  }

  /**
   * Status line
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void fill(final Composite parent) {
    // Creates a separator to the parent
    new Label(parent, SWT.SEPARATOR);

    this.label = new CLabel(parent, SWT.SHADOW_NONE);

    GC graphics = new GC(parent);
    graphics.setFont(parent.getFont());

    StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();

    FontMetrics fontMtric = graphics.getFontMetrics();
    statusLineLayoutData.widthHint = fontMtric.getAverageCharWidth() * STATUS_LBL_WIDTH_CHRS;
    statusLineLayoutData.heightHint = fontMtric.getHeight();

    // Dispose the graphics object after the usage
    graphics.dispose();

    this.label.setLayoutData(statusLineLayoutData);

    // ICDM-2565
    // Check whether the Connection state is Connected / Reconnected
    if (ConnectionState.isReconnectedFlag()) {
      setStatusDetails(ConnectionState.RECONNECTED);
    }
    else {
      setStatusDetails(ConnectionState.CONNECTED);
    }
  }

  /**
   * Update connection state with the given input
   *
   * @param state
   */
  void updateStatus(final ConnectionState state) {
    setStatusDetails(state);
    this.label.redraw();
  }

  /**
   * Set the connection status details to the status line label
   *
   * @param state ConnectionState
   */
  private void setStatusDetails(final ConnectionState state) {

    if (this.label == null) {
      // Status area not initialized yet
      return;
    }

    this.label.setText(state.getText());
    this.label.setToolTipText(state.getToolTip());

    // Set the image according to the connection status
    ImageKeys imageKey;
    switch (state) {
      case DISCONNECTED:
        imageKey = ImageKeys.DISCONNECTED_16X16;
        break;
      case RECONNECTING:
        imageKey = ImageKeys.RECONNECTING_16X16;
        break;
      case RECONN_INCOMPLETE:
        imageKey = ImageKeys.WARNING_16X16;
        break;
      default:
        imageKey = ImageKeys.CONNECTED_16X16;
        break;
    }

    // Set the image according to the connection status
    this.label.setImage(ImageManager.getInstance().getRegisteredImage(imageKey));

  }

}
