/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

import com.bosch.caltool.dmframework.notification.GlobalCQNNotifier;


/**
 * @author hef2fe
 */
// Implelents callbale to check if dcn is completed in 30 seconds
public enum DatabaseUpdater {

                             /**
                              * Single instance
                              */
                             INSTANCE;


  /**
   * @return the theInstance
   */
  public static DatabaseUpdater getInstance() {
    return INSTANCE;
  }

  /**
   * if false, error has occured during database update
   */
  private boolean successful = true;


  /**
   * Refresh the data mocdel
   */
  public synchronized void refreshNotificationChanges() {
    GlobalCQNNotifier cqnNotifier = new GlobalCQNNotifier();

    cqnNotifier.refreshDataModel();
    if (!cqnNotifier.isSuccessful()) {
      this.successful = false;
    }
  }

  /**
   * Returns the status of database change refresh
   *
   * @return if false, error has occured during data model refresh
   */
  public synchronized boolean isSuccessful() {
    return this.successful;
  }


}
