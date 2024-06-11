
/**
 * LoadA2LFileDataFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class LoadA2LFileDataFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727543L;
    
    private com.bosch.caltool.apic.ws.LoadA2LFileDataFault faultMessage;

    
        public LoadA2LFileDataFaultException() {
            super("LoadA2LFileDataFaultException");
        }

        public LoadA2LFileDataFaultException(java.lang.String s) {
           super(s);
        }

        public LoadA2LFileDataFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public LoadA2LFileDataFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.LoadA2LFileDataFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.LoadA2LFileDataFault getFaultMessage(){
       return faultMessage;
    }
}
    