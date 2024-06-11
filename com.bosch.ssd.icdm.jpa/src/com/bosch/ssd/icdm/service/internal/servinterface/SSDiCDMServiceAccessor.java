/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal.servinterface;

import javax.persistence.EntityManager;

import com.bosch.ssd.icdm.service.SSDInterface;
import com.bosch.ssd.icdm.service.SSDService;

/**
 * @author SON9COB
 *
 */
public class SSDiCDMServiceAccessor {

  
  /**
   * Method to create SSD service everytime
   * 
   * @param em - entity manager to be used
   * @param usersName - user name who has logged in
   * @return - ssd service instance
   */
  public  SSDInterface createServiceInstance(final EntityManager em, final String usersName) {
       return new SSDService(usersName,em);
  }
}
