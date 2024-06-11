/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * @author bne4cob
 */
@FunctionalInterface
public interface ICnsRefresherDce {

  /**
   * refresh data based on DCE changes
   *
   * @param dce Display Change Event
   */
  void refresh(DisplayChangeEvent dce);
}
