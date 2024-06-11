/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;


/**
 * @author BNE4COB
 */
final class DMErrorCodes {

  /**
   * The error constant for checking the version of data being modified
   */
  public static final int ERR_STALE_DATA = -1001;

  /**
   * The error constant when object store initialisation is not done
   */
  public static final int OBJ_STOR_UNINIT = -1002;

  /**
   * Private constructor
   */
  private DMErrorCodes() {
    // Do nothing
  }

}
