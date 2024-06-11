/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;


/**
 * Notification listener for data model changes
 * 
 * @author bne4cob
 */
public interface INotificationListener {

  /**
   * Perform the changes to be done in the UI, when a change notification is received.
   * 
   * @param event details of modified data
   */
  void onDataModelChange(DisplayChangeEvent event);

}
