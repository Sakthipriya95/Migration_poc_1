
/**
 * LoginFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class LoginFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727527L;
    
    private com.bosch.caltool.apic.ws.LoginFault faultMessage;

    
        public LoginFaultException() {
            super("LoginFaultException");
        }

        public LoginFaultException(java.lang.String s) {
           super(s);
        }

        public LoginFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public LoginFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.LoginFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.LoginFault getFaultMessage(){
       return faultMessage;
    }
}
    