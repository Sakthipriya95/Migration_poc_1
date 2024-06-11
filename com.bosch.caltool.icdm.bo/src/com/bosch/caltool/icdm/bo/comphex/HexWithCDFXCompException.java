/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comphex;


/**
 * @author vau3cob
 */
public class HexWithCDFXCompException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   *
   */
  public HexWithCDFXCompException() {
    super();
  }

  /**
   * @param arg0
   */
  public HexWithCDFXCompException(final String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public HexWithCDFXCompException(final Throwable arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public HexWithCDFXCompException(final String arg0, final Throwable arg1) {
    super(arg0, arg1);
  }
}
