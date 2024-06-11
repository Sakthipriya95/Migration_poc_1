/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws;


/**
 * @author svj7cob
 */
//ICDM-2400
public class GetPidcActiveVersionException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -1328712715764738764L;


  private com.bosch.caltool.apic.ws.APICWsRequestFault faultMessage;

  public GetPidcActiveVersionException() {
    super("GetPidcActiveVersionException");
  }

  public GetPidcActiveVersionException(final String str) {
    super(str);
  }

  public GetPidcActiveVersionException(final String str, final Throwable exp) {
    super(str, exp);
  }

  public GetPidcActiveVersionException(final Throwable cause) {
    super(cause);
  }

  public void setFaultMessage(final com.bosch.caltool.apic.ws.APICWsRequestFault msg) {
    this.faultMessage = msg;
  }

  public com.bosch.caltool.apic.ws.APICWsRequestFault getFaultMessage() {
    return this.faultMessage;
  }
}
