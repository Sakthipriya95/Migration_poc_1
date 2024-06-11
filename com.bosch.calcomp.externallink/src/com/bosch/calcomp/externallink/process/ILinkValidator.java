/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;

/**
 * Additional validations if any on the link url
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkValidator {

  /**
   * @param linkText link text - url text
   * @throws ExternalLinkException if validation fails
   */
  void validate(String linkText) throws ExternalLinkException;
}
