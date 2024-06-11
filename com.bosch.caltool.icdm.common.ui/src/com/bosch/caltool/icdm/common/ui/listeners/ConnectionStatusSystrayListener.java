/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.connectionstate.ConnectionState;
import com.bosch.caltool.icdm.client.bo.connectionstate.IConnectionStatusListener;
import com.bosch.caltool.icdm.common.exception.GenericException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.SystemTray;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Listener for showing system tray notification during connection status change
 *
 * @author bne4cob
 */
public class ConnectionStatusSystrayListener implements IConnectionStatusListener {

  /**
   * Shows tooltip when connection state is changed. The tooltip text is defined in ConnectionState class
   * <p>
   * For disconnection warning tooltip <br>
   * For connection, info tooltip <br>
   * For incomplete reconnection, warning tooltip
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void onConnectionChange(final ConnectionState state) throws GenericException {
    // Show notifications based on preference setting
    IPreferenceStore preference = PlatformUI.getPreferenceStore();
    if (ApicConstants.CODE_NO.equals(preference.getString(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED)) &&
        (state != ConnectionState.RECONN_INCOMPLETE)) {
      // Hide notifications, for every status change except incomplete reconnection
      CDMLogger.getInstance().debug("Creating tooltip disabled in preferences");
      return;
    }

    // Nothing to be done for reconnecting state
    if (state == ConnectionState.RECONNECTING) {
      return;
    }

    CDMLogger.getInstance().debug("Creating tooltip for connection state - {}", state);

    // Show the tooltip. For incomplete reconnection, displays the message in adialog box also
    Display.getDefault().asyncExec(() -> {
      int tipStyle = SWT.ICON_WARNING;
      // ICDM-2565
      if ((state == ConnectionState.CONNECTED) || (state == ConnectionState.RECONNECTED)) {
        tipStyle = SWT.ICON_INFORMATION;
      }
      SystemTray.INSTANCE.showToolTip(state.getToolTip(), tipStyle);

      CDMLogger.getInstance().debug("Tooltip created successfully for connection state - {}", state);

    });
  }
}
