
/**
 * GetPidcWebFlowDataFault1Exception.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.2
 * Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class GetPidcWebFlowElementFaultException extends java.lang.Exception {

  private static final long serialVersionUID = 1539077648048L;

  private GetPidcWebFlowElementFault faultMessage;


  public GetPidcWebFlowElementFaultException() {
    super("GetPidcWebFlowDataFault1Exception");
  }

  public GetPidcWebFlowElementFaultException(final java.lang.String s) {
    super(s);
  }

  public GetPidcWebFlowElementFaultException(final java.lang.String s, final java.lang.Throwable ex) {
    super(s, ex);
  }

  public GetPidcWebFlowElementFaultException(final java.lang.Throwable cause) {
    super(cause);
  }


  public void setFaultMessage(final com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault msg) {
    this.faultMessage = msg;
  }

  public com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault getFaultMessage() {
    return this.faultMessage;
  }
}
