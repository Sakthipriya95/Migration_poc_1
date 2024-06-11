/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;


/**
 * @author bne4cob
 */
public final class TFConstants {

  /**
   * Default Input Type
   */
  public static final DATA_INPUT_TYPE DEF_INPUT_TYPE = DATA_INPUT_TYPE.XML;

  /**
   * @author bne4cob
   */
  public static enum DATA_INPUT_TYPE {
    /**
     * XML Input
     */
    XML,

    /**
     * CSV input
     */
    CSV;
  }

  /**
   * Private constructor
   */
  private TFConstants() {
    // Nothing to do
  }

}
