/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;

/**
 * @author bne4cob
 */
public class ChangeHandler {

  /**
   * Extenstion point name
   */
  private static final String GUI_NOTIF_EXT_PT = "com.bosch.caltool.icdm.ws.rest.client.cns.listener";

  private static final ILoggerAdapter LOGGER = ClientConfiguration.getDefault().getLogger();

  /**
   * @param chgDataList list of change data
   */
  public void triggerLocalChangeEvent(final List<ChangeData<?>> chgDataList) {
    DisplayChangeEvent dce = (new DisplayEventCreator()).createLocalDce(chgDataList);
    notifyUI(dce);
  }

  /**
   * @param changeEventMap change event map
   */
  public void triggerRemoteChangeEvent(final Map<Long, ChangeEvent> changeEventMap) {
    DisplayChangeEvent dce = (new DisplayEventCreator()).createRemoteDce(changeEventMap);
    notifyUI(dce);
  }


  private void notifyUI(final DisplayChangeEvent dce) {

    if (!ClientConfiguration.getDefault().isCnsEnabled()) {
      LOGGER.debug("CNS is not enabled");
      return;
    }
    if (Platform.getExtensionRegistry() == null) {
      LOGGER.warn("CNS is enabled, but environment is not RCP client. Notifications cannot be sent.");
      // CNS notifiations are triggered only from RCP client
      return;
    }
    final IConfigurationElement[] configs =
        Platform.getExtensionRegistry().getConfigurationElementsFor(GUI_NOTIF_EXT_PT);

    ICnsListener listener;

    for (IConfigurationElement config : configs) {
      try {
        listener = (ICnsListener) config.createExecutableExtension("class");
        listener.onChangeNotification(dce);
      }
      catch (CoreException exp) {
        LOGGER.error("Failed to load extension point", exp);
      }

    }
  }

}
