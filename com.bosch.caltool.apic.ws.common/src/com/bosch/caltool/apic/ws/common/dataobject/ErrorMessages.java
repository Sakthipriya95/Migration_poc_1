/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.common.dataobject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Error messages
 *
 * @author bne4cob
 */
public class ErrorMessages implements Serializable {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = 3400729295704246413L;

  private Map<String, String> errors = new HashMap<>();

  /**
   * @return the errMessages
   */
  public Map<String, String> getErrors() {
    return this.errors;
  }

  /**
   * @param errMessages the errMessages to set
   */
  public void setErrors(final Map<String, String> errMessages) {
    this.errors = errMessages;
  }

}
