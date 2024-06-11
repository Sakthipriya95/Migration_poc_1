
/**
 * GetAttrGroupsFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class GetAttrGroupsFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727574L;
    
    private com.bosch.caltool.apic.ws.GetAttrGroupsFault faultMessage;

    
        public GetAttrGroupsFaultException() {
            super("GetAttrGroupsFaultException");
        }

        public GetAttrGroupsFaultException(java.lang.String s) {
           super(s);
        }

        public GetAttrGroupsFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetAttrGroupsFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.GetAttrGroupsFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.GetAttrGroupsFault getFaultMessage(){
       return faultMessage;
    }
}
    