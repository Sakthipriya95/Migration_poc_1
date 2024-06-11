/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.serviceloader.report;

import com.bosch.caltool.icdm.rest.serviceloader.common.ServiceLoaderException;

/**
 * @author elm1cob
 */
public class ServiceReportException extends ServiceLoaderException {

  /**
  *
  */
  private static final long serialVersionUID = -5600589294816933272L;

  /**
   * @param message String
   * @param cause Throwable
   */
  public ServiceReportException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
