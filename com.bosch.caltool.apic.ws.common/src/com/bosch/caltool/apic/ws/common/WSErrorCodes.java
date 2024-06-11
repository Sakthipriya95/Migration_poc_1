/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.common;

/**
 * Error codes
 *
 * @author bne4cob
 */
public final class WSErrorCodes {

  /**
   * Private constructor for utility class
   */
  private WSErrorCodes() {
    // Private constructor
  }

  /**
   * Input data is stale
   */
  public static final String STALE_DATA = "STALE_DATA";

  /**
   * Data requested not found
   */
  public static final String DATA_NOT_FOUND = "DATA_NOT_FOUND";

  /**
   * Error from command
   */
  public static final String COMMAND_ERROR = "COMMAND_ERROR";

  /**
   * User not authorized
   */
  public static final String UNAUTHORIZED = "UNAUTHORIZED";

  /**
   * ICDM Error
   */
  public static final String ICDM_ERROR = "ICDM_ERR";

  /**
   * Internal server Errors
   */
  public static final String INT_SERVER_ERROR = "SERVER_ERROR";

  /**
   * Unable to connect to server
   */
  public static final String CONNECTION_FAILED = "CONNECTION_FAILED";

  /**
   * Error is at the client side
   */
  public static final String CLIENT_ERROR = "CLIENT_ERROR";

  /**
   * input in the request is invalid 400
   */
  public static final String BAD_REQUEST = "BAD_INPUT";
}
