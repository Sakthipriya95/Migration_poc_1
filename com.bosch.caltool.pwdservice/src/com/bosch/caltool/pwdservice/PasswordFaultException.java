
/**
 * PasswordFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

package com.bosch.caltool.pwdservice;

public class PasswordFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1443618890097L;
    
    private com.bosch.caltool.pwdservice.PasswordFault faultMessage;

    
        public PasswordFaultException() {
            super("PasswordFaultException");
        }

        public PasswordFaultException(java.lang.String s) {
           super(s);
        }

        public PasswordFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public PasswordFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.pwdservice.PasswordFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.pwdservice.PasswordFault getFaultMessage(){
       return faultMessage;
    }
}
    