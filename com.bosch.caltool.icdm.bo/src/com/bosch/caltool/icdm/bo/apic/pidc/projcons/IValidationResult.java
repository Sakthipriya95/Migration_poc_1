/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;


/**
 * Validation result
 * 
 * @author bne4cob
 */
public interface IValidationResult {

  /**
   * @return summary of the result
   */
  String getSummary();

  /**
   * @return type of error
   */
  ErrorType getErrorType();

  /**
   * @return Error level
   */
  IErrorLevel getErrorLevel();

}
