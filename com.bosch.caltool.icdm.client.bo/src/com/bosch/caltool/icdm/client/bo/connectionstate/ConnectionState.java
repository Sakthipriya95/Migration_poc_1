/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.connectionstate;

/**
 * Network Connection state
 *
 * @author bne4cob
 */
public enum ConnectionState {
                             /**
                              * CONNECTED
                              */
                             CONNECTED("Online", "The iCDM client is CONNECTED to the server"),
                             /**
                              * DISCONNECTED
                              */
                             DISCONNECTED("Disconnected", "The iCDM client is DISCONNECTED from the server"),
                             /**
                              * RECONNECTING
                              */
                             RECONNECTING(
                                          "Reconnecting...",
                                          "Reconnecting the iCDM client to the server. Please wait..."),
                             /**
                              * RECONNECTED
                              */
                             // ICDM-2565
                             RECONNECTED("Online", "The iCDM client is RECONNECTED to the server"),
                             /**
                              * RECONNECTION INCOMPLETE
                              */
                             RECONN_INCOMPLETE(
                                               "Reconnection incomplete",
                                               "FAILED to reconnect to the server properly. It is adviced to restart the iCDM client.");

  /**
   * State as a text
   */
  private final String text;

  /**
   * Tooltip of this state
   */
  private final String tooltip;

  /**
   * Flag to check whether the network is lost and reconnected
   */
  // ICDM-2565
  private static boolean reconnectedFlag;


  /**
   * @param reconnectedFlag the reconnectedFlag to set
   */
  public static void setReconnectedFlag(final boolean reconnectedFlag) {
    ConnectionState.reconnectedFlag = reconnectedFlag;
  }

  /**
   * @return the reconnectedFlag
   */
  public static boolean isReconnectedFlag() {
    return reconnectedFlag;
  }

  /**
   * Constructor
   *
   * @param text text
   * @param tooltip tooltip
   */
  ConnectionState(final String text, final String tooltip) {
    this.text = text;
    this.tooltip = tooltip;
  }

  /**
   * @return the text
   */
  public String getText() {
    return this.text;
  }

  /**
   * @return tooltip
   */
  public String getToolTip() {
    return this.tooltip;
  }
}