
/**
 * GetAttributeValuesFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetAttributeValuesFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1518698935507L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault faultMessage;

    
        public GetAttributeValuesFaultException() {
            super("GetAttributeValuesFaultException");
        }

        public GetAttributeValuesFaultException(java.lang.String s) {
           super(s);
        }

        public GetAttributeValuesFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetAttributeValuesFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.APICWsRequestFault getFaultMessage(){
       return faultMessage;
    }
}
    