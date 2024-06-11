/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process.listener;

/**
 * Constants that are shared between ICDMURIClient, ICDMURIServer and ICDMURIServerThread
 */
final class ListenerConstants {

  /**
   * Port Number used for TCP communication
   */
  final public static int PORT_NUMBER = 2008;

  /**
   * Send Action
   */
  public static final String SEND_REQUEST = "sendAction";

  /**
   * Private constructor
   */
  private ListenerConstants() {
    // Private constructor for constant definition class
  }
}
