/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.connectionstate.ConnectionState;
import com.bosch.caltool.icdm.client.bo.connectionstate.IConnectionStatusListener;
import com.bosch.caltool.icdm.common.exception.GenericException;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Status line contribution of network connection state. Automatically gets updated by listening to connection poller
 *
 * @author bne4cob
 */
public class ConnectionStateStatusLineContributionListener implements IConnectionStatusListener {

  /**
   * {@inheritDoc}
   */
  @Override
  public void onConnectionChange(final ConnectionState state) throws GenericException {

    IWorkbenchWindow win = Arrays.stream(PlatformUI.getWorkbench().getWorkbenchWindows()).filter(Objects::nonNull)
        .findFirst().orElse(null);

    if (win == null) {
      return;
    }

    final IWorkbenchPart workbenchPart = win.getActivePage().getActivePart();
    if (workbenchPart instanceof IViewPart) {
      final IViewPart viewPart = (IViewPart) workbenchPart;
      IStatusLineManager statusLineManager = viewPart.getViewSite().getActionBars().getStatusLineManager();
      if (statusLineManager == null) {
        return;
      }
      ConnectionStateStatusLineContribution stateConribution =
          (ConnectionStateStatusLineContribution) statusLineManager.find(ConnectionStateStatusLineContribution.OBJ_ID);

      CDMLogger.getInstance().debug("Setting status line info for connection state - {}", state);

      // Update status line asynchronously
      Display.getDefault().asyncExec(() -> {
        stateConribution.updateStatus(state);
        CDMLogger.getInstance().debug("Setting status line info completed for connection state - {}", state);
      });
    }
  }

}
