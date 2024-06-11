/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal.servinterface;


/**
 * @author SSN9COB
 */
public interface SSDServiceMethod {

  /**
   * Get Current Method Name from where the logging is invoked
   *
   * @return Method Name
   */
  default String getCurrentMethodName() {
    return Thread.currentThread().getStackTrace()[2].getMethodName();
  }
}
