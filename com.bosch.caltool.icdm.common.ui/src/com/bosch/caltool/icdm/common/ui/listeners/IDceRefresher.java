/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * Common interface for all viewers and editors to enable DCN
 *
 * @author bne4cob
 */
public interface IDceRefresher {

  /**
   * Performs steps to refresh this UI component
   *
   * @param dce Display Change Event
   */
  void refreshUI(DisplayChangeEvent dce);

  /**
   * @return data handler of the refresher component
   */
  IClientDataHandler getDataHandler();

}
