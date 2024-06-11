/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.vcdminterface;

import com.vector.easee.application.cdmservice.CDMWebServiceException;


/**
 * ICDM-1687 This is an exception rethrown from vcmdinterface plugin when an {@link CDMWebServiceException} occurs in
 * com.bosch.caltool.apic.vcdminterface.VCDMInterface.getParameterValues(String, String[])
 *
 * @author jvi6cob
 */
public class VCDMInterfaceException extends Exception {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = -8289113890914962983L;

  /**
   * @param message String
   * @param cause Throwable
   */
  public VCDMInterfaceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message String
   */
  public VCDMInterfaceException(final String message) {
    super(message);
  }

}
