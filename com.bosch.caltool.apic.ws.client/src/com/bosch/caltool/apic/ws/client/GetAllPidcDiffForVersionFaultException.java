
/**
 * GetAllPidcDiffForVersionFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetAllPidcDiffForVersionFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943651L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetAllPidcDiffForVersionFault faultMessage;

    
        public GetAllPidcDiffForVersionFaultException() {
            super("GetAllPidcDiffForVersionFaultException");
        }

        public GetAllPidcDiffForVersionFaultException(java.lang.String s) {
           super(s);
        }

        public GetAllPidcDiffForVersionFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetAllPidcDiffForVersionFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetAllPidcDiffForVersionFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetAllPidcDiffForVersionFault getFaultMessage(){
       return faultMessage;
    }
}
    