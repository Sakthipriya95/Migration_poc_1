/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;

/**
 * @author bne4cob
 */
public interface ICnsAsyncMessage extends Runnable {

  /**
   * @param event
   */
  void setChangeEvent(ChangeEvent event);

  /**
   * @param sessionId session ID
   */
  void setSessionId(String sessionId);

}
