/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.connectionstate;

import com.bosch.caltool.icdm.common.exception.GenericException;


/**
 * Connection status listener interface. Implementations can listen to change in network connection status.
 * <p>
 * Use <code>ObjectStore.getInstance().addConnectionStatusListener(final IConnectionStatusListener listener)</code> to
 * register the listeners.
 * 
 * @author bne4cob
 */
public interface IConnectionStatusListener {

  /**
   * Actions to be done when connection status is changed
   * 
   * @param state ConnectionState
   * @throws GenericException if there is a failure in this action
   */
  void onConnectionChange(ConnectionState state) throws GenericException;

}
