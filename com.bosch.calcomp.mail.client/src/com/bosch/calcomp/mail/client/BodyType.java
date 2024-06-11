/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client;


/**
 *
 */
public enum BodyType {
                      /**
                       * Body Content : text/plain
                       */
                      PLAIN_TEXT("text/plain"),
                      /**
                       * Body Content : text/html
                       */
                      HTML("text/html");

  private final String type;

  BodyType(final String type) {
    this.type = type;
  }


  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }


}
