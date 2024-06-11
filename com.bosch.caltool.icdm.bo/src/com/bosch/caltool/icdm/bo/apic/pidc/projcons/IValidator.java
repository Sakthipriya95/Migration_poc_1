/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.SortedSet;


/**
 * @author bne4cob
 * @param <R> Validation result type
 */
public interface IValidator<R extends IValidationResult> {

  /**
   * Run the validation
   */
  void validate();

  /**
   * Get the result of validation. To be invoked after executing validate()
   * 
   * @return sorted set of validation results
   */
  SortedSet<R> getResult();

}
