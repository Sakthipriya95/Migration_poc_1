/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;


/**
 * Exception when a duplicate element is found
 * 
 * @author bne4cob
 */
public class DuplicateElementException extends RuntimeException {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = -4329527630874373798L;

  /**
   * @param message error message
   */
  public DuplicateElementException(final String message) {
    super(message);
  }
}
