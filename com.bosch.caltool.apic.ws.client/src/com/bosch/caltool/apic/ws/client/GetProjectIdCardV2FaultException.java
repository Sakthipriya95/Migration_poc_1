
/**
 * GetProjectIdCardV2FaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetProjectIdCardV2FaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943745L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardV2Fault faultMessage;

    
        public GetProjectIdCardV2FaultException() {
            super("GetProjectIdCardV2FaultException");
        }

        public GetProjectIdCardV2FaultException(java.lang.String s) {
           super(s);
        }

        public GetProjectIdCardV2FaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetProjectIdCardV2FaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardV2Fault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardV2Fault getFaultMessage(){
       return faultMessage;
    }
}
    