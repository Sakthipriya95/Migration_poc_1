/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;


/**
 * Cache to store the change notifications received from the database change event listener
 * 
 * @author bne4cob
 */
public final class ChangeNotificationCache {

  /**
   * Map of change events
   */
  private final ConcurrentMap<Long, ChangedData> changedDataMap = new ConcurrentHashMap<Long, ChangedData>();

  /**
   * Map of change events that are being processed. To be removed from the main list after processing.
   */
  private final ConcurrentMap<Long, ChangedData> usedChgDataMap = new ConcurrentHashMap<Long, ChangedData>();


  /**
   * Add new Changed Data to the cache. Access to this method is synchronized
   * 
   * @param changedData the changed data
   */
  public void addChangedData(final Map<Long, ChangedData> changedData) {
    synchronized (this) {
      this.changedDataMap.putAll(changedData);
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("CQN : Records added to change data cache. Count - {}; Details - {}", changedData.size(),
            changedData);
      }
    }
  }

  /**
   * Returns the collected change events as a new instance of DisplayChangeEvent. Access to this method is synchronized.
   * Invoking the method will always return a new instance of the <code>DisplayChangeEvent</code>.
   * 
   * @return the event list
   */
  public DisplayChangeEvent getDCE() {

    synchronized (this) {
      this.usedChgDataMap.clear();
      this.usedChgDataMap.putAll(this.changedDataMap);

      final DisplayChangeEvent displayEvent = new DisplayChangeEvent();
      displayEvent.getChangedData().putAll(this.usedChgDataMap);

      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("CQN : New display change event created : " + displayEvent.toString());
      }
      return displayEvent;
    }
  }

  /**
   * Remove the events retrieved by invoking <code>getDCE()</code> from this cache.
   */
  public void removeUsed() {
    synchronized (this) {
      for (Long key : this.usedChgDataMap.keySet()) {
        this.changedDataMap.remove(key);
      }
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("CQN : Removed processed records from change data cache");
      }
    }
  }


  /**
   * Easy method to get Logger for this class. Uses the object store
   * 
   * @return the logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }


}
