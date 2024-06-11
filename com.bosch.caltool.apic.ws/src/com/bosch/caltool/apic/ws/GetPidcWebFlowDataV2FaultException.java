
/**
 * GetPidcWebFlowDataV2FaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class GetPidcWebFlowDataV2FaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727543L;
    
    private com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault faultMessage;

    
        public GetPidcWebFlowDataV2FaultException() {
            super("GetPidcWebFlowDataV2FaultException");
        }

        public GetPidcWebFlowDataV2FaultException(java.lang.String s) {
           super(s);
        }

        public GetPidcWebFlowDataV2FaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetPidcWebFlowDataV2FaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault getFaultMessage(){
       return faultMessage;
    }
}
    