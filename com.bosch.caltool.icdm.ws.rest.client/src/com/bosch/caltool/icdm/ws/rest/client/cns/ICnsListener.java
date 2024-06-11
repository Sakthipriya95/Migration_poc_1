/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns;


/**
 * @author bne4cob
 */
public interface ICnsListener {

  /**
   * Actions to be done on receiving change notification
   *
   * @param dce DisplayChangeEvent
   */
  void onChangeNotification(DisplayChangeEvent dce);

}
