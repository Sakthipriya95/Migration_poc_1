
/**
 * GetAllPidcDiffFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetAllPidcDiffFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943760L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffFault faultMessage;

    
        public GetAllPidcDiffFaultException() {
            super("GetAllPidcDiffFaultException");
        }

        public GetAllPidcDiffFaultException(java.lang.String s) {
           super(s);
        }

        public GetAllPidcDiffFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetAllPidcDiffFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffFault getFaultMessage(){
       return faultMessage;
    }
}
    