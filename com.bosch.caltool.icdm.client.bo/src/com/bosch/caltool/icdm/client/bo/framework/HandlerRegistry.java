/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author bne4cob
 */
public enum HandlerRegistry {
                             /**
                              * Singleton instance
                              */
                             INSTANCE;

  private final ConcurrentMap<IClientDataHandler, Object> singletonClientDataHandlerMap = new ConcurrentHashMap<>();

  /**
   * @param handler IClientDataHandler
   */
  public void registerSingletonDataHandler(final IClientDataHandler handler) {
    // Dummy value
    this.singletonClientDataHandlerMap.put(handler, Boolean.TRUE);
  }

  /**
   * @return set of IClientDataHandler
   */
  public Set<IClientDataHandler> getAllSingletonDataHandlerSet() {
    return new HashSet<>(this.singletonClientDataHandlerMap.keySet());
  }
}
