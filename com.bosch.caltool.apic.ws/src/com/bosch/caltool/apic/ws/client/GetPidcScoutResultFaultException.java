
/**
 * GetPidcScoutResultFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetPidcScoutResultFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1518698935345L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.PidcScoutFault faultMessage;

    
        public GetPidcScoutResultFaultException() {
            super("GetPidcScoutResultFaultException");
        }

        public GetPidcScoutResultFaultException(java.lang.String s) {
           super(s);
        }

        public GetPidcScoutResultFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetPidcScoutResultFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.PidcScoutFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.PidcScoutFault getFaultMessage(){
       return faultMessage;
    }
}
    