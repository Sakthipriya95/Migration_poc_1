/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.wadl.parser;

import com.bosch.caltool.icdm.rest.serviceloader.common.ServiceLoaderException;

/**
 * @author bne4cob
 */
public class WadlParserException extends ServiceLoaderException {

  /**
   *
   */
  private static final long serialVersionUID = -3302513883826827576L;

  /**
   * @param message exception message
   * @param e exception
   */
  public WadlParserException(final String message, final Exception e) {
    super(message, e);
  }

}
