
/**
 * GetProjectIdCardFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetProjectIdCardFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1518698935484L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault faultMessage;

    
        public GetProjectIdCardFaultException() {
            super("GetProjectIdCardFaultException");
        }

        public GetProjectIdCardFaultException(java.lang.String s) {
           super(s);
        }

        public GetProjectIdCardFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetProjectIdCardFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault getFaultMessage(){
       return faultMessage;
    }
}
    