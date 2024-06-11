/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.db.connector;

import javax.persistence.EntityManager;

import com.bosch.ssd.api.jpa.activator.JPAActivator;

/**
 * @author vau3cob
 */
public class SSDDataBaseConnector {

  /**
   * To get the database connection
   * @return
   */
  public EntityManager createConnection() {
    JPAActivator jpa = new JPAActivator();
    return jpa.createConnection();
  }
}
