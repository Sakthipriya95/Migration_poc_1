
/**
 * GetPidcVersionStatisticsFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetPidcVersionStatisticsFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943667L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetPidcVersionStatisticsFault faultMessage;

    
        public GetPidcVersionStatisticsFaultException() {
            super("GetPidcVersionStatisticsFaultException");
        }

        public GetPidcVersionStatisticsFaultException(java.lang.String s) {
           super(s);
        }

        public GetPidcVersionStatisticsFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetPidcVersionStatisticsFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetPidcVersionStatisticsFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetPidcVersionStatisticsFault getFaultMessage(){
       return faultMessage;
    }
}
    