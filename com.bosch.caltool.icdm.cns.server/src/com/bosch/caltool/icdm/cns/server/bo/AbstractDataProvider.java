/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;


/**
 * @author BNE4COB
 */
public abstract class AbstractDataProvider {

  private final CnsServiceData serviceData;

  /**
   * @param serviceData Service Data
   */
  protected AbstractDataProvider(final CnsServiceData serviceData) {
    this.serviceData = serviceData;

  }

  /**
   * @return the Service Data
   */
  public CnsServiceData getServiceData() {
    return this.serviceData;
  }
}
