/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;


/**
 * Implementation should provide the necessarry details to refresh the data cache.
 * 
 * @author BNE4COB
 */
public interface ICacheRefresher {

  /**
   * Refresh the data cache of the data model for the given display change event
   * 
   * @param event event
   */
  void refreshDataCache(DisplayChangeEvent event);

}
